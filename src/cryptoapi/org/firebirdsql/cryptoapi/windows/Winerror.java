package org.firebirdsql.cryptoapi.windows;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 20.09.2011
 *          Time: 22:22:37
 */
public class Winerror {
  public static final int ERROR_CANCELLED = 0x4C7;
  public static final int ERROR_CANCELLED_BY_USER = 0x8010006E;
  public static final int ERROR_CONTAINER_ALREADY_EXISTS = 0x8009000F;
  public static final int NTE_KEYSET_NOT_DEF =  0x80090019;
  public static final int NTE_BAD_KEYSET_PARAM = 0x8009001F;
  public static final int ERROR_NTE_BAD_KEYSET = 0x80090016;
  public static final int NTE_SILENT_CONTEXT   = 0x80090022;

  public static final int CRYPT_E_EXISTS = 0x80092005;
  public static final int CRYPT_E_NO_SIGNER = 0x8009200E;
  public static final int CRYPT_E_HASH_VALUE = 0x80091007;
  public static final int CRYPT_BAD_DATA = 0x80090005;

  public static final int NTE_BAD_SIGNATURE = 0x80090006;
  public static final int NTE_FAIL = 0x80090020;
  public static final int ERROR_NO_MORE_ITEMS = 0x103;
}
