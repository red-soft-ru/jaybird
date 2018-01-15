package org.firebirdsql.cryptoapi.windows;

import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 26.03.12
 *          Time: 22:21
 */
public class CryptoUtil {
  public static CryptoException raiseCryptoError(final String context, final int err) {
    return new CryptoException(String.format("Error in function %s occured.", context), err);
  }
}
