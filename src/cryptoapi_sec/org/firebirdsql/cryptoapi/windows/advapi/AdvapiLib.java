package org.firebirdsql.cryptoapi.windows.advapi;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CRYPT_DECRYPT_MESSAGE_PARA;

import java.nio.ByteBuffer;

@SuppressWarnings({"JavaDoc"})
public interface AdvapiLib extends Library {
  /**
   * The CryptAcquireContext function is used to acquire a handle to a particular key container within a particular
   * cryptographic service provider (CSP). This returned handle is used in calls to CryptoAPI functions that use the
   * selected CSP.
   * This function first attempts to find a CSP with the characteristics described in the dwProvType and pszProvider
   * parameters. If the CSP is found, the function attempts to find a key container within the CSP that matches the name
   * specified by the pszContainer parameter. To acquire the context and the key container of a private key associated
   * with the public key of a certificate, use CryptAcquireCertificatePrivateKey.
   * With the appropriate setting of dwFlags, this function can also create and destroy key containers and can
   * provide access to a CSP with a temporary key container if access to a private key is not required.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptAcquireContext(
   * __out  HCRYPTPROV *phProv,
   * __in   LPCTSTR pszContainer,
   * __in   LPCTSTR pszProvider,
   * __in   DWORD dwProvType,
   * __in   DWORD dwFlags
   * );
   *
   * @return {@code true} if success
   */
  public boolean CryptAcquireContextA(
      PointerByReference phProv, String pszContainer, String pszProvider, int dwProvType, int dwFlags
  );

  /**
   * The CryptReleaseContext function releases the handle of a cryptographic service provider (CSP) and a key container.
   * At each call to this function, the reference count on the CSP is reduced by one. When the reference count
   * reaches zero, the context is fully released and it can no longer be used by any function in the application.
   * An application calls this function after finishing the use of the CSP. After this function is called, the
   * released CSP handle is no longer valid. This function does not destroy key containers or key pairs.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptReleaseContext(
   * __in  HCRYPTPROV hProv,
   * __in  DWORD dwFlags
   * );
   */
  public boolean CryptReleaseContext(Pointer phProv, int dwFlags);

  /**
   * The CryptCreateHash function initiates the hashing of a stream of data. It creates and returns to the calling
   * application a handle to a cryptographic service provider (CSP) hash object. This handle is used in subsequent
   * calls to CryptHashData and CryptHashSessionKey to hash session keys and other streams of data.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptCreateHash(
   * __in   HCRYPTPROV hProv,
   * __in   ALG_ID Algid,
   * __in   HCRYPTKEY hKey,
   * __in   DWORD dwFlags,
   * __out  HCRYPTHASH *phHash
   * );
   */
  public boolean CryptCreateHash(Pointer hProv, int Algid, Pointer hKey, int dwFlags, PointerByReference phHash);

  /**
   * The CryptDestroyHash function destroys the hash object referenced by the hHash parameter. After a hash object
   * has been destroyed, it can no longer be used.
   * To help ensure security, we recommend that hash objects be destroyed after they have been used.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptDestroyHash(
   * __in  HCRYPTHASH hHash
   * );
   */
  public boolean CryptDestroyHash(Pointer hHash);

  /**
   * The CryptGetHashParam function retrieves data that governs the operations of a hash object. The actual hash
   * value can be retrieved by using this function.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptGetHashParam(
   * __in     HCRYPTHASH hHash,
   * __in     DWORD dwParam,
   * __out    BYTE *pbData,
   * __inout  DWORD *pdwDataLen,
   * __in     DWORD dwFlags
   * );
   */
  public boolean CryptGetHashParam(Pointer hHash, int dwParam, byte[] pbData, IntByReference pdwDataLen, int dwFlags);

  /**
   * The CryptSetHashParam function customizes the operations of a hash object, including setting up initial hash
   * contents and selecting a specific hashing algorithm.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptSetHashParam(
   * __in  HCRYPTHASH hHash,
   * __in  DWORD dwParam,
   * __in  const BYTE *pbData,
   * __in  DWORD dwFlags
   * );
   */
  public boolean CryptSetHashParam(Pointer hHash, int dwParam, byte[] pbData, int dwFlags);

  /**
   * The CryptHashData function adds data to a specified hash object. This function and CryptHashSessionKey can be
   * called multiple times to compute the hash of long or discontinuous data streams.
   * Before calling this function, CryptCreateHash must be called to create a handle of a hash object.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptHashData(
   * __in  HCRYPTHASH hHash,
   * __in  BYTE *pbData,
   * __in  DWORD dwDataLen,
   * __in  DWORD dwFlags
   * );
   */
  public boolean CryptHashData(Pointer hHash, byte[] pbData, int dwDataLen, int dwFlags);

