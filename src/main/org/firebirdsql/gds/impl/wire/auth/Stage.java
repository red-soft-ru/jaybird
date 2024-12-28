package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.impl.wire.ByteBuffer;

import java.sql.SQLException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 23:45
 */
public interface Stage {
  boolean stage(ByteBuffer data) throws SQLException;

  Stage nextStage();
}
