package org.firebirdsql.gds;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 01.07.12
 *          Time: 14:22
 */
public interface Operation {
  public void cancelOperation() throws GDSException;
}
