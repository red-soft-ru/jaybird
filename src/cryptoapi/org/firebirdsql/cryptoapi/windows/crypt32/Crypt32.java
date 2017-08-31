package org.firebirdsql.cryptoapi.windows.crypt32;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.log4j.Logger;
import org.firebirdsql.cryptoapi.windows.CryptoUtil;
import org.firebirdsql.cryptoapi.windows.JnaUtils;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.windows.Win32Api;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;

import java.util.Arrays;

import static com.sun.jna.Platform.isWindows;
import static org.firebirdsql.cryptoapi.windows.Wincrypt.PKCS_7_ASN_ENCODING;
import static org.firebirdsql.cryptoapi.windows.Wincrypt.X509_ASN_ENCODING;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 20:27:58
 */
@SuppressWarnings("UnusedDeclaration")
public class Crypt32 {

  private static Logger LOG = Logger.getLogger(Crypt32.class);
  private static final boolean LOGGING = false;

  private static final String CRYPT_LIB_NAME = Platform.isWindows() ? "Crypt32" : "capi20";
  public final static Crypt32Lib lib;

  private static String toString(Object p) {
    try {
      return p == null ? "(null)" : "" + p;
    } catch (Exception e) {
      return "(err)";
    }
  }

  static {
    /*
    try {
      FileInputStream f = new FileInputStream("/usr/lib/libcapi20.so");
    } catch (IOException e) {
      LOG.fatal(e);
    }
    */
    LOG.debug("Initializing Crypt32 API library");
    JnaUtils.init();
    try {
      lib = (Crypt32Lib) Native.loadLibrary(CRYPT_LIB_NAME, Crypt32Lib.class);
      LOG.debug(lib.toString());
    } catch (Throwable e) {
      LOG.error("Crypt32 API Initialization failed", e);
      throw new RuntimeException(e);
    }
  }

  public static Pointer certOpenSystemStore(Pointer provHandle, String subsystemProtocol)
      throws CryptoException {
    if (LOGGING)
      LOG.debug("certOpenSystemStore " + provHandle + " " + subsystemProtocol);
    Pointer res = isWindows() ? lib.CertOpenSystemStoreA(provHandle, subsystemProtocol) : lib.CertOpenSystemStore(provHandle, subsystemProtocol);
    if (res == Pointer.NULL)
      throw new CryptoException("Unable to open system store " + subsystemProtocol + ".", Advapi.getLastError());
    if (LOGGING)
      LOG.debug("certOpenSystemStore " + res);
    return res;
  }

  public static boolean certCloseStore(Pointer certStoreHandle) {
    if (LOGGING)
      LOG.debug("certCloseStore " + certStoreHandle);
    boolean res = lib.CertCloseStore(certStoreHandle, 0);
    if (LOGGING)
      LOG.debug("certCloseStore " + res);
    return res;
  }

  public static _CERT_CONTEXT.PCCERT_CONTEXT certEnumCertificatesInStore(Pointer certStoreHandle, _CERT_CONTEXT.PCERT_CONTEXT prevCertContext) {
    final Pointer p = prevCertContext == null ? null : prevCertContext.getPointer();
    return lib.CertEnumCertificatesInStore(certStoreHandle, p);
  }

  public static _CERT_CONTEXT.PCCERT_CONTEXT certFindCertificateInStore(Pointer certStoreHandle, int encodingType, int findFlags,
                                                                        int findType, Pointer findPara, _CERT_CONTEXT.PCERT_CONTEXT prevCertContext) {
    if (LOGGING)
      LOG.debug("certFindCertificateInStore " + certStoreHandle + " " + encodingType + " " + findFlags + " " + findType + " " + findPara + " " + toString(prevCertContext));
    _CERT_CONTEXT.PCCERT_CONTEXT res = lib.CertFindCertificateInStore(certStoreHandle, encodingType, findFlags, findType, findPara, prevCertContext);
    if (LOGGING)
      LOG.debug("certFindCertificateInStore " + toString(res));
    return res;
  }

  public static boolean cryptAcquireCertificatePrivateKey(_CERT_CONTEXT.PCERT_CONTEXT pCert, int dwFlags,
                                                          PointerByReference phCryptProvOrNCryptKey,
                                                          IntByReference pdwKeySpec, IntByReference pfCallerFreeProvOrNCryptKey) {
    if (LOGGING)
      LOG.debug("cryptAcquireCertificatePrivateKey " + toString(pCert) + " " + dwFlags + " " + phCryptProvOrNCryptKey + " " + pdwKeySpec + " " + pfCallerFreeProvOrNCryptKey);
    boolean res = lib.CryptAcquireCertificatePrivateKey(pCert, dwFlags, null, phCryptProvOrNCryptKey, pdwKeySpec, pfCallerFreeProvOrNCryptKey);
    if (LOGGING)
      LOG.debug("cryptAcquireCertificatePrivateKey " + res);
    return res;
  }

  public static _CERT_CONTEXT.PCCERT_CONTEXT certCreateCertificateContext(int certEncodingType, byte[] certEncoded) throws CryptoException {
    if (LOGGING)
      LOG.debug("certCreateCertificateContext " + certEncodingType + " " + Arrays.toString(certEncoded));
    _CERT_CONTEXT.PCCERT_CONTEXT res = lib.CertCreateCertificateContext(certEncodingType, certEncoded, certEncoded.length);
    if (res == null)
      throw new CryptoException("Can't decode certificate from byte buffer.", Advapi.getLastError());
    if (LOGGING)
      LOG.debug("certCreateCertificateContext " + toString(res));
    return res;
  }

  public static boolean certFreeCertificateContext(Pointer certContext) {
    if (!lib.CertFreeCertificateContext(certContext)) {
      LOG.error("Error releasing certificate context", new CryptoException("Error releasing certificate context"));
      return false;
    }
    return true;
  }

  public static byte[] cryptDecryptMessage(
      Pointer certStore,
      byte[] pbData
  ) throws CryptoException {
    final _CRYPT_DECRYPT_MESSAGE_PARA.PCRYPT_DECRYPT_MESSAGE_PARA decryptPara = new _CRYPT_DECRYPT_MESSAGE_PARA.PCRYPT_DECRYPT_MESSAGE_PARA();
    decryptPara.cbSize = decryptPara.size();
    decryptPara.dwMsgAndCertEncodingType = X509_ASN_ENCODING | PKCS_7_ASN_ENCODING;
    decryptPara.cCertStore = 1;
    PointerByReference p = new PointerByReference(certStore);
    decryptPara.rghCertStore = p;

    IntByReference pdwDataLen = new IntByReference(pbData.length);

    if (!lib.CryptDecryptMessage(decryptPara, pbData, pbData.length, null, pdwDataLen, null))
      throw CryptoUtil.raiseCryptoError("CryptDecryptMessage - initialization", Advapi.getLastError());

    final byte[] data = new byte[Math.max(pbData.length, pdwDataLen.getValue())];
    System.arraycopy(pbData, 0, data, 0, pbData.length);

    final int ciferSize = pdwDataLen.getValue();
    pdwDataLen.setValue(pbData.length);
    if (!lib.CryptDecryptMessage(decryptPara, pbData, pbData.length, data, pdwDataLen, null))
      throw CryptoUtil.raiseCryptoError("CryptDecryptMessage", Advapi.getLastError());

    return Win32Api.getActualData(data, pdwDataLen.getValue());
  }
}
