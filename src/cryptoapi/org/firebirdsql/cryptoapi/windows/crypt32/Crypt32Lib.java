package org.firebirdsql.cryptoapi.windows.crypt32;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
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
   * The CertOpenStore function opens a certificate store by using a specified store provider type.
   * While this function can open a certificate store for most purposes, CertOpenSystemStore is recommended to open
   * the most common certificate stores. CertOpenStore is required for more complex options and special cases.
   *
   * Syntax:
   *
   * HCERTSTORE WINAPI CertOpenStore(
   *   __in  LPCSTR lpszStoreProvider,
   *   __in  DWORD dwMsgAndCertEncodingType,
   *   __in  HCRYPTPROV_LEGACY hCryptProv,
   *   __in  DWORD dwFlags,
   *   __in  const void *pvPara
   * );
   */
  public Pointer CertOpenStore(int lpszStoreProvider, int dwMsgAndCertEncodingType, Pointer hCryptProv, int dwFlags, String pvPara);

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
   * CertAddCertificateContextToStore Function
   *
   * The CertAddCertificateContextToStore function adds a certificate context to the certificate store.
   * Syntax
   *
   * BOOL WINAPI CertAddCertificateContextToStore(
   *   __in       HCERTSTORE hCertStore,
   *   __in       PCCERT_CONTEXT pCertContext,
   *   __in       DWORD dwAddDisposition,
   *   __out_opt  PCCERT_CONTEXT *ppStoreContext
   * );
   */
  public boolean CertAddCertificateContextToStore(
      Pointer hCertStore,
      Pointer pCertContext,
      int dwAddDisposition,
      PointerByReference ppStoreContext
  );

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
   * The CertFreeCertificateChain function frees a certificate chain by reducing its reference count.
   * If the reference count becomes zero, memory allocated for the chain is released.
   * To free a context obtained by a get, duplicate, or create function, call the appropriate free function.
   * To free a context obtained by a find or enumerate function, either pass it in as the previous context
   * parameter to a subsequent invocation of the function, or call the appropriate free function.
   * For more information, see the reference topic for the function that obtains the context.
   *
   * Syntax:
   * VOID WINAPI CertFreeCertificateChain(
   *   __in  PCCERT_CHAIN_CONTEXT pChainContext
   * );
   */
  public void CertFreeCertificateChain(Pointer pChainContext);

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

  /**
   * The CertSetCertificateContextProperty function sets an extended property for a specified certificate context.
   *
   * Syntax:
   *
   * BOOL WINAPI CertSetCertificateContextProperty(
   *   __in  PCCERT_CONTEXT pCertContext,
   *   __in  DWORD dwPropId,
   *   __in  DWORD dwFlags,
   *   __in  const void *pvData
   * );
   */
  public boolean CertSetCertificateContextProperty(
      _CERT_CONTEXT.PCCERT_CONTEXT pCertContext,
      int dwPropId,
      int dwFlags,
      Structure pvData
  );

  /**
   * BOOL WINAPI CertGetCertificateContextProperty(
   *   _In_     PCCERT_CONTEXT pCertContext,
   *   _In_     DWORD dwPropId,
   *   _Out_    void *pvData,
   *   _Inout_  DWORD *pcbData
   * );
   */

  public boolean CertGetCertificateContextProperty(
      _CERT_CONTEXT.PCCERT_CONTEXT pCertContext,
      int dwPropId,
      //Structure pvData,
      Pointer pvData,
      IntByReference pcbData
  );

  /**
   * The CryptImportPublicKeyInfo function converts and imports the public key information into the provider and
   * returns a handle of the public key. CryptImportPublicKeyInfoEx provides a revised version of this function.
   *
   * Syntax:
   *
   * BOOL WINAPI CryptImportPublicKeyInfo(
   *   __in   HCRYPTPROV hCryptProv,
   *   __in   DWORD dwCertEncodingType,
   *   __in   PCERT_PUBLIC_KEY_INFO pInfo,
   *   __out  HCRYPTKEY *phKey
   * );
   */
  public boolean CryptImportPublicKeyInfo(Pointer hCryptProv, int dwCertEncodingType, _CERT_PUBLIC_KEY_INFO.PCERT_PUBLIC_KEY_INFO pInfo, PointerByReference phKey);

  /**
   *  BOOL WINAPI CryptDecryptMessage(
   *  _In_              PCRYPT_DECRYPT_MESSAGE_PARA pDecryptPara,
   *  _In_        const BYTE                        *pbEncryptedBlob,
   *  _In_              DWORD                       cbEncryptedBlob,
   *  _Out_opt_         BYTE                        *pbDecrypted,
   *  _Inout_opt_       DWORD                       *pcbDecrypted,
   *  _Out_opt_         PCCERT_CONTEXT              *ppXchgCert
   * );
   */
  public boolean CryptDecryptMessage(
      _CRYPT_DECRYPT_MESSAGE_PARA.PCRYPT_DECRYPT_MESSAGE_PARA decryptPara,
      byte[] pbEncryptedBlob,
      int cbEncryptedBlob,
      byte[] pbDecrypted,
      IntByReference cbDecrypted,
      _CERT_CONTEXT.PCCERT_CONTEXT pXchgCert
  );

  /**
   *  Find OID information. Returns NULL if unable to find any information
   *  for the specified key and group. Note, returns a pointer to a constant
   *  data structure. The returned pointer MUST NOT be freed.
   *
   *  dwKeyType's:
   *    CRYPT_OID_INFO_OID_KEY, pvKey points to a szOID
   *    CRYPT_OID_INFO_NAME_KEY, pvKey points to a wszName
   *    CRYPT_OID_INFO_ALGID_KEY, pvKey points to an ALG_ID
   *    CRYPT_OID_INFO_SIGN_KEY, pvKey points to an array of two ALG_ID's:
   *      ALG_ID[0] - Hash Algid
   *      ALG_ID[1] - PubKey Algid
   *
   *  Setting dwGroupId to 0, searches all groups according to the dwKeyType.
   *  Otherwise, only the dwGroupId is searched.
   *
   *  WINCRYPT32API
   *          PCCRYPT_OID_INFO
   *  WINAPI
   *  CryptFindOIDInfo(
   *          IN DWORD dwKeyType,
   *          IN void *pvKey,
   *          IN DWORD dwGroupId
   *          );
   */
  public _CRYPT_OID_INFO.PCCRYPT_OID_INFO CryptFindOIDInfo(
          int dwKeyType,
          Pointer pvKey,
          int dwGroupId
  );
}
