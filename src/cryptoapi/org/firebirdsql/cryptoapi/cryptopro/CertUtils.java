package org.firebirdsql.cryptoapi.cryptopro;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.util.Base64;
import org.firebirdsql.cryptoapi.windows.Win32Api;
import org.firebirdsql.cryptoapi.windows.Wincrypt;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;
import org.firebirdsql.cryptoapi.windows.crypt32.Crypt32;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT.PCCERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CERT_CONTEXT.PCERT_CONTEXT;
import org.firebirdsql.cryptoapi.windows.crypt32._CRYPT_KEY_PROV_INFO.PCRYPT_KEY_PROV_INFO;
import org.firebirdsql.cryptoapi.windows.crypt32._CRYPT_OID_INFO;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.firebirdsql.cryptoapi.windows.Wincrypt.*;
import static org.firebirdsql.cryptoapi.windows.Winerror.ERROR_NO_MORE_ITEMS;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 16.06.2011
 *          Time: 15:49:33
 */
@SuppressWarnings("UnusedDeclaration")
public class CertUtils {
  public final static Logger LOGGER = LoggerFactory.getLogger(CertUtils.class);
  public static final String CA_STORE = "CA";
  public static final String ROOT_STORE = "ROOT";

  public static final String ALIAS_PART_ISSUER = "; Issued=";
  public static final String ALIAS_PART_SERIAL = "; Serial Number=";

  public static PCCERT_CONTEXT getCertContext(final String base64cert) throws CryptoException {
    return Crypt32.certCreateCertificateContext(Wincrypt.X509_ASN_ENCODING | Wincrypt.PKCS_7_ASN_ENCODING, decode(base64cert));
  }

  public static Pointer findCertificate(Pointer certStore, PCERT_CONTEXT certificate) {
    return Crypt32.certFindCertificateInStore(certStore, X509_ASN_ENCODING | PKCS_7_ASN_ENCODING, 0, CERT_FIND_EXISTING, certificate.getPointer(), null);
  }

