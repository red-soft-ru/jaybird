package org.firebirdsql.cryptoapi.windows.sspi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * User: xmel
 * Date: 6/13/12
 * Time: 11:41 PM
 */
public abstract class _ALG_ID extends Structure {
  private static final List FIELDS = Arrays.asList("value");

  public static class ALG_ID extends _ALG_ID implements Structure.ByValue { }
  public static class PALG_ID extends _ALG_ID implements Structure.ByReference { }

  public int value;

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }
}
