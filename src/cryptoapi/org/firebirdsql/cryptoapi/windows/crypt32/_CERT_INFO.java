package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;

/**
 * The CERT_INFO structure contains the information of a certificate.
 *
 * Syntax:
 * typedef struct _CERT_INFO {
 *   DWORD                      dwVersion;
 *   CRYPT_INTEGER_BLOB         SerialNumber;
 *   CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
 *   CERT_NAME_BLOB             Issuer;
 *   FILETIME                   NotBefore;
 *   FILETIME                   NotAfter;
 *   CERT_NAME_BLOB             Subject;
 *   CERT_PUBLIC_KEY_INFO       SubjectPublicKeyInfo;
 *   CRYPT_BIT_BLOB             IssuerUniqueId;
 *   CRYPT_BIT_BLOB             SubjectUniqueId;
 *   DWORD                      cExtension;
 *   PCERT_EXTENSION            rgExtension;
 * } CERT_INFO, *PCERT_INFO;
 *
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 19:57:09
 */
public class _CERT_INFO extends Structure {
  private static final List FIELDS = Arrays.asList(
      "dwVersion",
      "SerialNumber",
      "SignatureAlgorithm",
      "Issuer",
      "NotBefore",
      "NotAfter",
      "Subject",
      "SubjectPublicKeyInfo",
      "IssuerUniqueId",
      "SubjectUniqueId",
      "cExtension",
      "rgExtension");

  public static class CERT_INFO extends _CERT_INFO implements Structure.ByValue { }
  public static class PCERT_INFO extends _CERT_INFO implements Structure.ByReference{
    public PCERT_INFO() {
    }

    public PCERT_INFO(Pointer p) {
      super(p);
    }
  }

  public int                                 dwVersion;
  public _CRYPTOAPI_BLOB.CRYPT_INTEGER_BLOB SerialNumber;
  public _CRYPT_ALGORITHM_IDENTIFIER.CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
  public _CERT_NAME_BLOB.CERT_NAME_BLOB      Issuer;
  public FILETIME                            NotBefore;
  public FILETIME                            NotAfter;
  public _CERT_NAME_BLOB.CERT_NAME_BLOB Subject;
  public _CERT_PUBLIC_KEY_INFO.CERT_PUBLIC_KEY_INFO SubjectPublicKeyInfo;
  public _CRYPT_BIT_BLOB.CRYPT_BIT_BLOB      IssuerUniqueId;
  public _CRYPT_BIT_BLOB.CRYPT_BIT_BLOB      SubjectUniqueId;
  public int                                 cExtension;
  public Pointer                             rgExtension; // PCERT_EXTENSION

  public _CERT_INFO() {
  }

  public _CERT_INFO(Pointer p) {
    super(p);
    read();
  }

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }

  public _CERT_EXTENSION.PCERT_EXTENSION getRgExtension() {
    return cExtension > 0 ? new _CERT_EXTENSION.PCERT_EXTENSION(rgExtension) : null;
  }
}
