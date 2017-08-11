package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 *  Type used for any algorithm
 *
 *  Where the Parameters CRYPT_OBJID_BLOB is in its encoded representation. For most
 *  algorithm types, the Parameters CRYPT_OBJID_BLOB is NULL (Parameters.cbData = 0).
 *
 *  typedef struct _CRYPT_ALGORITHM_IDENTIFIER {
 *      LPSTR               pszObjId;
 *      CRYPT_OBJID_BLOB    Parameters;
 *  } CRYPT_ALGORITHM_IDENTIFIER, *PCRYPT_ALGORITHM_IDENTIFIER;
 *
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 19.03.2011
 *          Time: 16:22:48
 */
public class _CRYPT_ALGORITHM_IDENTIFIER extends Structure {
  public static class CRYPT_ALGORITHM_IDENTIFIER extends _CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByValue { }
  public static class PCRYPT_ALGORITHM_IDENTIFIER extends _CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByReference{ }
  
  public String               pszObjId;
  public _CRYPTOAPI_BLOB.CRYPT_OBJID_BLOB Parameters;
  // size: 12
  private static final List FIELDS = Arrays.asList("pszObjId", "Parameters");

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }
}
