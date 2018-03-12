package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * The CERT_CONTEXT structure contains both the encoded and decoded representations of a certificate.
 * A certificate context returned by one of the functions defined in Wincrypt.h must be freed by calling the
 * CertFreeCertificateContext function. The CertDuplicateCertificateContext function can be called to make a
 * duplicate copy (which also must be freed by calling CertFreeCertificateContext).
 *
 * Syntax:
 * typedef struct _CERT_CONTEXT {
 *   DWORD      dwCertEncodingType;
 *   BYTE       *pbCertEncoded;
 *   DWORD      cbCertEncoded;
 *   PCERT_INFO pCertInfo;
 *   HCERTSTORE hCertStore;
 * } CERT_CONTEXT, *PCERT_CONTEXT;typedef const CERT_CONTEXT *PCCERT_CONTEXT;
 * 
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 19:49:08
 */
public class _CERT_CONTEXT extends Structure {
  private static final List FIELDS = Arrays.asList(
      "dwCertEncodingType",
      "pbCertEncoded",
      "cbCertEncoded",
      "pCertInfo",
      "hCertStore");

  public static class CERT_CONTEXT extends _CERT_CONTEXT implements Structure.ByValue { }
  public static class PCERT_CONTEXT extends _CERT_CONTEXT implements Structure.ByReference{
    public PCERT_CONTEXT() {
    }

    public PCERT_CONTEXT(Pointer p) {
      super(p);
    }
  }
  public static class PCCERT_CONTEXT extends PCERT_CONTEXT implements Structure.ByReference {
    public PCCERT_CONTEXT() {
    }

    public PCCERT_CONTEXT(Pointer p) {
      super(p);
    }
  }

  /**
    <pre>
    Type of encoding used. It is always acceptable to specify both the certificate and message encoding types
    by combining them with a bitwise-OR operation as shown in the following example:

    X509_ASN_ENCODING | PKCS_7_ASN_ENCODING

    Currently defined encoding types are:
    </pre>
        <li> X509_ASN_ENCODING
        <li> PKCS_7_ASN_ENCODING
   */
  public int dwCertEncodingType;

  /** A pointer to a buffer that contains the encoded certificate. */
  public Pointer pbCertEncoded;

  /** The size, in bytes, of the encoded certificate. */
  public int cbCertEncoded;

  /** The address of a CERT_INFO structure that contains the certificate information. */
  //public Pointer pCertInfo;
  public _CERT_INFO.PCERT_INFO pCertInfo;

  /** A handle to the certificate store that contains the certificate context. */
  public Pointer hCertStore;

  public _CERT_CONTEXT() {
  }

  public _CERT_CONTEXT(Pointer p) {
    super(p);
    read();
  }

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }
}
