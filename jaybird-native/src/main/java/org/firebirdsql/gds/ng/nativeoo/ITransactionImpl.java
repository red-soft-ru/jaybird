package org.firebirdsql.gds.ng.nativeoo;

import org.firebirdsql.gds.ng.AbstractFbTransaction;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.TransactionState;
import org.firebirdsql.jna.fbclient.CloseableMemory;
import org.firebirdsql.jna.fbclient.FbInterface.IStatus;
import org.firebirdsql.jna.fbclient.FbInterface.ITransaction;

import java.sql.SQLException;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbTransaction} for native client access using OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class ITransactionImpl extends AbstractFbTransaction {

    private static final System.Logger log = System.getLogger(ITransactionImpl.class.getName());

    private final ITransaction transaction;
    private final IStatus status;

    public ITransactionImpl(IDatabaseImpl database, ITransaction iTransaction, TransactionState initialState) {
        super(initialState, database);
        transaction = iTransaction;
        status = database.getStatus();
    }

    @Override
    public IDatabaseImpl getDatabase() {
        return (IDatabaseImpl)super.getDatabase();
    }

    @Override
    public int getHandle() {
        throw new UnsupportedOperationException( "Native OO API not support transaction handle" );
    }

    public ITransaction getTransaction() {
        return transaction;
    }

    @Override
    public void commit() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            final IDatabaseImpl db = getDatabase();
            db.checkConnected();
            switchState(TransactionState.COMMITTING);
            transaction.commit(getStatus());
            processStatus();
            switchState(TransactionState.COMMITTED);
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        } finally {
            final TransactionState transactionState = getState();
            if (transactionState != TransactionState.COMMITTED) {
                log.log(System.Logger.Level.WARNING, "Commit not completed, state was " + transactionState,
                        new RuntimeException("Commit not completed"));
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            final IDatabaseImpl db = getDatabase();
            db.checkConnected();
            switchState(TransactionState.ROLLING_BACK);
            transaction.rollback(getStatus());
            processStatus();
            switchState(TransactionState.ROLLED_BACK);
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        } finally {
            final TransactionState transactionState = getState();
            if (transactionState != TransactionState.ROLLED_BACK) {
                log.log(System.Logger.Level.WARNING, "Rollback not completed, state was " + transactionState,
                        new RuntimeException("Rollback not completed"));
            }
        }
    }

    @Override
    public void prepare(byte[] recoveryInformation) throws SQLException {
        boolean noRecoveryInfo = recoveryInformation == null || recoveryInformation.length == 0;
        try (LockCloseable ignored = withLock()) {
            CloseableMemory memRecoveryInformation = null;
            if (!noRecoveryInfo) {
                memRecoveryInformation = new CloseableMemory(recoveryInformation.length);
                memRecoveryInformation.write(0, recoveryInformation, 0, recoveryInformation.length);
            }
            final IDatabaseImpl db = getDatabase();
            db.checkConnected();
            switchState(TransactionState.PREPARING);
            if (noRecoveryInfo) {
                // TODO check for recovery information
                transaction.prepare(getStatus(), 0,
                        null);
            } else {
                transaction.prepare(getStatus(), (short) recoveryInformation.length,
                        memRecoveryInformation);
            }
            if (memRecoveryInformation != null)
                memRecoveryInformation.close();
            processStatus();
            switchState(TransactionState.PREPARED);
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        } finally {
            if (getState() != TransactionState.PREPARED) {
                log.log(System.Logger.Level.WARNING, "Prepare not completed", new RuntimeException("Prepare not completed"));
            }
        }
    }

    @Override
    public byte[] getTransactionInfo(byte[] requestItems, int maxBufferLength) throws SQLException {
        try {
            final byte[] responseArray = new byte[maxBufferLength];
            try (LockCloseable ignored = withLock();
                 CloseableMemory memRequestItems = new CloseableMemory(requestItems.length);
                 CloseableMemory memResponseArray = new CloseableMemory(responseArray.length)
            ) {
                memRequestItems.write(0, requestItems, 0, requestItems.length);
                memResponseArray.write(0, responseArray, 0, responseArray.length);
                final IDatabaseImpl db = getDatabase();
                db.checkConnected();
                transaction.getInfo(getStatus(), (short) requestItems.length, memRequestItems,
                        (short) maxBufferLength, memResponseArray);
                processStatus();
            }
            return responseArray;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private IStatus getStatus() {
        status.init();
        return status;
    }

    private void processStatus() throws SQLException {
        getDatabase().processStatus(status, null);
    }
}
