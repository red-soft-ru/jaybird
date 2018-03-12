package org.firebirdsql.cryptoapi.windows.crypt32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 19.03.2011
 *          Time: 16:29:09
 */
public class _CERT_NAME_BLOB extends _CRYPTOAPI_BLOB {
  public static class CERT_NAME_BLOB extends _CERT_NAME_BLOB implements Structure.ByValue { }
  public static class PCERT_NAME_BLOB extends _CERT_NAME_BLOB implements Structure.ByReference{
    public PCERT_NAME_BLOB() {
    }

    public PCERT_NAME_BLOB(Pointer p) {
      super(p);
    }

    public PCERT_NAME_BLOB(Structure s) {
      super(s);
    }
  }

  // PCRYPTOAPI_BLOB

  public _CERT_NAME_BLOB() {
  }

  public _CERT_NAME_BLOB(Pointer p) {
    super(p);
  }

  public _CERT_NAME_BLOB(Structure s) {
    super(s);
  }
}
