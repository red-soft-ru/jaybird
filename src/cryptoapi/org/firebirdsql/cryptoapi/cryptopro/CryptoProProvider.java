package org.firebirdsql.cryptoapi.cryptopro;

import com.sun.jna.Pointer;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.windows.Wincrypt;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;

import java.security.Provider;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 17.03.2011
 *          Time: 17:30:51
 */
@SuppressWarnings({"unchecked"})
public class CryptoProProvider extends Provider {

  public static int PROV_GOST_94_DH       = 71;
  public static int PROV_GOST_2001_DH     = 75;
  public static int PROV_DEFAULT = PROV_GOST_2001_DH;

  public static String NAME = "RedSoftCryptoProProvider";

  /**
   * Constructs a provider with the specified name, version number,
   * and information.
   *
   * @param name    the provider name.
   * @param version the provider version number.
   * @param info    a description of the provider and its services.
   */
  protected CryptoProProvider(String name, double version, String info) {
    super(name, version, info);
  }

  public static Pointer acquireContext() throws CryptoException {
    return Advapi.cryptAcquireContext(null, null, CryptoProProvider.PROV_DEFAULT, Wincrypt.CRYPT_VERIFYCONTEXT);
  }
}
