package org.firebirdsql.cryptoapi.cryptopro.exception;

import org.firebirdsql.cryptoapi.windows.ErrorMessages;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 20.11.12
 *          Time: 20:55
 */
public class CryptoException extends Exception {
  private int errorCode;

  public CryptoException(String s) {
    super(s);
  }

  public CryptoException(final String message, final int code) {
    super(message + (code != 0 ? " " + ErrorMessages.getMessage(code) : ""));
    this.errorCode = code;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public CryptoException(Throwable e) {
    super(e);
  }

}
