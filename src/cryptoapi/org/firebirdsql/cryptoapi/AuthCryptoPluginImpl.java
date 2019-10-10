package org.firebirdsql.cryptoapi;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import org.firebirdsql.cryptoapi.cryptopro.CertUtils;
import org.firebirdsql.cryptoapi.cryptopro.ContainerInfo;
import org.firebirdsql.cryptoapi.cryptopro.CryptoProProvider;
import org.firebirdsql.cryptoapi.cryptopro.RandomUtil;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.windows.Win32Api;
import org.firebirdsql.cryptoapi.windows.Wincrypt;
import org.firebirdsql.cryptoapi.windows.Winerror;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;
import org.firebirdsql.cryptoapi.windows.crypt32.Crypt32;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_PUBLIC_KEY_INFO;
import org.firebirdsql.cryptoapi.windows.crypt32._CRYPT_KEY_PROV_INFO;
import org.firebirdsql.cryptoapi.windows.sspi._ALG_ID;
import org.firebirdsql.gds.impl.wire.Bytes;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoException;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.gds.impl.wire.auth.AuthPrivateKeyContext;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import static org.firebirdsql.cryptoapi.windows.Wincrypt.*;
import static org.firebirdsql.cryptoapi.windows.advapi.Advapi.cryptSetKeyParam;

public class AuthCryptoPluginImpl extends AuthCryptoPlugin {
  private Pointer provider;
  private Pointer myStore;
  private String repositoryPin;

  public AuthCryptoPluginImpl() throws CryptoException {
    try {
      initializeProvider(CryptoProProvider.PROV_DEFAULT);
      myStore = Crypt32.certOpenSystemStore(null, "MY");
      repositoryPin = null;
    } catch (CryptoException e) {
      throw e;
    } catch (AuthCryptoException e) {
      throw new CryptoException(e);
    }
  }

  public void initializeProvider(final int providerType) throws AuthCryptoException {
    try {
      if (provider != null)
        Advapi.cryptReleaseContext(provider);
      provider = Advapi.cryptAcquireContext(null, null, providerType, Wincrypt.CRYPT_VERIFYCONTEXT);
    } catch (CryptoException e) {
      throw new AuthCryptoException(e);
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
      final Pointer cert = CertUtils.findCertificate(myStore, certContext);
      AuthPrivateKeyContext keyContext = null;
      if (cert != null) {
        try {
          keyContext = getUserKey(cert);
        } finally {
          Crypt32.certFreeCertificateContext(cert);
        }
      }
      if (keyContext != null)
        return keyContext;
      // try to find in containers
      keyContext = findCertInContainers(certBase64);
      if (keyContext == null && cert == null)
        throw new AuthCryptoException("Can't find certificate in personal store");
      if (cert == null) {
        try {
          final String container = Advapi.getContainerName((Pointer) keyContext.getProvHandle());
          final byte[] provParam = Advapi.cryptGetProvParam((Pointer) keyContext.getProvHandle(), PP_PROVTYPE, 0);
          final int provType = Win32Api.byteArrayToInt(provParam);
          CertUtils.setCertificateContainerNameParam(certContext, container, provType);
          Crypt32.certAddCertificateContextToStore(myStore, certContext.getPointer());
        } catch (CryptoException e) {
          throw new AuthCryptoException("Can't add certificate to store", e);
        }
      }
      return keyContext;
    } finally {
      Crypt32.certFreeCertificateContext(certContext.getPointer());
    }
  }

  protected AuthPrivateKeyContext getUserKey(Pointer cert) throws AuthCryptoException {
    Pointer provHandle = null;
    final Pointer prov;
    try {
      prov = Crypt32.certGetCertificateContextProperty(cert, CERT_KEY_PROV_INFO_PROP_ID);
      _CRYPT_KEY_PROV_INFO info = new _CRYPT_KEY_PROV_INFO(prov);
      final WString containerName = info.pwszContainerName;
      if (containerName == null)
        return null;
      provHandle = Advapi.cryptAcquireContext(containerName.toString(), null, info.dwProvType/*CRYPTO_PROVIDER*/,0);
      final Pointer keyHandle = Advapi.cryptGetUserKey(provHandle, info.dwKeySpec);
      if (keyHandle != null)
        return new AuthPrivateKeyContext(provHandle, keyHandle);
      else
        Advapi.cryptReleaseContext(provHandle);
    } catch (CryptoException e) {
      freeProviderContext(provHandle);
      throw new AuthCryptoException("Can't get exchange private key for user certificate", e);
    }
    finally {
//      freeProviderContext(provHandle);
    }
    return null;
  }

