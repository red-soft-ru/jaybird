package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.AbstractFbTransaction;
import org.firebirdsql.gds.ng.TransactionState;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface.*;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.sql.SQLException;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbTransaction} for native client access using OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class ITransactionImpl extends AbstractFbTransaction {

    private static final Logger log = LoggerFactory.getLogger(ITransactionImpl.class);

    private final FbClientLibrary clientLibrary;
    private final ITransaction transaction;

    public ITransactionImpl(IDatabaseImpl database, ITransaction iTransaction, TransactionState initialState) {
        super(initialState, database);
        transaction = iTransaction;
        clientLibrary = database.getClientLibrary();
    }

    @Override
    public IDatabaseImpl getDatabase() {
        return (IDatabaseImpl)super.getDatabase();
    }

    @Override
    public int getHandle() {
        return -1;
    }

    public ITransaction getTransaction() {
        return transaction;
    }

    @Override
    public void commit() throws SQLException {
        try {
            synchronized (getSynchronizationObject()) {
                final IDatabaseImpl db = getDatabase();
                db.checkConnected();
                switchState(TransactionState.COMMITTING);
                transaction.commit(db.getStatus());
                switchState(TransactionState.COMMITTED);
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        } finally {
            final TransactionState transactionState = getState();
            if (transactionState != TransactionState.COMMITTED) {
                log.warn("Commit not completed, state was " + transactionState,
                        new RuntimeException("Commit not completed"));
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        try {
            synchronized (getSynchronizationObject()) {
                final IDatabaseImpl db = getDatabase();
                db.checkConnected();
                switchState(TransactionState.ROLLING_BACK);
                transaction.rollback(db.getStatus());
                switchState(TransactionState.ROLLED_BACK);
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        } finally {
            final TransactionState transactionState = getState();
            if (transactionState != TransactionState.ROLLED_BACK) {
                log.warn("Rollback not completed, state was " + transactionState,
                        new RuntimeException("Rollback not completed"));
            }
        }
    }

    @Override
    public void prepare(byte[] recoveryInformation) throws SQLException {
        boolean noRecoveryInfo = recoveryInformation == null || recoveryInformation.length == 0;
        try {
            synchronized (getSynchronizationObject()) {
                final IDatabaseImpl db = getDatabase();
                db.checkConnected();
                switchState(TransactionState.PREPARING);
                if (noRecoveryInfo) {
                    // TODO check for recovery information
                    transaction.prepare(db.getStatus(), 0,
                            null);
                } else {
                    transaction.prepare(db.getStatus(), (short) recoveryInformation.length,
                            recoveryInformation);
                }
                switchState(TransactionState.PREPARED);
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        } finally {
            if (getState() != TransactionState.PREPARED) {
                log.warn("Prepare not completed", new RuntimeException("Prepare not completed"));
            }
        }
    }

    @Override
    public byte[] getTransactionInfo(byte[] requestItems, int maxBufferLength) throws SQLException {
        try {
            final byte[] responseArray = new byte[maxBufferLength];
            synchronized (getSynchronizationObject()) {
                final IDatabaseImpl db = getDatabase();
                db.checkConnected();
                transaction.getInfo(db.getStatus(), (short) requestItems.length, requestItems,
                        (short) maxBufferLength, responseArray);
            }

            return responseArray;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

}
