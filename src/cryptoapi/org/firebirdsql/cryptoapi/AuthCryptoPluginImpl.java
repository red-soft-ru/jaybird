package org.firebirdsql.cryptoapi;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.firebirdsql.gds.impl.wire.Bytes;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoException;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.gds.impl.wire.auth.AuthPrivateKeyContext;
import org.firebirdsql.cryptoapi.windows.Winerror;
import org.firebirdsql.cryptoapi.cryptopro.CertUtils;
import org.firebirdsql.cryptoapi.cryptopro.ContainerInfo;
import org.firebirdsql.cryptoapi.cryptopro.CryptoProProvider;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.windows.Wincrypt;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;
import org.firebirdsql.cryptoapi.windows.crypt32.Crypt32;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.sspi._ALG_ID;

import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import static org.firebirdsql.cryptoapi.windows.Wincrypt.*;
import static org.firebirdsql.cryptoapi.windows.advapi.Advapi.cryptSetKeyParam;


public class AuthCryptoPluginImpl extends AuthCryptoPlugin {
  private final Pointer provider;
  private final Pointer myStore;

  public AuthCryptoPluginImpl() throws CryptoException {
    try {
      provider = CryptoProProvider.acquireContext();
      myStore = Crypt32.certOpenSystemStore(null, "MY");
    } catch (CryptoException e) {
      throw e;
    }
  }

  @Override
  public AuthPrivateKeyContext getUserKey(final String certBase64) throws AuthCryptoException {
    final _CERT_CONTEXT.PCCERT_CONTEXT certContext;
    try {
      certContext = CertUtils.getCertContext(certBase64);
    } catch (CryptoException e) {
      throw new AuthCryptoException(e);
    }
    try {
      // поищем сначала в контейнерах
      final AuthPrivateKeyContext keyContext = findCertInContainers(certBase64);
      if (keyContext != null)
        return keyContext;
      // на Linux мы должны сами поискать сертификат в хранилище, так как КриптоПро не ищет.
      final _CERT_CONTEXT.PCCERT_CONTEXT cert = CertUtils.findCertificate(myStore, certContext);
      if (cert == null)
        throw new AuthCryptoException("Can't find certificate in personal store");
      try {
        return getUserKey(cert);
      } finally {
        Crypt32.certFreeCertificateContext(cert.getPointer());
      }
    } finally {
      Crypt32.certFreeCertificateContext(certContext.getPointer());
    }
  }

  private AuthPrivateKeyContext findCertInContainers(String certBase64) throws AuthCryptoException {
    try {
      final Pointer hProv = CryptoProProvider.acquireContext();
      final List<ContainerInfo> certs = CertUtils.getAvailableContainersCertificatesList(hProv);
      final byte[] certData = CertUtils.decode(certBase64);
      for (ContainerInfo cert : certs) {
        if (Arrays.equals(cert.certData, certData)) {
          final Pointer keyProv = Advapi.cryptAcquireContext(cert.containerName, null, CryptoProProvider.PROV_DEFAULT, 0);
          try {
            final Pointer hKey = Advapi.cryptGetUserKey(keyProv, AT_KEYEXCHANGE);
            return new AuthPrivateKeyContext(keyProv, hKey);
          } catch (CryptoException e) {
            Advapi.cryptReleaseContext(keyProv);
            throw e;
          }
        }
      }
      return null;
    } catch (CryptoException e) {
      throw new AuthCryptoException(e);
    } catch (CertificateException e) {
      throw new AuthCryptoException(e);
    }
  }

  protected AuthPrivateKeyContext getUserKey(_CERT_CONTEXT.PCCERT_CONTEXT cert) throws AuthCryptoException {
    final PointerByReference provHandle = new PointerByReference();
    final IntByReference keySpec = new IntByReference();
    final IntByReference callerFreeProv = new IntByReference();
    if (!Crypt32.cryptAcquireCertificatePrivateKey(cert, 0, provHandle, keySpec, callerFreeProv))
      throw new AuthCryptoException("Can't get private key for user certificate");

    try {
      final Pointer keyHandle = Advapi.cryptGetUserKey(provHandle.getValue(), keySpec.getValue());
      return new AuthPrivateKeyContext(provHandle.getValue(), keyHandle);
    } catch (CryptoException e) {
      freeProviderContext(provHandle);
      throw new AuthCryptoException("Can't get exchange private key for user certificate", e);
    }
  }

  @Override
  public void setIV(final Object keyHandle, final Bytes iVdata) throws AuthCryptoException {
    if (!cryptSetKeyParam((Pointer) keyHandle, Wincrypt.KP_IV, iVdata.bytes(), 0))
      throw new AuthCryptoException("Can't set initialization vector for key.");
  }

  @Override
  public byte[] getIV(final Object keyHandle) throws AuthCryptoException {
    try {
      return Advapi.cryptGetKeyParam((Pointer) keyHandle, Wincrypt.KP_IV);
    } catch (Exception e) {
      throw new AuthCryptoException("Can't get initialization vector for key.");
    }
  }


  @Override
  public void freeKeyHandle(final Object keyHandle) {
    Advapi.cryptDestroyKey((Pointer) keyHandle);
  }

