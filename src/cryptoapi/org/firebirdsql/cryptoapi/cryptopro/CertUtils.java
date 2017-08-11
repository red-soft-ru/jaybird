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

  private static void readAvailableCertificatesFromContainers(Pointer provHandle, Map<String, ContainerInfo> res) throws CryptoException, CertificateException {
    final List<String> list = Advapi.enumContainers(provHandle);
    for (String container : list) {
      final byte[] certData;
      try {
        ContainerInfo ci = getCertificateByContainerName(container);
        if (ci != null) {
          if (!res.containsKey(ci.containerName))
            res.put(ci.containerName, ci);
        } else
          LOGGER.warn("Skipping container w/o certificate: " + container);
      } catch (CryptoException e) {
        LOGGER.warn("Skipping container: " + container, e);
      }
    }
  }

  private static void readAvailableCertificatesFromSystemStore(String storeName, Map<String, ContainerInfo> res) throws CryptoException, CertificateException {
    Pointer hStore = Crypt32.certOpenSystemStore(Pointer.NULL, storeName);
    try {
      PCCERT_CONTEXT certContext = null;
      do {
        final PointerByReference provHandle = new PointerByReference();
        final IntByReference keySpec = new IntByReference();
        final IntByReference callerFreeProv = new IntByReference();
        certContext = Crypt32.certEnumCertificatesInStore(hStore, certContext);
        if (certContext != null) {
          byte[] certEncoded = getCertEncoded(certContext);
          if (Crypt32.cryptAcquireCertificatePrivateKey(certContext, CRYPT_ACQUIRE_SILENT_FLAG, provHandle, keySpec, callerFreeProv))
            try {
              final Pointer userKeyHandle = Advapi.cryptGetUserKey(provHandle.getValue(), keySpec.getValue());
              if (userKeyHandle != null)
                try {
                  byte[] keyParam = Advapi.cryptGetProvParam(provHandle.getValue(), Wincrypt.PP_CONTAINER, 0);
                  if (keyParam != null) {
                    String containerName = Native.toString(keyParam);
                    if (!res.containsKey(containerName))
                      res.put(containerName, new ContainerInfo(containerName, certEncoded, keySpec.getValue()));
                  }
                } finally {
                  Advapi.cryptDestroyKey(userKeyHandle);
                }
            } finally {
              if (callerFreeProv.getValue() != 0)
                Advapi.cryptReleaseContext(provHandle.getValue());
            }
          else
            LOGGER.warn("Skipping certificate w/o private key: " + generateCertificate(certEncoded));
        }
      } while (certContext != null);
    } finally {
      Crypt32.certCloseStore(hStore);
    }
  }

  /**
   * Возвращает список сертификатов в доступных на данный момент контейнерах
   */
  public static List<ContainerInfo> getAvailableContainersCertificatesList(Pointer provHandle) throws CryptoException, CertificateException {
    final List<ContainerInfo> res = new ArrayList<ContainerInfo>();
    Map<String, ContainerInfo> containers = new HashMap<String, ContainerInfo>();
    readAvailableCertificatesFromContainers(provHandle, containers);
    readAvailableCertificatesFromSystemStore("MY", containers);
    res.addAll(containers.values());
    return res;
  }

  private static ContainerInfo getCertificateByContainerName(Pointer prov, String container, int flag, boolean certShouldExists) {
    Pointer p;
    try {
      p = Advapi.cryptGetUserKey(prov, flag);
      if (p != null) {
        byte[] certData;
        try {
          certData = Advapi.cryptGetKeyParam(p, KP_CERTIFICATE);
        } catch (CryptoException ignored) {
          if (certShouldExists)
            return null;
          else
            certData = null;
        } finally {
          Advapi.cryptDestroyKey(p);
        }
        return new ContainerInfo(container, certData, flag);
      }
    } catch (CryptoException ignored) {
    }
    return null;
  }

  public static ContainerInfo getCertificateByContainerName(String containerName, byte[] certData) throws CryptoException {
    final Pointer prov = Advapi.cryptAcquireContext(containerName, null, CryptoProProvider.PROV_DEFAULT, 0);
    try {
      ContainerInfo exchange = getCertificateByContainerName(prov, containerName, AT_KEYEXCHANGE, false);
      if (exchange != null && exchange.certData != null)
        return exchange;
      ContainerInfo signature = getCertificateByContainerName(prov, containerName, AT_SIGNATURE, false);
      if (signature != null && signature.certData != null)
        return signature;
      if (certData == null)
        return null;
      ContainerInfo container = exchange != null ? exchange : signature;
      if (container != null)
        container.certData = certData;
      return container;
    } finally {
      Advapi.cryptReleaseContext(prov);
    }
  }

  public static ContainerInfo getCertificateByContainerName(String containerName) throws CryptoException {
    return getCertificateByContainerName(containerName, null);
  }
}