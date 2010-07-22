package org.firebirdsql.jdbc;

import java.sql.SQLException;

import org.firebirdsql.jca.FirebirdLocalTransaction;
public class InternalConnectionTransactionCoordinator
        extends InternalTransactionCoordinator.LocalTransactionCoordinator {
  /**
  * @param connection
  * @param localTransaction
  */
  public InternalConnectionTransactionCoordinator(AbstractConnection connection,
                                                  FirebirdLocalTransaction localTransaction) {
    super(connection, localTransaction);
  }

  /* (non-Javadoc)
   * @see org.firebirdsql.jdbc.InternalTransactionCoordinator#commit()
   */
  public void commit() throws SQLException {
//    throw new FBSQLException("Calling commit() in managed environment is not allowed.");
  }

  /* (non-Javadoc)
   * @see org.firebirdsql.jdbc.InternalTransactionCoordinator#rollback()
   */
  public void rollback() throws SQLException {
    throw new FBSQLException("Calling rollback() in managed environment is not allowed.");
  }

}
