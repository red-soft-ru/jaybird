package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.GDSException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 15:18
 */
public class GDSAuthException extends GDSException {
  public GDSAuthException(String message, Throwable e) {
    super(message, e);
  }

  public GDSAuthException(String message) {
    super(message);
  }

  public GDSAuthException(final int errorCode, final String strParam) {
    super(errorCode);
    setNext(new GDSAuthException("(" + strParam + ")"));
  }
}
