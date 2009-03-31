package org.firebirdsql.jdbc;

import org.firebirdsql.jca.FBManagedConnection;
import org.firebirdsql.jca.FirebirdLocalTransaction;

import java.sql.SQLException;

import javax.resource.ResourceException;

public class InternalCurrentConnection extends FBConnection {

  private InternalLocalTransaction localTx;

  public InternalCurrentConnection(FBManagedConnection mc) {
    super(mc);
  }

  public synchronized FirebirdLocalTransaction getLocalTransaction() {
    if (localTx == null)
      localTx = new InternalLocalTransaction(mc, this);
    return localTx;
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException {
    super.setAutoCommit(false);
    txCoordinator.setCoordinator(
            new InternalConnectionTransactionCoordinator(this, getLocalTransaction()));
  }

  public void setManagedEnvironment(boolean managedEnv) throws SQLException {
    super.setManagedEnvironment(managedEnv);
    txCoordinator.setCoordinator(
            new InternalConnectionTransactionCoordinator(this, getLocalTransaction()));
  }

  public synchronized void close() throws SQLException {
    try {
      // if there is an Xid associated with the ManagedConnection,
      // remove the association, it is no longer needed, but prevents
      // us from destroying it
      InternalLocalTransaction localTx = (InternalLocalTransaction)getLocalTransaction();
      if (localTx.inTransaction())
        localTx.end();
      // call normal clean-up process
      super.close();         
      } catch(ResourceException ex) {
          throw new FBSQLException(ex);
      }
  }
}
