package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

/**
 * The CRYPT_KEY_PROV_INFO structure contains information about a key container within a cryptographic service provider (CSP).
 *
 * Syntax:
 * typedef struct _CRYPT_KEY_PROV_INFO {
 *   LPWSTR                pwszContainerName;
 *   LPWSTR                pwszProvName;
 *   DWORD                 dwProvType;
 *   DWORD                 dwFlags;
 *   DWORD                 cProvParam;
 *   PCRYPT_KEY_PROV_PARAM rgProvParam;
 *   DWORD                 dwKeySpec;
 * } CRYPT_KEY_PROV_INFO, *PCRYPT_KEY_PROV_INFO;
 *
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.09.2011
 *          Time: 18:14:29
 */
public class _CRYPT_KEY_PROV_INFO extends Structure {
  private static final List FIELDS = Arrays.asList(
      "pwszContainerName",
      "pwszProvName",
      "dwProvType",
      "dwFlags",
      "cProvParam",
      "rgProvParam",
      "dwKeySpec"
  );

  public static class CRYPT_KEY_PROV_INFO extends _CRYPT_KEY_PROV_INFO implements Structure.ByValue { }
  public static class PCRYPT_KEY_PROV_INFO extends _CRYPT_KEY_PROV_INFO implements Structure.ByReference{ }

  public WString           pwszContainerName;
  public WString           pwszProvName;
  public int               dwProvType;
  public int               dwFlags;
  public int               cProvParam;
  public Pointer           rgProvParam; // PCRYPT_KEY_PROV_PARAM
  public int               dwKeySpec;

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }

  public _CRYPT_KEY_PROV_INFO() {}
  public _CRYPT_KEY_PROV_INFO(Pointer p) {
    super(p);
    read();
  }
}