  /**
   * The CryptSignHash function signs data. Because all signature algorithms are asymmetric and thus slow, CryptoAPI
   * does not allow data to be signed directly. Instead, data is first hashed, and CryptSignHash is used to sign the hash.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptSignHash(
   * __in     HCRYPTHASH hHash,
   * __in     DWORD dwKeySpec,
   * __in     LPCTSTR sDescription,
   * __in     DWORD dwFlags,
   * __out    BYTE *pbSignature,
   * __inout  DWORD *pdwSigLen
   * );
   */
  public boolean CryptSignHashA(Pointer hHash, int dwKeySpec, String sDescription, int dwFlags, byte[] pbSignature, IntByReference pdwSigLen);

  /**
   * The CryptVerifySignature function verifies the signature of a hash object.
   * <p/>
   * Before calling this function, CryptCreateHash must be called to create the handle of a hash object.
   * CryptHashData or CryptHashSessionKey is then used to add data or session keys to the hash object.
   * After CryptVerifySignature completes, only CryptDestroyHash can be called by using the hHash handle.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptVerifySignature(
   * __in  HCRYPTHASH hHash,
   * __in  BYTE *pbSignature,
   * __in  DWORD dwSigLen,
   * __in  HCRYPTKEY hPubKey,
   * __in  LPCTSTR sDescription,
   * __in  DWORD dwFlags
   * );
   */
  public boolean CryptVerifySignatureA(Pointer hHash, byte[] pbSignature, int dwSigLen, Pointer hPubKey, String sDescription, int dwFlags);

  /**
   * The CryptGetKeyParam function retrieves data that governs the operations of a key.
   * If the Microsoft Cryptographic Service Provider is used, the base symmetric keying material is not obtainable
   * by this or any other function.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptGetKeyParam(
   * __in     HCRYPTKEY hKey,
   * __in     DWORD dwParam,
   * __out    BYTE *pbData,
   * __inout  DWORD *pdwDataLen,
   * __in     DWORD dwFlags
   * );
   */
  public boolean CryptGetKeyParam(Pointer hKey, int dwParam, byte[] pbData, IntByReference pdwDataLen, int dwFlags);

  /**
   * The CryptGetUserKey function retrieves a handle of one of a user's two public/private key pairs.
   * This function is used only by the owner of the public/private key pairs and only when the handle of a cryptographic
   * service provider (CSP) and its associated key container is available. If the CSP handle is not available and the
   * user's certificate is, use CryptAcquireCertificatePrivateKey.
   * <p/>
   * Syntax:
   * <p/>
   * BOOL WINAPI CryptGetUserKey(
   * __in   HCRYPTPROV hProv,
   * __in   DWORD dwKeySpec,
   * __out  HCRYPTKEY *phUserKey
   * );
   */
  public boolean CryptGetUserKey(Pointer hProv, int dwKeySpec, PointerByReference phUserKey);

  /**
   * The CryptSetKeyParam function customizes various aspects of a session key's operations. The values set by
   * this function are not persisted to memory and can only be used with in a single session.
   * The Microsoft Base Cryptographic Provider does not permit setting values for key exchange or signature keys;
   * however, custom providers can define values that can be set for its keys.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptSetKeyParam(
   * __in  HCRYPTKEY hKey,
   * __in  DWORD dwParam,
   * __in  const BYTE *pbData,
   * __in  DWORD dwFlags
   * );
   */
  public boolean CryptSetKeyParam(Pointer hKey, int dwPram, byte[] pbData, int dwFlags);

  public boolean CryptSetKeyParam(Pointer hKey, int dwPram, Pointer pbData, int dwFlags);

  /**
   * The CryptImportKey function transfers a cryptographic key from a key BLOB into a cryptographic service
   * provider (CSP). This function can be used to import an Schannel session key, regular session key, public key,
   * or public/private key pair. For all but the public key, the key or key pair is encrypted.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptImportKey(
   * __in   HCRYPTPROV hProv,
   * __in   BYTE *pbData,
   * __in   DWORD dwDataLen,
   * __in   HCRYPTKEY hPubKey,
   * __in   DWORD dwFlags,
   * __out  HCRYPTKEY *phKey
   * );
   */
  public boolean CryptImportKey(Pointer hProv, byte[] pbData, int dwDataLen, Pointer hPubKey, int dwFlags, PointerByReference phKey);

