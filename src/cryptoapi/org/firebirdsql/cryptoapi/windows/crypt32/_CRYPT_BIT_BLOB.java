package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * In a CRYPT_BIT_BLOB the last byte may contain 0-7 unused bits. Therefore, the
 * overall bit length is cbData * 8 - cUnusedBits.
 *
 * typedef struct _CRYPT_BIT_BLOB {
 *     DWORD   cbData;
 *     BYTE    *pbData;
 *     DWORD   cUnusedBits;
 * } CRYPT_BIT_BLOB, *PCRYPT_BIT_BLOB;
 *
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 20:07:27
 */
public class _CRYPT_BIT_BLOB extends Structure {
  public static class CRYPT_BIT_BLOB extends _CRYPT_BIT_BLOB implements Structure.ByValue { }
  public static class PCRYPT_BIT_BLOB extends _CRYPT_BIT_BLOB implements Structure.ByReference{ }
  
  public int cbData;
  public Pointer pbData;
  public int cUnusedBits;

  public _CRYPT_BIT_BLOB() {
    super();
  }

  public _CRYPT_BIT_BLOB(byte[] data) {
    cbData = data.length;
    pbData = new Memory(data.length);
    pbData.write(0, data, 0, data.length);
  }

  private static final List FIELDS = Arrays.asList("cbData", "pbData", "cUnusedBits");

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }
}
