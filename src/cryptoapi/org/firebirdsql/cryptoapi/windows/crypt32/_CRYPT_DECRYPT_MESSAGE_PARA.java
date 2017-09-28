package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

/**
 * typedef struct _CRYPT_DECRYPT_MESSAGE_PARA {
 * DWORD      cbSize;
 * DWORD      dwMsgAndCertEncodingType;
 * DWORD      cCertStore;
 * HCERTSTORE *rghCertStore;
 * DWORD      dwFlags;
 } CRYPT_DECRYPT_MESSAGE_PARA, *PCRYPT_DECRYPT_MESSAGE_PARA;

 * Created by vasiliy on 11.07.17.
 */
public class _CRYPT_DECRYPT_MESSAGE_PARA extends Structure {
  private static final List FIELDS = Arrays.asList(
      "cbSize",
      "dwMsgAndCertEncodingType",
      "cCertStore",
      "rghCertStore",
      "dwFlags"
  );

  public static class CRYPT_DECRYPT_MESSAGE_PARA extends _CRYPT_DECRYPT_MESSAGE_PARA implements Structure.ByValue { }
  public static class PCRYPT_DECRYPT_MESSAGE_PARA extends _CRYPT_DECRYPT_MESSAGE_PARA implements Structure.ByReference{
    public PCRYPT_DECRYPT_MESSAGE_PARA() {
    }

    public PCRYPT_DECRYPT_MESSAGE_PARA(Pointer p) {
      super(p);
    }
  }
  public static class PCCRYPT_DECRYPT_MESSAGE_PARA extends PCRYPT_DECRYPT_MESSAGE_PARA implements Structure.ByReference {
    public PCCRYPT_DECRYPT_MESSAGE_PARA() {
    }

    public PCCRYPT_DECRYPT_MESSAGE_PARA(Pointer p) {
      super(p);
    }
  }

  /** Size of this structure in bytes. */
  public int cbSize;

  /**
   * Type of encoding used. It is always acceptable to specify both the certificate and
   * message encoding types by combining them with a bitwise-OR operation as shown in the following example:
   * X509_ASN_ENCODING | PKCS_7_ASN_ENCODING
   * Currently defined encoding types are:
   * X509_ASN_ENCODING
   * PKCS_7_ASN_ENCODING
   */
  public int dwMsgAndCertEncodingType;

  /** Number of elements in the rghCertStore array. */
  public int cCertStore;

  /**
   * Array of certificate store handles.
   *
   * These certificate store handles are used to obtain the certificate context to use for decrypting a message.
   * For more information, see the decryption functions CryptDecryptMessage, and CryptDecryptAndVerifyMessageSignature.
   * An encrypted message can have one or more recipients. The recipients are identified by
   * a unique certificate identifier, often the hash of the certificate issuer and serial number.
   * The certificate stores are searched to find a certificate context corresponding to the unique identifier.
   *
   * Recipients can also be identified by their KeyId. Both Key Agreement (Diffie-Hellman) and
   * Key Transport (RSA) recipients are supported.
   *
   * Only certificate contexts in the store with one of the following properties,
   * CERT_KEY_PROV_INFO_PROP_ID, or CERT_KEY_CONTEXT_PROP_ID can be used.
   * These properties specify the location of a needed private exchange key.
   */
  public PointerByReference rghCertStore;

  /** The CRYPT_MESSAGE_SILENT_KEYSET_FLAG can be set to suppress any UI by the CSP.
   * For more information about the CRYPT_SILENT flag, see CryptAcquireContext.
   */
  public int dwFlags;

  public _CRYPT_DECRYPT_MESSAGE_PARA() {
  }

  public _CRYPT_DECRYPT_MESSAGE_PARA(Pointer p) {
    super(p);
    read();
  }

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }
}
