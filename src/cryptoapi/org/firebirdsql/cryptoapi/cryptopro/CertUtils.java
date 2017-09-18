package org.firebirdsql.cryptoapi.cryptopro;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.log4j.Logger;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.util.Base64;
import org.firebirdsql.cryptoapi.windows.Wincrypt;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;
import org.firebirdsql.cryptoapi.windows.crypt32.Crypt32;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT.PCCERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT.PCERT_CONTEXT;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.firebirdsql.cryptoapi.windows.Wincrypt.*;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 16.06.2011
 *          Time: 15:49:33
 */
@SuppressWarnings("UnusedDeclaration")
public class CertUtils {
  public final static Logger LOGGER = Logger.getLogger(CertUtils.class);
  public static final String CA_STORE = "CA";
  public static final String ROOT_STORE = "ROOT";

  public static PCCERT_CONTEXT getCertContext(final String base64cert) throws CryptoException {
    return Crypt32.certCreateCertificateContext(Wincrypt.X509_ASN_ENCODING | Wincrypt.PKCS_7_ASN_ENCODING, decode(base64cert));
  }

  public static PCCERT_CONTEXT findCertificate(Pointer certStore, PCERT_CONTEXT certificate) {
    return Crypt32.certFindCertificateInStore(certStore, X509_ASN_ENCODING | PKCS_7_ASN_ENCODING, 0, CERT_FIND_EXISTING, certificate.getPointer(), null);

  }

  public static byte[] decode(String base64Encoded) throws CryptoException {
    final byte[] encoded;
    try {
      encoded = Base64.decode(extractBase64Encoded(base64Encoded));
    } catch (IOException e) {
      throw new CryptoException(e);
    }
    return encoded;
  }

  public static String extractBase64Encoded(String base64Encoded) throws IOException {
    final StringBuilder buf = new StringBuilder(base64Encoded.length());
    final BufferedReader r = new BufferedReader(new StringReader(base64Encoded));
    String line;
    do {
      line = r.readLine();
      if (line != null) {
        line = line.trim();
        if (line.length() > 0 && line.charAt(0) != '-') buf.append(line);
      }
    } while (line != null);
    return buf.toString();
  }

  public static byte[] getCertEncoded(_CERT_CONTEXT c) {
    return c.pbCertEncoded.getByteArray(0, c.cbCertEncoded);
  }

  public static X509Certificate generateCertificate(byte[] data) throws CertificateException, CryptoException {
    try {
      final ByteArrayInputStream bis = new ByteArrayInputStream(data);
      final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      final Certificate c = certificateFactory.generateCertificate(bis);
      return (X509Certificate) c;
    } catch (Throwable te) {
      throw new CryptoException(te);
    }
  }
}