  @Override
  public byte[] encrypt(final Object keyHandle, final byte[] data) throws AuthCryptoException {
    try {
      return Advapi.cryptEncrypt((Pointer) keyHandle, null, true, data);
    } catch (Exception e) {
      throw new AuthCryptoException("Can't create session key.", e);
    }
  }

  @Override
  public Object createHash(final byte[] data) throws AuthCryptoException {
    final Pointer hashHandle = Advapi.cryptCreateHash(provider, Wincrypt.CALG_GR3411);
    if (!Advapi.cryptHashData(hashHandle, data, 0))
      throw new AuthCryptoException("Error hashing data.");
    return hashHandle;
  }

  @Override
  public boolean destroyHash(final Object hashHandle) {
    return Advapi.cryptDestroyHash((Pointer) hashHandle);
  }

  @Override
  public byte[] hashData(final byte[] data, final int hashingCount) throws AuthCryptoException {
    try {
      return Advapi.hashData(provider, data, Wincrypt.CALG_GR3411, hashingCount);
    } catch (CryptoException e) {
      throw new AuthCryptoException(e);
    }
  }

  @Override
  public Object deriveKey(final Object hashHandle, final boolean exportable) throws AuthCryptoException {
    try {
      return Advapi.cryptDeriveKey(provider, Wincrypt.CALG_G28147, (Pointer) hashHandle, exportable ? Wincrypt.CRYPT_EXPORTABLE : 0);
    } catch (Exception e) {
      throw new AuthCryptoException("Can't create session key.", e);
    }
  }

  @Override
  public void freeProviderContext(final Object provHandle) {
    if (provHandle != null)
      Advapi.cryptReleaseContext((Pointer) provHandle);
  }

  @Override
  public Object getSessionPublicKey(final Bytes publicKeyData, final Bytes exchangeKeyData, final AuthPrivateKeyContext userKey)
      throws AuthCryptoException {
    final Pointer exchKey = Advapi.cryptImportKey((Pointer) userKey.getProvHandle(), exchangeKeyData.bytes(), (Pointer) userKey.getKeyHandle(), 0);
    if (exchKey == null)
      throw new AuthCryptoException("Can't import public key.");
    try {
      Pointer keyHandle = Advapi.cryptImportKey(provider, publicKeyData.bytes(), exchKey, 0);
      if (keyHandle == null) {
        int error = Advapi.getLastError();
        if (error == Winerror.CRYPT_BAD_DATA) {
          // Установка параметра PRO_EXPORT - с сервером 2.6 без него не работает (((
          final _ALG_ID.ALG_ID alg = new _ALG_ID.ALG_ID();
          alg.value = CALG_PRO_EXPORT;
          alg.write();
          cryptSetKeyParam(exchKey, KP_ALGID, alg.getPointer(), 0);

          keyHandle = Advapi.cryptImportKey(provider, publicKeyData.bytes(), exchKey, 0);

          if (keyHandle == null)
            throw new AuthCryptoException("Can't import public key.");
        } else
          throw new AuthCryptoException("Can't import public key.");
      }
      return keyHandle;
    } finally {
      freeKeyHandle(exchKey);
    }
  }

  @Override
  public byte[] decrypt(final Object keyHandle, final byte[] data) throws AuthCryptoException {
    try {
      return Advapi.cryptDecrypt((Pointer) keyHandle, null, true, 0, data);
    } catch (Exception e) {
      throw new AuthCryptoException("Can't create session key.", e);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (provider != null)
      Advapi.cryptReleaseContext(provider);
    if (myStore != null)
      Crypt32.certCloseStore(myStore);
    super.finalize();
  }

  @Override
  public byte[] ccfiEncrypt(byte[] data) throws AuthCryptoException {
    return new byte[0];
  }

  @Override
  public byte[] ccfiDecrypt(byte[] data) throws AuthCryptoException {
    try {
      return Advapi.cryptDecryptMessage(myStore, data);
    } catch (Exception e) {
      throw new AuthCryptoException("Can't decrypt message.", e);
    }
  }

  @Override
  public byte[] ccfiSign(byte[] data, String certBase64) throws AuthCryptoException {
    try {
      AuthPrivateKeyContext cont = getUserKey(certBase64);
      Pointer p = (Pointer)cont.getProvHandle();
      final Pointer hashHandle = Advapi.cryptCreateHash(p, Wincrypt.CALG_GR3411);
//      Advapi.cryptGetHashParam(hashHandle, 0x000a, data);
      Advapi.cryptHashData(hashHandle, data, 0);
      Advapi.cryptGetHashParam(hashHandle, 0x000a, data);

      byte[] res = null;
      try {
        res = Advapi.cryptSignHash(hashHandle, AT_SIGNATURE, 0);
      } catch (CryptoException e) {
        // todo if NTE_BAD_KEYS
        res = Advapi.cryptSignHash(hashHandle, AT_KEYEXCHANGE, 0);
      }
      return res;
    } catch (Exception e) {
      throw new AuthCryptoException("Can't sign data.", e);
    }
  }
}
