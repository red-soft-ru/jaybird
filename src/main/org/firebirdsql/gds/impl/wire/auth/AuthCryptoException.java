package org.firebirdsql.gds.impl.wire.auth;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 17:52
 */
public class AuthCryptoException extends Exception {
  public AuthCryptoException(final String message) {
    super(message);
  }

  public AuthCryptoException(final String message, final Throwable cause) {
    super(message + " (" + cause.getMessage() + ")", cause);
  }

  public AuthCryptoException(final Throwable cause) {
    super(cause);
  }
}
