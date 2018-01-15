package org.firebirdsql.cryptoapi.windows.crypt32;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Type used for an extension to an encoded content
 * Where the Value's CRYPT_OBJID_BLOB is in its encoded representation.
 *
 * typedef struct _CERT_EXTENSION {
 *     LPSTR               pszObjId;
 *     BOOL                fCritical;
 *     CRYPT_OBJID_BLOB    Value;
 * } CERT_EXTENSION, *PCERT_EXTENSION;
 *
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 20:09:29
 */
public class _CERT_EXTENSION extends Structure {

  public static class CERT_EXTENSION extends _CERT_EXTENSION implements Structure.ByValue {
    public CERT_EXTENSION(Pointer p) {
      super(p);
    }
  }
  public static class PCERT_EXTENSION extends _CERT_EXTENSION implements Structure.ByReference{
    public PCERT_EXTENSION() {
    }

    public PCERT_EXTENSION(Pointer p) {
      super(p);
      if (p != null)
        read();
    }
  }

  public String                   pszObjId;
  public boolean                  fCritical;
  public _CRYPTOAPI_BLOB.CRYPT_OBJID_BLOB Value; //CRYPT_OBJID_BLOB

  private static final List<String> FIELDS = Arrays.asList("pszObjId", "fCritical", "Value");

  public _CERT_EXTENSION() {
  }

  public _CERT_EXTENSION(Pointer p) {
    super(p);
    read();
  }

  @Override
  protected List getFieldOrder() {
    return FIELDS;
  }
}
