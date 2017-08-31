package org.firebirdsql.cryptoapi.windows.advapi;

import org.firebirdsql.cryptoapi.windows.JnaUtils;
import org.firebirdsql.cryptoapi.windows.Win32Api;
import org.firebirdsql.cryptoapi.windows.Winerror;
import org.firebirdsql.cryptoapi.windows.CryptoUtil;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.firebirdsql.cryptoapi.windows.ErrorMessages;
import org.firebirdsql.cryptoapi.windows.Wincrypt;
import org.firebirdsql.cryptoapi.windows.crypt32._CRYPT_DECRYPT_MESSAGE_PARA;

import static org.firebirdsql.cryptoapi.windows.Wincrypt.CRYPT_FIRST;
import static org.firebirdsql.cryptoapi.windows.Wincrypt.PKCS_7_ASN_ENCODING;
import static org.firebirdsql.cryptoapi.windows.Wincrypt.X509_ASN_ENCODING;


public class Advapi {
  private static Logger LOG = Logger.getLogger(Advapi.class);

  private static final String ADVAPI_LIB_NAME = Platform.isWindows() ?"advapi32" : "capi20";
  private final static AdvapiLib lib;
  private static final boolean LOGGING = false;

  static {
    JnaUtils.init();
    if (LOGGING)
      LOG.debug("static");
    try {
      lib = (AdvapiLib)Native.loadLibrary(ADVAPI_LIB_NAME, AdvapiLib.class);
    } catch (Exception e) {
      LOG.error("Advapi Initialization failed", e);
      throw new RuntimeException(e);
    }
    //Native.setProtected(true);
    if (LOGGING)
      LOG.debug("static " + lib);
  }

  /**
   * synchronized - из-за глюков в линуксе
   * http://www.cryptopro.ru/forum2/default.aspx?g=posts&m=45216#post45216
   * update:
   * TODO убрать после перехода на новые версии pcsc-lite - в 1.6 проблема решена. Протестировано на pcsc-lite 1.8.13 и ccid 1.4.18.
*/
  public synchronized static Pointer cryptAcquireContext(String keyContainer, String provider, int provType, int flags)
      throws CryptoException {
    if (LOGGING)
      LOG.debug("cryptAcquireContext " + keyContainer + " " + provider + " " + provType + " " + flags);
    final PointerByReference phProv = new PointerByReference();
    if (!lib.CryptAcquireContextA(phProv, keyContainer, provider, provType, flags))
      throw CryptoUtil.raiseCryptoError("cryptAcquireContext - init", getLastError());
    if (LOGGING)
      LOG.debug("cryptAcquireContext " + phProv.getValue());
    return phProv.getValue();
  }

  public static boolean cryptReleaseContext(Pointer provHandle) {
    if (LOGGING)
      LOG.debug("cryptReleaseContext " + provHandle);
    boolean res = lib.CryptReleaseContext(provHandle, 0);
    if (LOGGING)
      LOG.debug("cryptReleaseContext " + res);
    return res;
  }

  public static Pointer cryptCreateHash(Pointer provHandle, int algId) {
    if (LOGGING)
      LOG.debug("cryptCreateHash " + provHandle + " " + algId);
    final PointerByReference hash = new PointerByReference();
    Pointer res = lib.CryptCreateHash(provHandle, algId, null, 0, hash) ? hash.getValue() : null;
    if (LOGGING)
      LOG.debug("cryptCreateHash " + res);
    return res;
  }

  public static boolean cryptDestroyHash(Pointer hashHandle) {
    if (LOGGING)
      LOG.debug("cryptDestroyHash " + hashHandle);
    boolean res = lib.CryptDestroyHash(hashHandle);
    if (!res) {
      final int lastError = getLastError();
      final String message = ErrorMessages.getMessage(lastError);
      LOG.error("Advapi::cryptDestroyHash>>Error destroying handle: " + message, new Exception("Advapi::cryptDestroyHash (" + message + ")"));
    }
    if (LOGGING)
      LOG.debug("cryptDestroyHash " + res);
    return res;
  }

