package org.firebirdsql.cryptoapi.windows.crypt32;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 20:27:53
 */
@SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
public interface Crypt32Lib extends Library {
  /**
   * The CertOpenSystemStore function is a simplified function that opens the most common system certificate store.
   * To open certificate stores with more complex requirements, such as file-based or memory-based stores, use CertOpenStore.
   *
   * Syntax:
   * HCERTSTORE WINAPI CertOpenSystemStore(
   *   __in  HCRYPTPROV_LEGACY hprov,
   *   __in  LPTCSTR szSubsystemProtocol
   * );
   */
  public Pointer CertOpenSystemStoreA(Pointer hprov, String szSubsystemProtocol);

  // linux version
  public Pointer CertOpenSystemStore(Pointer hprov, String szSubsystemProtocol);

  /**
   * The CertCloseStore function closes a certificate store handle and reduces the reference count on the store.
   * There needs to be a corresponding call to CertCloseStore for each successful call to the CertOpenStore or
   * CertDuplicateStore functions.
   *
   * Syntax:
   * BOOL WINAPI CertCloseStore(
   *   __in  HCERTSTORE hCertStore,
   *   __in  DWORD dwFlags
   * );
   */
  public boolean CertCloseStore(Pointer hCertStore, int dwFlags);

  /**
   * The CertEnumCertificatesInStore function retrieves the first or next certificate in a certificate store.
   * Used in a loop, this function can retrieve in sequence all certificates in a certificate store.
   *
   * Syntax:
   * PCCERT_CONTEXT WINAPI CertEnumCertificatesInStore(
   *   __in  HCERTSTORE hCertStore,
   *   __in  PCCERT_CONTEXT pPrevCertContext
   * );
   */
  public _CERT_CONTEXT.PCCERT_CONTEXT CertEnumCertificatesInStore(Pointer hCertStore, Pointer pPrevCertContext);

  /**
   * The CertFindCertificateInStore function finds the first or next certificate context in a certificate store that
   * matches a search criteria established by the dwFindType and its associated pvFindPara. This function can be
   * used in a loop to find all of the certificates in a certificate store that match the specified find criteria.
   *
   * Syntax:
   * PCCERT_CONTEXT WINAPI CertFindCertificateInStore(
   *   __in  HCERTSTORE hCertStore,
   *   __in  DWORD dwCertEncodingType,
   *   __in  DWORD dwFindFlags,
   *   __in  DWORD dwFindType,
   *   __in  const void *pvFindPara,
   *   __in  PCCERT_CONTEXT pPrevCertContext
   * );
   */
  public _CERT_CONTEXT.PCCERT_CONTEXT CertFindCertificateInStore(Pointer hCertStore, int dwCertEncodingType, int dwFindFlags,
                                                                 int dwFindType, byte[] pvFindPara, _CERT_CONTEXT.PCERT_CONTEXT pPrevCertContext);

  public _CERT_CONTEXT.PCCERT_CONTEXT CertFindCertificateInStore(Pointer hCertStore, int dwCertEncodingType, int dwFindFlags,
                                                                 int dwFindType, Pointer pvFindPara, _CERT_CONTEXT.PCERT_CONTEXT pPrevCertContext);

  /**
   * The CryptAcquireCertificatePrivateKey function obtains the private key for a certificate. This function is used
   * to obtain access to a user's private key when the user's certificate is available, but the handle of the user's
   * key container is not available. This function can only be used by the owner of a private key and not by any other user.
   * If a CSP handle and the key container containing a user's private key are available, the CryptGetUserKey
   * function should be used instead.
   *
   * Syntax:
   * BOOL WINAPI CryptAcquireCertificatePrivateKey(
   *   __in   PCCERT_CONTEXT pCert,
   *   __in   DWORD dwFlags,
   *   __in   void *pvReserved,
   *   __out  HCRYPTPROV_OR_NCRYPT_KEY_HANDLE *phCryptProvOrNCryptKey,
   *   __out  DWORD *pdwKeySpec,
   *   __out  BOOL *pfCallerFreeProvOrNCryptKey
   * );
   */
  public boolean CryptAcquireCertificatePrivateKey(_CERT_CONTEXT.PCERT_CONTEXT pCert, int dwFlags, Pointer pvReserved,
                                                   PointerByReference phCryptProvOrNCryptKey,
                                                   IntByReference pdwKeySpec, IntByReference pfCallerFreeProvOrNCryptKey);


  /**
   * The CertCreateCertificateContext function creates a certificate context from an encoded certificate.
   * The created context is not persisted to a certificate store. The function makes a copy of the encoded
   * certificate within the created context.
   *
   * Syntax:
   *
   * PCCERT_CONTEXT WINAPI CertCreateCertificateContext(
   *   __in  DWORD dwCertEncodingType,
   *   __in  const BYTE *pbCertEncoded,
   *   __in  DWORD cbCertEncoded
   * );
   */
  public _CERT_CONTEXT.PCCERT_CONTEXT CertCreateCertificateContext(int dwCertEncodingType, byte[] pbCertEncoded, int cbCertEncoded);


  /**
   * CertFreeCertificateContext Function
   * The CertFreeCertificateContext function frees a certificate context by decrementing its reference count.
   * When the reference count goes to zero, CertFreeCertificateContext frees the memory used by a certificate context.
   * To free a context obtained by a get, duplicate, or create function, call the appropriate free function.
   * To free a context obtained by a find or enumerate function, either pass it in as the previous context parameter
   * to a subsequent invocation of the function, or call the appropriate free function. For more information, see
   * the reference topic for the function that obtains the context.
   *
   * Syntax:
   *
   * BOOL WINAPI CertFreeCertificateContext(
   *   __in  PCCERT_CONTEXT pCertContext
   * );
   */
  public boolean CertFreeCertificateContext(Pointer pCertContext);
}
