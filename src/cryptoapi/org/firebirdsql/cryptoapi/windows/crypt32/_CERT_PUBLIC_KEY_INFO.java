package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * typedef struct _CERT_PUBLIC_KEY_INFO {
 * CRYPT_ALGORITHM_IDENTIFIER    Algorithm;
 * CRYPT_BIT_BLOB                PublicKey;
 * } CERT_PUBLIC_KEY_INFO, *PCERT_PUBLIC_KEY_INFO;
 *
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 20:06:13
 */
public class _CERT_PUBLIC_KEY_INFO extends Structure {
  public static class CERT_PUBLIC_KEY_INFO extends _CERT_PUBLIC_KEY_INFO implements Structure.ByValue {
    public CERT_PUBLIC_KEY_INFO() {
    }

    public CERT_PUBLIC_KEY_INFO(Pointer p) {
      super(p);
    }
  }
  public static class PCERT_PUBLIC_KEY_INFO extends _CERT_PUBLIC_KEY_INFO implements Structure.ByReference {
    public PCERT_PUBLIC_KEY_INFO(Pointer p) {
      super(p);
    }
  }

  public _CRYPT_ALGORITHM_IDENTIFIER.CRYPT_ALGORITHM_IDENTIFIER Algorithm;
  public _CRYPT_BIT_BLOB.CRYPT_BIT_BLOB PublicKey;

  private static final List FIELDS = Arrays.asList("Algorithm", "PublicKey");

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }

  public _CERT_PUBLIC_KEY_INFO() {
  }

  public _CERT_PUBLIC_KEY_INFO(Pointer p) {
    super(p);
    read();
  }
}
