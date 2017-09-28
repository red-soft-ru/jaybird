package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * typedef struct _CRYPTOAPI_BLOB {
 *     DWORD   cbData;
 *     BYTE    *pbData;
 * } CRYPT_INTEGER_BLOB, *PCRYPT_INTEGER_BLOB,
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 19:59:13
 */
public class _CRYPTOAPI_BLOB extends Structure {
  private static final List<String> FIELDS = Arrays.asList("cbData", "pbData");

  public static class CRYPT_INTEGER_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByValue { }
  public static class CRYPT_OBJID_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByValue { }
  public static class CRYPT_DATA_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByValue { }
  public static class PCRYPT_DATA_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByReference {
    public PCRYPT_DATA_BLOB() {
    }

    public PCRYPT_DATA_BLOB(Pointer p) {
      super(p);
    }
  }
  public static class CERT_RDN_VALUE_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByValue { }
  public static class PCERT_RDN_VALUE_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByReference{ }
  public static class CRYPT_ATTR_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByValue {}
  public static class PCRYPT_ATTR_BLOB extends _CRYPTOAPI_BLOB implements Structure.ByReference {}

  public int cbData; // data length
  public Pointer pbData; // data

  public _CRYPTOAPI_BLOB() {
  }

  public _CRYPTOAPI_BLOB(Pointer p) {
    super(p);
    read();
  }

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }

  public _CRYPTOAPI_BLOB(Structure s) {
    super(s.getPointer());
    s.write();
    read();
  }
}
