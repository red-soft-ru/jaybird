package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.GDSException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 15:18
 */
@Deprecated
public class GDSAuthException extends GDSException {

  public GDSAuthException(String message) {
    super(message);
  }
}
