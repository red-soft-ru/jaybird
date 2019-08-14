package org.firebirdsql.cryptoapi.windows.crypt32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

import java.util.Arrays;
import java.util.List;

/**
 * OID Information
 *
 * typedef struct _CRYPT_OID_INFO {
 *     DWORD           cbSize;
 *     LPCSTR          pszOID;
 *     LPCWSTR         pwszName;
 *     DWORD           dwGroupId;
 *     ALG_ID  	    Algid;
 *     CRYPT_DATA_BLOB ExtraInfo;
 * } CRYPT_OID_INFO, *PCRYPT_OID_INFO;
 *
 */
public class _CRYPT_OID_INFO extends Structure {
    private static final List FIELDS = Arrays.asList(
            "cbSize",
            "pszOID",
            "pwszName",
            "dwGroupId",
            "Algid",
            "ExtraInfo"
    );

    public static class CRYPT_OID_INFO extends _CRYPT_OID_INFO implements Structure.ByValue { }
    public static class PCCRYPT_OID_INFO extends _CRYPT_OID_INFO implements Structure.ByReference{ }

    public int               cbSize;
    public String            pszOID;
    public WString           pwszName;
    public int               dwGroupId;
    public int               Algid;
    public Pointer ExtraInfo;

    @Override
    protected List getFieldOrder() {
        return FIELDS;
    }

    public _CRYPT_OID_INFO() {}
    public _CRYPT_OID_INFO(Pointer p) {
        super(p);
        read();
    }
}