  /**
   * The CryptGetProvParam function retrieves parameters that govern the operations of a cryptographic
   * service provider (CSP).
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptGetProvParam(
   * __in     HCRYPTPROV hProv,
   * __in     DWORD dwParam,
   * __out    BYTE *pbData,
   * __inout  DWORD *pdwDataLen,
   * __in     DWORD dwFlags
   * );
   */
  public boolean CryptGetProvParam(Pointer hProv, int dwParam, byte[] data, IntByReference pdwDataLen, int dwFlags);

  /**
   * The CryptExportKey function exports a cryptographic key or a key pair from a cryptographic service
   * provider (CSP) in a secure manner.
   * <p/>
   * A handle to the key to be exported is passed to the function, and the function returns a key BLOB.
   * This key BLOB can be sent over a nonsecure docs or stored in a nonsecure storage location.
   * This function can export an Schannel session key, regular session key, public key, or public/private key pair.
   * The key BLOB to export is useless until the intended recipient uses the CryptImportKey function on it to import
   * the key or key pair into a recipient's CSP.
   * <p/>
   * Syntax:
   * BOOL WINAPI CryptExportKey(
   * __in     HCRYPTKEY hKey,
   * __in     HCRYPTKEY hExpKey,
   * __in     DWORD dwBlobType,
   * __in     DWORD dwFlags,
   * __out    BYTE *pbData,
   * __inout  DWORD *pdwDataLen
   * );
   */
  public boolean CryptExportKey(Pointer hKey, Pointer hExpKey, int dwBlobType, int dwFlags, ByteBuffer pbData, IntByReference pdwDataLen);

  /**
   * The CryptDestroyKey function releases the handle referenced by the hKey parameter. After a key handle has
   * been released, it is no longer valid and cannot be used again.
   * If the handle refers to a session key, or to a public key that has been imported into the cryptographic
   * service provider (CSP) through CryptImportKey, this function destroys the key and frees the memory that
   * the key used. Many CSPs overwrite the memory where the key was held before freeing it. However, the
   * underlying public/private key pair is not destroyed by this function. Only the handle is destroyed.
   * <p/>
   * Syntax:
   * <p/>
   * BOOL WINAPI CryptDestroyKey(
   * __in  HCRYPTKEY hKey
   * );
   */
  public boolean CryptDestroyKey(Pointer hKey);

  /**
   * The CryptSetProvParam function customizes the operations of a cryptographic service provider (CSP).
   * This function is commonly used to set a security descriptor on the key container associated with a CSP to
   * control access to the private keys in that key container.
   *
   * Syntax:
   * BOOL WINAPI CryptSetProvParam(
   *   __in  HCRYPTPROV hProv,
   *   __in  DWORD dwParam,
   *   __in  const BYTE *pbData,
   *   __in  DWORD dwFlags
   * );
   */
   public boolean CryptSetProvParam(Pointer hProv, int dwParam, byte[] pbData, int dwFlags);

  /** The CryptGenKey function generates a random cryptographic session key or a public/private key pair.
   * A handle to the key or key pair is returned in phKey. This handle can then be used as needed with any CryptoAPI
   * function that requires a key handle.
   *
   * The calling application must specify the algorithm when calling this function. Because this algorithm type is
   * kept bundled with the key, the application does not need to specify the algorithm later when the actual
   * cryptographic operations are performed.
   *
   * Syntax:
   *
   *BOOL WINAPI CryptGenKey(
   *  __in   HCRYPTPROV hProv,
   *  __in   ALG_ID Algid,
   *  __in   DWORD dwFlags,
   *  __out  HCRYPTKEY *phKey
   *);
   **/
  public boolean CryptGenKey(
    Pointer hProv,
    int Algid,
    int dwFlags,
    PointerByReference phKey);

  /** The CryptGenRandom function fills a buffer with cryptographically random bytes.
   *
   * Syntax:
   *
   *BOOL WINAPI CryptGenRandom(
   *  __in     HCRYPTPROV hProv,
   *  __in     DWORD dwLen,
   *  __inout  BYTE *pbBuffer
   *);
   **/
  public boolean CryptGenRandom(
    Pointer hProv,
    int dwFlags,
    ByteBuffer buffer);

  public int GetLastError();

  /**
   * DWORD WINAPI FormatMessage(
   *   _In_      DWORD dwFlags,
   *   _In_opt_  LPCVOID lpSource,
   *   _In_      DWORD dwMessageId,
   *   _In_      DWORD dwLanguageId,
   *   _Out_     LPTSTR lpBuffer,
   *   _In_      DWORD nSize,
   *   _In_opt_  va_list *Arguments
   * );
   */
  public int FormatMessage(
      int dwFlags,
      Pointer lpSource,
      int dwMessageId,
      int dwLanguageId,
      byte[] lpBuffer,
      int nSize,
      Pointer Arguments
  );
}