  public static String decodeX500Name(String name) {
    return isEmpty(name) || name.length() < 2 || name.charAt(0) != '\"' ?
        name :
        name.substring(1, name.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
  }

  public static boolean isEmpty(String s) {
    return isEmpty(s, true);
  }

  public static boolean isEmpty(String s, Boolean doTrim) {
    return doTrim ? s == null || s.trim().length() == 0 : s == null || s.length() == 0;
  }

  public static String decodeName(String x500String, String name) {
    if (!name.endsWith("="))
      name += "=";
    final List<String> ss = new ArrayList<String>();
    getListItems(x500String, ',', ss, '"', false);
    for (String s : ss) {
      final String part = s.trim();
      if (part.startsWith(name)) {
        final String value = decodeX500Name(part.substring(name.length()).trim());
        return unquote(value, '"');
      }
    }
    return null;
  }

  public static String getProvName(PCCERT_CONTEXT certContext) throws CryptoException {
    final PointerByReference provHandle = new PointerByReference();
    final IntByReference keySpec = new IntByReference();
    final IntByReference pfCallerFreeProv = new IntByReference();
    final boolean hasPrivateKey = Crypt32.cryptAcquireCertificatePrivateKey(certContext, 0, provHandle, keySpec, pfCallerFreeProv);
    try {
      if (!hasPrivateKey)
        throw new CryptoException("Certificate does not contain private key", Advapi.getLastError());
      return getProvName(provHandle.getValue());
    } finally {
      if (pfCallerFreeProv.getValue() != 0 && provHandle.getValue() != null)
        Advapi.cryptReleaseContext(provHandle.getValue());
    }
  }

  public static String getProvName(String container) throws CryptoException {
    final Pointer provHandle = Advapi.cryptAcquireContext(container, null, CryptoProProvider.PROV_DEFAULT, 0);
    try {
      if (provHandle == null)
        throw new CryptoException("Container " + container + " not found", Advapi.getLastError());
      return getProvName(provHandle);
    } finally {
      Advapi.cryptReleaseContext(provHandle);
    }
  }

  public static String getProvName(Pointer provHandle) throws CryptoException {
    final byte[] bytes = Advapi.cryptGetProvParam(provHandle, Wincrypt.PP_NAME, 0);
    return Native.toString(bytes);
  }

  public static <T extends Collection<String>> T getListItems(String str, char delimeter, T list) {
    return getListItems(str, delimeter, list, null, false);
  }

  public static <T extends Collection<String>> T getListItems(String str, char delimeter, T list, Character quote, boolean unquote) {
    return getListItems(str, String.valueOf(delimeter), false, list, quote, unquote);
  }

  public static <T extends Collection<String>> T getListItems(String str,
                                                              String delimeterList,
                                                              boolean anyDelimiter,
                                                              T list, Character quote, boolean unquote) {
    if (str != null) {
      Character delimiter = delimeterList.length() > 1 ? null : delimeterList.charAt(0);
      final char q = quote == null ? '\0' : quote;
      quote = unquote ? quote : null;
      boolean quoted = false;
      final int strMaxIndex = str.length() - 1;
      int last_index = 0;
      for (int i = 0; i <= strMaxIndex; i++) {
        final char c = str.charAt(i);
        if (c == q && i > 0 && str.charAt(i - 1) != '\\')
          quoted = !quoted;
        if ((delimiter != null && c == delimiter || delimiter == null && delimeterList.indexOf(c) >= 0) && !quoted) {
          if (i > last_index)
            list.add(unquote(str.substring(last_index, i).trim(), quote));
          last_index = i + 1;
          if (!anyDelimiter && delimiter == null)
            delimiter = c;
        } else if (i == strMaxIndex)
          list.add(unquote(str.substring(last_index, ++i).trim(), quote));
      }
    }
    return list;
  }

  public static String unquote(String s, Character quote) {
    if (quote == null || s == null)
      return s;
    while (s.charAt(0) == quote && s.charAt(s.length() - 1) == quote)
      s = s.substring(1, s.length() - 1);
    return s;
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

  public static String encodeSerialNumber(BigInteger value) {
    return value != null ? value.toString(16).toUpperCase() : null;
  }

  public static String encodeIssuer(String value) {
    if (value == null)
      return null;
    String res = decodeName(value, "CN");
    if (res == null)
      res = decodeName(value, "O");
    return res;
  }

  public static String encodeAlias(X509Certificate cert) {
    if (cert != null && cert.getSubjectDN() != null) {
      String res = "" + cert.hashCode();
      if (cert.getSubjectDN() != null) {
        res = decodeName(cert.getSubjectDN().toString(), "CN");
        if (res == null)
          res = cert.getSubjectDN().toString();
      }
      if (cert.getIssuerDN() != null) {
        String issuer = encodeIssuer(cert.getIssuerDN().toString());
        if (issuer != null)
          res += ALIAS_PART_ISSUER + issuer;
      }
      if (cert.getSerialNumber() != null)
        res += ALIAS_PART_SERIAL + encodeSerialNumber(cert.getSerialNumber());
      return res;
    } else
      return null;
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
      Pointer certContext = null;
      do {
        final PointerByReference provHandle = new PointerByReference();
        final IntByReference keySpec = new IntByReference();
        final IntByReference callerFreeProv = new IntByReference();
        certContext = Crypt32.certEnumCertificatesInStore(hStore, certContext);
        if (certContext != null) {
          byte[] certEncoded = getCertEncoded(new _CERT_CONTEXT(certContext));
          if (Crypt32.cryptAcquireCertificatePrivateKey(certContext, CRYPT_ACQUIRE_SILENT_FLAG, provHandle, keySpec, callerFreeProv))
            try {
              final Pointer userKeyHandle = Advapi.cryptGetUserKey(provHandle.getValue(), keySpec.getValue());
              if (userKeyHandle != null)
                try {
                  byte[] keyParam = Advapi.cryptGetProvParam(provHandle.getValue(), Wincrypt.PP_CONTAINER, 0);
                  if (keyParam != null) {
                    String containerName = Native.toString(keyParam);
                    if (!res.containsKey(containerName)) {
                      byte[] provParam = Advapi.cryptGetProvParam(provHandle.getValue(), PP_PROVTYPE, 0);
                      int provType = Win32Api.byteArrayToInt(provParam);
                      res.put(containerName, new ContainerInfo(containerName, certEncoded, keySpec.getValue(), provType));
                    }
                  }
                } finally {
                  Advapi.cryptDestroyKey(userKeyHandle);
                }
            } finally {
              if (callerFreeProv.getValue() != 0)
                Advapi.cryptReleaseContext(provHandle.getValue());
            }
          else
            LOGGER.warn("Skipping certificate w/o private key: " + encodeAlias(generateCertificate(certEncoded)));
        }
      } while (certContext != null);
    } finally {
      Crypt32.certCloseStore(hStore);
    }
  }

  public static int getAlgorithmIDByProvider(Pointer provHandle) throws CryptoException {
    int flags = CRYPT_FIRST;
    for (int idx = 0; ; idx++) {
      if (idx != 0)
        flags = 0;

      byte[] keyParam = Advapi.cryptGetProvParam(provHandle, PP_ENUMALGS, flags, 1000);
      int errCode = Advapi.getLastError();
      if (errCode == ERROR_NO_MORE_ITEMS)
        break;
      if (keyParam != null) {
        int value = Win32Api.byteArrayToInt(keyParam);

        _CRYPT_OID_INFO.PCCRYPT_OID_INFO cryptInfo = Crypt32.cryptFindOIDInfo(CRYPT_OID_INFO_ALGID_KEY, value, 1);

        if (cryptInfo != null) {
          cryptInfo = null;
          return value;
        }
      }
    }

    return 0;
  }

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
        int provType = 0;
        try {
          certData = Advapi.cryptGetKeyParam(p, KP_CERTIFICATE);
          byte[] data = Advapi.cryptGetProvParam(p, PP_PROVTYPE, 0);
          provType = Win32Api.byteArrayToInt(data);
        } catch (CryptoException ignored) {
          if (certShouldExists)
            return null;
          else
            certData = null;
        } finally {
          Advapi.cryptDestroyKey(p);
        }
        return new ContainerInfo(container, certData, flag, provType);
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

  public static void setCertificateContainerNameParam(PCCERT_CONTEXT certContext, String containerName, int provType)
          throws CryptoException {
    // Add container name to certificate
    if (!isEmpty(containerName)) {
      final PCRYPT_KEY_PROV_INFO keyProvInfo = new PCRYPT_KEY_PROV_INFO();
      keyProvInfo.pwszContainerName = new WString(containerName);
      keyProvInfo.pwszProvName =  Platform.isLinux() ? new WString(CertUtils.getProvName(containerName)) : null;// (Roman: on linux we got SIGSEGV with null value)
      keyProvInfo.dwProvType = provType;
      keyProvInfo.dwFlags = 0;
      keyProvInfo.cProvParam = 0;
      keyProvInfo.rgProvParam = null;
      keyProvInfo.dwKeySpec = Wincrypt.AT_KEYEXCHANGE;
      Crypt32.certSetCertificateContextProperty(certContext, Wincrypt.CERT_KEY_PROV_INFO_PROP_ID, 0, keyProvInfo);
    }
  }

  public static int getAlgorithmIDByContext(PCCERT_CONTEXT context) {
    if (context.pCertInfo.SubjectPublicKeyInfo.Algorithm.pszObjId.equals(szOID_CP_GOST_R3410_12_256)) {
      return Wincrypt.CALG_GR3411_12_256;
    } else if (context.pCertInfo.SubjectPublicKeyInfo.Algorithm.pszObjId.equals(szOID_CP_GOST_R3410_12_512)) {
      return Wincrypt.CALG_GR3411_12_512;
    }
    return Wincrypt.CALG_GR3411;
  }
}