  /**
   * The cryptGetHashParam function retrieves data that governs the operations of a hash object. The actual hash
   * value can be retrieved by using this function.
   *
   * @param hashHandle handle to hash object.
   * @param paramCode type of data requested.
   * @param data data buffer. NULLABLE.
   * @return Actual length of retrieved data or -1 if fails. See Windows Crypto API for details.
   * @see AdvapiLib#
   */
  public static int cryptGetHashParam(Pointer hashHandle, int paramCode, byte[] data) {
    if (LOGGING)
      LOG.debug("cryptGetHashParam " + hashHandle + " " + paramCode + " (data)");
    final IntByReference dataLen = new IntByReference(data == null ? 0 : data.length);
    int res = lib.CryptGetHashParam(hashHandle, paramCode, data, dataLen, 0) ? dataLen.getValue() : -1;
    if (LOGGING)
      LOG.debug("cryptGetHashParam " + res);
    return res;
  }

  public static boolean cryptSetHashParam(Pointer hashHandle, int paramCode, byte[] data, int flags) {
    if (LOGGING)
      LOG.debug("cryptSetHashParam " + hashHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetHashParam(hashHandle, paramCode, data, flags);
    if (LOGGING)
      LOG.debug("cryptSetHashParam " + res);
    return res;
  }

  public static boolean cryptHashData(Pointer hashHandle, byte[] data, int flags) {
    final int cData = data.length;
    return cryptHashData(hashHandle, data, cData, flags);
  }

  public static boolean cryptHashData(Pointer hashHandle, byte[] data, int cData, int flags) {
    if (LOGGING)
      LOG.debug("cryptHashData " + hashHandle + " (data) " + cData + " " + flags);
    boolean res = lib.CryptHashData(hashHandle, data, cData, flags);
    if (LOGGING)
      LOG.debug("cryptHashData " + res);
    return res;
  }

  public static byte[] cryptSignHash(Pointer hashHandle, int keySpec, int flags) throws CryptoException {
    if (LOGGING)
      LOG.debug("cryptSignHash " + hashHandle + " " + keySpec + " " + flags);
    final IntByReference len = new IntByReference();
    if (!lib.CryptSignHashA(hashHandle, keySpec, null, flags, null, len))
      throw CryptoUtil.raiseCryptoError("CryptSignHash", getLastError());
    final byte[] sign = new byte[len.getValue()];
    if (!lib.CryptSignHashA(hashHandle, keySpec, null, flags, sign, len))
      throw CryptoUtil.raiseCryptoError("CryptSignHash", getLastError());
    if (LOGGING)
      LOG.debug("cryptSignHash " + Arrays.toString(sign));
    return Win32Api.getActualData(sign, len.getValue());
  }

  public static byte[] cryptGetKeyParam(Pointer keyHandle, int paramCode) throws CryptoException {
    if (LOGGING)
      LOG.debug("cryptGetKeyParam " + keyHandle + " " + paramCode);
    final IntByReference len = new IntByReference();
    if (!lib.CryptGetKeyParam(keyHandle, paramCode, null, len, 0))
      throw CryptoUtil.raiseCryptoError("CryptGetKeyParam", getLastError());
    final byte[] data = new byte[len.getValue()];
    if (!lib.CryptGetKeyParam(keyHandle, paramCode, data, len, 0))
      throw CryptoUtil.raiseCryptoError("CryptGetKeyParam", getLastError());
    byte[] res = Win32Api.getActualData(data, len.getValue());
    if (LOGGING)
      LOG.debug("cryptGetKeyParam " + Arrays.toString(res));
    return res;
  }

  public static Pointer cryptGetUserKey(Pointer provHandle, int keySpec) throws CryptoException {
    if (LOGGING)
      LOG.debug("cryptGetUserKey " + provHandle + " " + keySpec);
    final PointerByReference phUserKey = new PointerByReference();
    if (!lib.CryptGetUserKey(provHandle, keySpec, phUserKey))
      throw CryptoUtil.raiseCryptoError("CryptGetUserKey", getLastError());
    final Pointer res = phUserKey.getValue();
    if (LOGGING)
      LOG.debug("cryptGetUserKey " + res);
    return res;
  }

  public static boolean cryptSetKeyParam(Pointer keyHandle, int paramCode, byte[] data, int flags) {
    if (LOGGING)
      LOG.debug("cryptSetKeyParam " + keyHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetKeyParam(keyHandle, paramCode, data, flags);
    if (LOGGING)
      LOG.debug("cryptSetKeyParam " + res);
    return res;
  }

  public static boolean cryptSetKeyParam(Pointer keyHandle, int paramCode, Pointer data, int flags) {
    if (LOGGING)
      LOG.debug("cryptSetKeyParam " + keyHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetKeyParam(keyHandle, paramCode, data, flags);
    if (LOGGING)
      LOG.debug("cryptSetKeyParam " + res);
    return res;
  }

  public static Pointer cryptImportKey(Pointer provHandle, byte[] data, Pointer pubKeyHandle, int flags) {
    if (LOGGING)
      LOG.debug("cryptImportKey " + provHandle + " (data) " + pubKeyHandle + " " + flags);
    final PointerByReference keyHandle = new PointerByReference();
    final Pointer res = lib.CryptImportKey(provHandle, data, data.length, pubKeyHandle, flags, keyHandle) ? keyHandle.getValue() : null;
    if (LOGGING)
      LOG.debug("cryptImportKey " + res);
    return res;
  }

  public static byte[] cryptGetProvParam(Pointer provHandle, int paramCode, int flags) throws CryptoException {
    if (LOGGING)
      LOG.debug("cryptGetProvParam " + paramCode + " " + paramCode + " " + flags);
    final IntByReference len = new IntByReference();
    if (!lib.CryptGetProvParam(provHandle, paramCode, null, len, flags))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    final byte data[] = new byte[len.getValue()];
    if (!lib.CryptGetProvParam(provHandle, paramCode, data, len, flags))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    if (LOGGING)
      LOG.debug("cryptGetProvParam " + Arrays.toString(data));
    return Win32Api.getActualData(data, len.getValue());
  }

  public static synchronized List<String> enumContainers(Pointer provHandle) throws CryptoException {
    int flag = CRYPT_FIRST;
    final List<String> containers = new ArrayList<String>();

    final IntByReference dataSize = new IntByReference();
    if (!lib.CryptGetProvParam(provHandle, Wincrypt.PP_ENUMCONTAINERS, null, dataSize, CRYPT_FIRST)) {
      final int errCode = getLastError();
      if (errCode == Winerror.ERROR_NO_MORE_ITEMS)
        return containers;
      else
        throw CryptoUtil.raiseCryptoError("enumContainers", errCode);
    }

    final byte[] bytes = new byte[dataSize.getValue()];
    while (true) {
      final IntByReference bufLen = new IntByReference(bytes.length);
      if (!lib.CryptGetProvParam(provHandle, Wincrypt.PP_ENUMCONTAINERS, bytes, bufLen, flag)) {
        final int errCode = getLastError();
        if (errCode == Winerror.ERROR_NO_MORE_ITEMS)
          break;
        else
          throw CryptoUtil.raiseCryptoError("enumContainers", errCode);
      }
      final byte[] strData = Win32Api.getActualData(bytes, bufLen.getValue());
      final String containerName = Native.toString(strData);
      containers.add(containerName);
      LOG.debug("Found container: " + containerName);
      flag = Wincrypt.CRYPT_NEXT;
    }
    return containers;
  }

  public static boolean cryptDestroyKey(Pointer keyHandle) {
    if (LOGGING)
      LOG.debug("cryptDestroyKey " + keyHandle);
    final boolean res = lib.CryptDestroyKey(keyHandle);
    if (!res) {
      final int lastError = getLastError();
      final String message = ErrorMessages.getMessage(lastError);
      LOG.error("Advapi::cryptDestroyKey>>Error destroying key handle: " + message, new Exception("Advapi::cryptDestroyKey (" + message + ")"));
    }
    if (LOGGING)
      LOG.debug("cryptDestroyKey " + res);
    return res;
  }

  /**
   * Important!
   * The CryptEncrypt function is not guaranteed to be thread safe and may return incorrect results
   * if invoked simultaneously by multiple callers. (http://msdn.microsoft.com/en-us/library/aa379924%28v=vs.85%29.aspx)
   */
  public static synchronized byte[] cryptEncrypt(
      Pointer keyHandle,
      Pointer hashHandle,
      boolean Final,
      byte[] pbData
  ) throws CryptoException {
    final IntByReference pdwDataLen = new IntByReference(pbData.length);
    if (!lib.CryptEncrypt(keyHandle, hashHandle, Final, 0, null, pdwDataLen, pbData.length))
      throw CryptoUtil.raiseCryptoError("CryptEncrypt - initialization", getLastError());

    final byte[] data = new byte[Math.max(pbData.length, pdwDataLen.getValue())];
    System.arraycopy(pbData, 0, data, 0, pbData.length);

    final int ciferSize = pdwDataLen.getValue();
    pdwDataLen.setValue(pbData.length);
    if (!lib.CryptEncrypt(keyHandle, hashHandle, Final, 0, data, pdwDataLen, data.length))
      throw CryptoUtil.raiseCryptoError("CryptEncrypt", getLastError());

    return Win32Api.getActualData(data, pdwDataLen.getValue());
  }

  public static byte[] cryptDecrypt(
      Pointer hKey,
      Pointer hHash,
      boolean Final,
      int dwFlags,
      byte[] pbData
  ) throws CryptoException {
    final IntByReference pdwDataLen = new IntByReference(pbData.length);
    if (!lib.CryptDecrypt(hKey, hHash, Final, dwFlags, pbData, pdwDataLen))
      throw CryptoUtil.raiseCryptoError("CryptDecrypt", getLastError());
    return Win32Api.getActualData(pbData, pdwDataLen.getValue());
  }

  public static Pointer cryptDeriveKey(
      Pointer provHandle,
      int algId,
      Pointer hashHandle,
      int flags
  ) throws CryptoException {
    final PointerByReference phKey = new PointerByReference();
    if (!lib.CryptDeriveKey(provHandle, algId, hashHandle, flags, phKey))
      throw CryptoUtil.raiseCryptoError("CryptDeriveKey", getLastError());

    return phKey.getValue();
  }

  public static byte[] hashData(Pointer provHandle, byte[] data, int algId, int hashCount) throws CryptoException {
    int size = -1;
    for (int i = 0; i < hashCount; i++) {
      final Pointer hash = cryptCreateHash(provHandle, algId);
      if (hash == null)
        throw CryptoUtil.raiseCryptoError("HashData (createHash)", getLastError());
      try {
        cryptHashData(hash, data, 0);
        if (size < 0) {
          final byte[] buf = new byte[4];
          Advapi.cryptGetHashParam(hash, Wincrypt.HP_HASHSIZE, buf);
          size = Win32Api.byteArrayToInt(buf);
          assert size > 0;
          data = new byte[size];
        }
        Advapi.cryptGetHashParam(hash, Wincrypt.HP_HASHVAL, data);
      } finally {
        cryptDestroyHash(hash);
      }
    }
    return data;
  }

  public static int getLastError() {
    return Platform.isWindows() ? Win32Api.getLastError() : lib.GetLastError();
  }

  public static String formatMessage(int errorCode) {
    if (Platform.isLinux()) {
      final byte[] bytes = new byte[4096];
      lib.FormatMessage(0x00001000, null, errorCode, 0, bytes, bytes.length, null);
      return Native.toString(bytes).trim();
    } else {
      return Win32Api.getMessage(errorCode);
    }
  }
}