  private AuthPrivateKeyContext findCertInContainers(String certBase64) throws AuthCryptoException {
    try {
      final Pointer hProv = CryptoProProvider.acquireContext();
      final List<ContainerInfo> certs;
      try {
        certs = CertUtils.getAvailableContainersCertificatesList(hProv);
      } finally {
        Advapi.cryptReleaseContext(hProv);
      }
      final byte[] certData = CertUtils.decode(certBase64);
      for (ContainerInfo cert : certs) {
        if (Arrays.equals(cert.certData, certData)) {
          final Pointer keyProv = Advapi.cryptAcquireContext(cert.containerName, null, cert.provType, 0);
          try {
            final Pointer hKey = Advapi.cryptGetUserKey(keyProv, Wincrypt.AT_KEYEXCHANGE);
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
    try {
      final int algID = CertUtils.getAlgorithmIDByProvider(provider);
      final Pointer hashHandle = Advapi.cryptCreateHash(provider, algID);
      if (!Advapi.cryptHashData(hashHandle, data, 0))
        throw new AuthCryptoException("Error hashing data.");
      return hashHandle;
    } catch (Exception e) {
      throw new AuthCryptoException("Error hashing data.");
    }
  }

  @Override
  public boolean destroyHash(final Object hashHandle) {
    return Advapi.cryptDestroyHash((Pointer) hashHandle);
  }

  @Override
  public byte[] hashData(final byte[] data, final int hashingCount, int hashMethod) throws AuthCryptoException {
    try {
      return Advapi.hashData(provider, data, hashMethod, hashingCount);
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
      Pointer keyHandle = Advapi.cryptImportKey((Pointer) userKey.getProvHandle(), publicKeyData.bytes(), exchKey, 0);
      if (keyHandle == null) {
        int error = Advapi.getLastError();
        if (error == Winerror.CRYPT_BAD_DATA) {
          // Установка параметра PRO_EXPORT - с сервером 2.6 без него не работает (((
          final _ALG_ID.ALG_ID alg = new _ALG_ID.ALG_ID();
          alg.value = CALG_PRO_EXPORT;
          alg.write();
          cryptSetKeyParam(exchKey, KP_ALGID, alg.getPointer(), 0);

          keyHandle = Advapi.cryptImportKey((Pointer) userKey.getProvHandle(), publicKeyData.bytes(), exchKey, 0);

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
  public byte[] ccfiDecrypt(final AuthPrivateKeyContext userKey, byte[] data, String certBase64) throws AuthCryptoException {
    try {
      if (repositoryPin != null)
        Advapi.setPin((Pointer)userKey.getProvHandle(), repositoryPin);
      return Crypt32.cryptDecryptMessage(myStore, data);
    } catch (Exception e) {
      throw new AuthCryptoException("Can't decrypt message.", e);
    }
  }

  @Override
  public byte[] ccfiSign(final AuthPrivateKeyContext userKey, byte[] data, String certBase64, final int keySpec) throws AuthCryptoException {
    Pointer p = null;
    try {
      p = (Pointer)userKey.getProvHandle();
      final int algID = CertUtils.getAlgorithmIDByProvider(p);
      final Pointer hashHandle = Advapi.cryptCreateHash(p, algID);
//      Advapi.cryptGetHashParam(hashHandle, 0x000a, data);
      Advapi.cryptHashData(hashHandle, data, 0);
      Advapi.cryptGetHashParam(hashHandle, 0x000a, data);

      byte[] res = null;
      if (keySpec != 0) {
        res = Advapi.cryptSignHash(hashHandle, keySpec, 0);
      } else {
        try {
          res = Advapi.cryptSignHash(hashHandle, AT_SIGNATURE, 0);
        } catch (CryptoException e) {
          // todo if NTE_BAD_KEYS
          res = Advapi.cryptSignHash(hashHandle, AT_KEYEXCHANGE, 0);
        }
      }
      return res;
    } catch (Exception e) {
      throw new AuthCryptoException("Can't sign data.", e);
    }
  }

  @Override
  public void setRepositoryPin(String pin) {
    this.repositoryPin = pin;
  }

  @Override
  public byte[] generateRandom(Object provHandle, int size) throws AuthCryptoException {
    byte[] res = null;
    try {
      if (provHandle == null)
        res = RandomUtil.createRandomBuffer(size).array();
      else
        res = RandomUtil.createRandomBuffer((Pointer)provHandle, size).array();
    } catch (CryptoException e) {
      throw new AuthCryptoException("Can't create random number.", e);
    }
    return res;
  }

  @Override
  public boolean verifySign(byte[]data, byte[] serverPublicCert, byte[] signedNumber) throws AuthCryptoException {
    boolean result = false;
    try {
      String publicCert = new String(serverPublicCert);
      byte[] cert = CertUtils.decode(publicCert);
      X509Certificate xCert = null;
      try {
        xCert = CertUtils.generateCertificate(cert);
      } catch (CertificateException e) {
        throw new CryptoException("Can't acquire public key.", Advapi.getLastError());
      }
      final Pointer provHandle = Advapi.cryptAcquireContext(null, null, CryptoProProvider.PROV_DEFAULT, Wincrypt.CRYPT_VERIFYCONTEXT);
      final _CERT_CONTEXT.PCCERT_CONTEXT context = Crypt32.certCreateCertificateContext(X509_ASN_ENCODING | PKCS_7_ASN_ENCODING, xCert.getEncoded());
      if (context == null) {
        final int error = Advapi.getLastError();
        throw new CryptoException("Import certificate failed.", error);
      }
      try {
        final Pointer keyHandle = Crypt32.cryptImportPublicKeyInfo(provHandle, X509_ASN_ENCODING | PKCS_7_ASN_ENCODING, new _CERT_PUBLIC_KEY_INFO.PCERT_PUBLIC_KEY_INFO(context.pCertInfo.SubjectPublicKeyInfo.getPointer()));
        if (keyHandle == null) {
          final int error = Advapi.getLastError();
          throw new CryptoException("Import public key failed.", error);
        }
        try {
          final int algID = CertUtils.getAlgorithmIDByContext(context);
          // Acquire a hash object handle.
          final Pointer hashHandle = Advapi.cryptCreateHash(provHandle, algID);
          if (hashHandle == null)
            throw new CryptoException("Error acquiring digest handle. Error code: " + Advapi.getLastError());
          try {
            int res1 = Advapi.cryptGetHashParam(hashHandle, 0x000a, null);
            byte[] byteRes1 = new byte[res1];
            int res2 = Advapi.cryptGetHashParam(hashHandle, 0x000a, byteRes1);
            Advapi.cryptHashData(hashHandle, data, 0);
            // Verify the signature
            result = Advapi.cryptVerifySignature(hashHandle, signedNumber, keyHandle, 0);
            if (!result)
              throw new CryptoException("Can't verify signature.", Advapi.getLastError());
          } finally {
            Advapi.cryptDestroyHash(hashHandle);
          }
        } finally {
          Advapi.cryptDestroyKey(keyHandle);
        }
      } finally {
        Crypt32.certFreeCertificateContext(context.getPointer());
        if (!Advapi.cryptReleaseContext(provHandle))
          throw new CryptoException("Error destroying provider context.");
      }
    } catch (CryptoException e) {
      throw new AuthCryptoException("Can't verify signature.", e);
    } catch (CertificateEncodingException e) {
      throw new AuthCryptoException("Can't acquire public key.", e);
    }
    return result;
  }
}
