package org.firebirdsql.cryptoapi.windows.advapi;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.windows.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.firebirdsql.cryptoapi.windows.Wincrypt.*;

public class Advapi {
  private static final System.Logger log = System.getLogger(Advapi.class.getName());

  private static final String ADVAPI_LIB_NAME = Platform.isWindows() ?"advapi32" : "capi20";
  private final static AdvapiLib lib;
  private static final boolean LOGGING = false;

  static {
    JnaUtils.init();
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "static");
    try {
      lib = Native.load(ADVAPI_LIB_NAME, AdvapiLib.class);
    } catch (Exception e) {
      log.log(System.Logger.Level.ERROR, "Advapi Initialization failed", e);
      throw new RuntimeException(e);
    }
    //Native.setProtected(true);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "static " + lib);
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
      log.log(System.Logger.Level.DEBUG, "cryptAcquireContext " + keyContainer + " " + provider + " " + provType + " " + flags);
    final PointerByReference phProv = new PointerByReference();
    if (!lib.CryptAcquireContextA(phProv, keyContainer, provider, provType, flags))
      throw CryptoUtil.raiseCryptoError("cryptAcquireContext - init", getLastError());
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptAcquireContext " + phProv.getValue());
    return phProv.getValue();
  }

  public static boolean cryptReleaseContext(Pointer provHandle) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptReleaseContext " + provHandle);
    boolean res = lib.CryptReleaseContext(provHandle, 0);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptReleaseContext " + res);
    return res;
  }

  public static Pointer cryptCreateHash(Pointer provHandle, int algId) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptCreateHash " + provHandle + " " + algId);
    final PointerByReference hash = new PointerByReference();
    Pointer res = lib.CryptCreateHash(provHandle, algId, null, 0, hash) ? hash.getValue() : null;
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptCreateHash " + res);
    return res;
  }

  public static boolean cryptDestroyHash(Pointer hashHandle) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptDestroyHash " + hashHandle);
    boolean res = lib.CryptDestroyHash(hashHandle);
    if (!res) {
      final int lastError = getLastError();
      final String message = ErrorMessages.getMessage(lastError);
      log.log(System.Logger.Level.ERROR, "Advapi::cryptDestroyHash>>Error destroying handle: " + message, new Exception("Advapi::cryptDestroyHash (" + message + ")"));
    }
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptDestroyHash " + res);
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
      log.log(System.Logger.Level.DEBUG, "cryptGetHashParam " + hashHandle + " " + paramCode + " (data)");
    final IntByReference dataLen = new IntByReference(data == null ? 0 : data.length);
    int res = lib.CryptGetHashParam(hashHandle, paramCode, data, dataLen, 0) ? dataLen.getValue() : -1;
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetHashParam " + res);
    return res;
  }

  public static boolean cryptSetHashParam(Pointer hashHandle, int paramCode, byte[] data, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetHashParam " + hashHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetHashParam(hashHandle, paramCode, data, flags);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetHashParam " + res);
    return res;
  }

  public static boolean cryptHashData(Pointer hashHandle, byte[] data, int flags) {
    final int cData = data.length;
    return cryptHashData(hashHandle, data, cData, flags);
  }

  public static boolean cryptHashData(Pointer hashHandle, byte[] data, int cData, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptHashData " + hashHandle + " (data) " + cData + " " + flags);
    boolean res = lib.CryptHashData(hashHandle, data, cData, flags);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptHashData " + res);
    return res;
  }

  public static byte[] cryptSignHash(Pointer hashHandle, int keySpec, int flags) throws CryptoException {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSignHash " + hashHandle + " " + keySpec + " " + flags);
    final IntByReference len = new IntByReference();
    if (!lib.CryptSignHashA(hashHandle, keySpec, null, flags, null, len))
      throw CryptoUtil.raiseCryptoError("CryptSignHash", getLastError());
    final byte[] sign = new byte[len.getValue()];
    if (!lib.CryptSignHashA(hashHandle, keySpec, null, flags, sign, len))
      throw CryptoUtil.raiseCryptoError("CryptSignHash", getLastError());
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSignHash " + Arrays.toString(sign));
    return Win32Api.getActualData(sign, len.getValue());
  }

  public static boolean cryptVerifySignature(Pointer hashHandle, byte[] signature, Pointer pubKeyHandle, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptVerifySignature " + hashHandle + " " + Arrays.toString(signature) + " " + pubKeyHandle + " " + flags);
    boolean res = lib.CryptVerifySignatureA(hashHandle, signature, signature.length, pubKeyHandle, null, flags);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptVerifySignature " + res);
    return res;
  }

  public static byte[] cryptGetKeyParam(Pointer keyHandle, int paramCode) throws CryptoException {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetKeyParam " + keyHandle + " " + paramCode);
    final IntByReference len = new IntByReference();
    if (!lib.CryptGetKeyParam(keyHandle, paramCode, null, len, 0))
      throw CryptoUtil.raiseCryptoError("CryptGetKeyParam", getLastError());
    final byte[] data = new byte[len.getValue()];
    if (!lib.CryptGetKeyParam(keyHandle, paramCode, data, len, 0))
      throw CryptoUtil.raiseCryptoError("CryptGetKeyParam", getLastError());
    byte[] res = Win32Api.getActualData(data, len.getValue());
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetKeyParam " + Arrays.toString(res));
    return res;
  }

  public static String getContainerName(Pointer providerHandle) throws CryptoException {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "getContainerName (!!) " + providerHandle);
    final IntByReference len = new IntByReference();
    if (!lib.CryptGetProvParam(providerHandle, Wincrypt.PP_CONTAINER, null, len, 0))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    final byte[] data = new byte[len.getValue()];
    if (!lib.CryptGetProvParam(providerHandle, Wincrypt.PP_CONTAINER, data, len, 0))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "getContainerName " + Native.toString(data));
    return Native.toString(data).trim();
  }

  public static Pointer cryptGetUserKey(Pointer provHandle, int keySpec) throws CryptoException {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetUserKey " + provHandle + " " + keySpec);
    final PointerByReference phUserKey = new PointerByReference();
    if (!lib.CryptGetUserKey(provHandle, keySpec, phUserKey))
      throw CryptoUtil.raiseCryptoError("CryptGetUserKey", getLastError());
    final Pointer res = phUserKey.getValue();
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetUserKey " + res);
    return res;
  }

  public static boolean cryptSetKeyParam(Pointer keyHandle, int paramCode, byte[] data, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetKeyParam " + keyHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetKeyParam(keyHandle, paramCode, data, flags);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetKeyParam " + res);
    return res;
  }

  public static boolean cryptSetKeyParam(Pointer keyHandle, int paramCode, Pointer data, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetKeyParam " + keyHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetKeyParam(keyHandle, paramCode, data, flags);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetKeyParam " + res);
    return res;
  }

  public static Pointer cryptImportKey(Pointer provHandle, byte[] data, Pointer pubKeyHandle, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptImportKey " + provHandle + " (data) " + pubKeyHandle + " " + flags);
    final PointerByReference keyHandle = new PointerByReference();
    final Pointer res = lib.CryptImportKey(provHandle, data, data.length, pubKeyHandle, flags, keyHandle) ? keyHandle.getValue() : null;
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptImportKey " + res);
    return res;
  }

  public static byte[] cryptGetProvParam(Pointer provHandle, int paramCode, int flags) throws CryptoException {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetProvParam " + paramCode + " " + paramCode + " " + flags);
    final IntByReference len = new IntByReference();
    if (!lib.CryptGetProvParam(provHandle, paramCode, null, len, flags))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    final byte data[] = new byte[len.getValue()];
    if (!lib.CryptGetProvParam(provHandle, paramCode, data, len, flags))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetProvParam " + Arrays.toString(data));
    return Win32Api.getActualData(data, len.getValue());
  }

  public static byte[] cryptGetProvParam(Pointer provHandle, int paramCode, int flags, int bufferSize) throws CryptoException {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetProvParam " + paramCode + " " + paramCode + " " + flags + " " + bufferSize);
    final IntByReference len = new IntByReference(bufferSize);
    final byte data[] = new byte[len.getValue()];
    if (!lib.CryptGetProvParam(provHandle, paramCode, data, len, flags))
      throw CryptoUtil.raiseCryptoError("CryptGetProvParam", getLastError());
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptGetProvParam " + Arrays.toString(data));
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
      log.log(System.Logger.Level.DEBUG, "Found container: " + containerName);
      flag = Wincrypt.CRYPT_NEXT;
    }
    return containers;
  }

  public static boolean cryptDestroyKey(Pointer keyHandle) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptDestroyKey " + keyHandle);
    final boolean res = lib.CryptDestroyKey(keyHandle);
    if (!res) {
      final int lastError = getLastError();
      final String message = ErrorMessages.getMessage(lastError);
      log.log(System.Logger.Level.ERROR, "Advapi::cryptDestroyKey>>Error destroying key handle: " + message, new Exception("Advapi::cryptDestroyKey (" + message + ")"));
    }
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptDestroyKey " + res);
    return res;
  }

  public static boolean cryptSetProvParam(Pointer provHandle, int paramCode, byte[] data, int flags) {
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetProvParam " + provHandle + " " + paramCode + " (data) " + flags);
    boolean res = lib.CryptSetProvParam(provHandle, paramCode, data, flags);
    if (LOGGING)
      log.log(System.Logger.Level.DEBUG, "cryptSetProvParam " + res);
    return res;
  }

  public static ByteBuffer cryptGenRandom(Pointer provHandle, int bufferSize) throws CryptoException {
    final ByteBuffer buf = ByteBuffer.wrap(new byte[bufferSize]);
    if (!lib.CryptGenRandom(provHandle, bufferSize, buf))
      throw CryptoUtil.raiseCryptoError("CryptGenRandom", getLastError());
    return buf;
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
    throw new CryptoException("Not available in secure mode");
  }

  public static byte[] cryptDecrypt(
      Pointer hKey,
      Pointer hHash,
      boolean Final,
      int dwFlags,
      byte[] pbData
  ) throws CryptoException {
    throw new CryptoException("Not available in secure mode");
  }

  public static Pointer cryptDeriveKey(
      Pointer provHandle,
      int algId,
      Pointer hashHandle,
      int flags
  ) throws CryptoException {
    throw new CryptoException("Not available in secure mode");
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

  public static void setPin(Pointer provHandle, String pin) {
    cryptSetProvParam(provHandle, Wincrypt.PP_KEYEXCHANGE_PIN, (pin + '\0').getBytes(), 0);
  }

  public static void clearPin(Pointer provHandle) {
    cryptSetProvParam(provHandle, Wincrypt.PP_KEYEXCHANGE_PIN, null, 0);
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
