package org.firebirdsql.gds.impl;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.Operation;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 04.07.12
 *          Time: 13:10
 */
public class GDSHelperOperation implements Operation {
  private final GDSHelper helper;

  public GDSHelperOperation(final GDSHelper helper) {
    this.helper = helper;
  }

  @Override
  public void cancelOperation() throws GDSException {
    helper.cancelOperation();
  }
}
