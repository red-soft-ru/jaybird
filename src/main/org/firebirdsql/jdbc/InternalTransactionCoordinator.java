/*
 * Firebird Open Source JavaEE Connector - JDBC Driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.jdbc;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.jca.FBManagedConnection;
import org.firebirdsql.jca.FirebirdLocalTransaction;
import org.firebirdsql.util.SQLExceptionChainBuilder;

import javax.resource.ResourceException;
import java.sql.SQLException;
import java.util.*;

import static org.firebirdsql.gds.ISCConstants.isc_tpb_autocommit;

/**
 * Transaction coordinator for the {@link org.firebirdsql.jdbc.FBConnection} class.
 */
public final class InternalTransactionCoordinator implements FBObjectListener.StatementListener,
        FBObjectListener.BlobListener, Synchronizable {

    private final AbstractConnection connection;
    private AbstractTransactionCoordinator coordinator;

    InternalTransactionCoordinator(AbstractConnection connection) {
        this.connection = connection;
    }

    public Object getSynchronizationObject() throws SQLException {
        if (coordinator instanceof AutoCommitCoordinator || coordinator instanceof FirebirdAutoCommitCoordinator) {
            return getConnection();
        }
        // TODO Suspicious: using new sync-object every time
        return new Object();
    }

    /**
     * Switches the auto commit coordinator if currently in a different auto commit mode.
     *
     * @param autoCommit
     *         Value for auto commit
     * @throws SQLException
     *         If {@code autoCommit} is {@code true} and the connection is currently enlisted in a
     *         distributed transaction
     */
    void switchTransactionCoordinator(boolean autoCommit) throws SQLException {
        if (coordinator != null && getAutoCommit() == autoCommit) {
            return;
        }

        setTransactionCoordinator(false, autoCommit);
    }

    /**
     * Sets the transaction coordinator.
     * <p>
     * In most cases {@link #switchTransactionCoordinator(boolean)} should be called.
     * </p>
     *
     * @param managedConnection
     *         Whether this is a managed connection
     * @param autoCommit
     *         Value for auto commit
     * @throws SQLException
     *         If {@code autoCommit} is {@code true}, and {@code managedConnection} is {@code false} and
     *         the connection is currently enlisted in a distributed transaction
     */
    void setTransactionCoordinator(boolean managedConnection, boolean autoCommit) throws SQLException {
        FBManagedConnection mc = connection.getManagedConnection();
        InternalTransactionCoordinator.AbstractTransactionCoordinator coordinator;
        if (managedConnection && mc.inDistributedTransaction()) {
            coordinator = new ManagedTransactionCoordinator(connection);
        } else if (autoCommit) {
            if (mc.inDistributedTransaction()) {
                throw new SQLException("Connection enlisted in distributed transaction",
                        FBSQLException.SQL_STATE_INVALID_TX_STATE);
            }
            if (connection.isUseFirebirdAutoCommit()) {
                coordinator = new FirebirdAutoCommitCoordinator(connection, connection.getLocalTransaction());
            } else {
                coordinator = new AutoCommitCoordinator(connection, connection.getLocalTransaction());
            }
        } else {
            coordinator = new LocalTransactionCoordinator(connection, connection.getLocalTransaction());
        }
        setCoordinator(coordinator);
    }

    public boolean getAutoCommit() throws SQLException {
        synchronized (getSynchronizationObject()) {
            return coordinator != null && coordinator.isAutoCommit();
        }
    }

    public void executionStarted(AbstractStatement stmt) throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.executionStarted(stmt);
        }
    }

    public AbstractConnection getConnection() throws SQLException {
        return connection;
    }

    public void statementClosed(AbstractStatement stmt) throws SQLException {
        coordinator.statementClosed(stmt);
    }

    public void statementCompleted(AbstractStatement stmt) throws SQLException {
        statementCompleted(stmt, true);
    }

    public void statementCompleted(AbstractStatement stmt, boolean success) throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.statementCompleted(stmt, success);
        }
    }

    public void executionCompleted(FirebirdBlob blob) throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.executionCompleted(blob);
        }
    }

    public void executionStarted(FirebirdBlob blob) throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.executionStarted(blob);
        }
    }

    public void ensureTransaction() throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.ensureTransaction();
        }
    }

    public void commit() throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.commit();
        }
    }

    public void rollback() throws SQLException {
        synchronized (getSynchronizationObject()) {
            coordinator.rollback();
        }
    }

    void handleConnectionClose() throws SQLException {
        coordinator.handleConnectionClose();
    }

    protected void setCoordinator(AbstractTransactionCoordinator coordinator) throws SQLException {
        synchronized (getSynchronizationObject()) {
            if (this.coordinator != null) {
                SQLExceptionChainBuilder<SQLException> chain = new SQLExceptionChainBuilder<SQLException>();
                try {
                    this.coordinator.completeStatements(CompletionReason.COMMIT);
                } catch (SQLException ex) {
                    chain.append(ex);
                } finally {
                    try {
                        this.coordinator.internalCommit();
                    } catch (SQLException ex) {
                        chain.append(ex);
                    }
                }

                if (chain.hasException()) {
                    throw chain.getException();
                }

                coordinator.setStatements(this.coordinator.getStatements());
            }

            this.coordinator = coordinator;
        }
    }

    abstract static class AbstractTransactionCoordinator implements FBObjectListener.StatementListener,
            FBObjectListener.BlobListener {
        protected final FirebirdLocalTransaction localTransaction;
        protected final AbstractConnection connection;

        protected final Collection<AbstractStatement> statements = new HashSet<AbstractStatement>();

        private AbstractTransactionCoordinator(AbstractConnection connection, FirebirdLocalTransaction localTransaction) {
            this.localTransaction = localTransaction;
            this.connection = connection;
        }

        /**
         * Get the connection which owns this coordinator.
         *
         * @return instance of {@link AbstractConnection}
         */
        public final AbstractConnection getConnection() throws SQLException {
            return connection;
        }

        protected final Collection<AbstractStatement> getStatements() {
            return statements;
        }

        protected final void setStatements(Collection<AbstractStatement> statements) {
            this.statements.addAll(statements);
        }

        protected void completeStatements(CompletionReason reason) throws SQLException {
            SQLExceptionChainBuilder<SQLException> chain = new SQLExceptionChainBuilder<SQLException>();

            // we have to loop through the array, since the 
            // statement.completeStatement() call causes the 
            // ConcurrentModificationException
            AbstractStatement[] statementsToComplete = statements.toArray(new AbstractStatement[statements.size()]);
            for (AbstractStatement statement : statementsToComplete) {
                try {
                    statement.completeStatement(reason);
                } catch (SQLException ex) {
                    chain.append(ex);
                }
            }

            // clear the statements (usually needed only for those that
            // were not removed in the statement.completeStatement() call
            statements.clear();

            if (chain.hasException()) {
                throw chain.getException();
            }
        }

        final void internalCommit() throws SQLException {
            try {
                if (localTransaction != null && localTransaction.inTransaction()) {
                    localTransaction.commit();
                }
            } catch (ResourceException ex) {
                throw new FBSQLException(ex);
            }
        }

        final void internalRollback() throws SQLException {
            try {
                if (localTransaction != null && localTransaction.inTransaction()) {
                    localTransaction.rollback();
                }
            } catch (ResourceException ex) {
                throw new FBSQLException(ex);
            }
        }

        public void ensureTransaction() throws SQLException {
            configureFirebirdAutoCommit();

            try {
                if (!localTransaction.inTransaction()) {
                    localTransaction.begin();
                }
            } catch (ResourceException ex) {
                throw new FBSQLException(ex);
            }
        }

        private void configureFirebirdAutoCommit() throws SQLException {
            if (connection.isUseFirebirdAutoCommit()) {
                TransactionParameterBuffer tpb = connection.getManagedConnection().getTransactionParameters();
                if (connection.getAutoCommit()) {
                    if (!tpb.hasArgument(isc_tpb_autocommit)) {
                        tpb.addArgument(isc_tpb_autocommit);
                    }
                } else {
                    tpb.removeArgument(isc_tpb_autocommit);
                }
            }
        }

        public abstract void commit() throws SQLException;

        public abstract void rollback() throws SQLException;

        abstract void handleConnectionClose() throws SQLException;

        boolean isAutoCommit() throws SQLException {
            return false;
        }
    }

    static class AutoCommitCoordinator extends AbstractTransactionCoordinator {

        public AutoCommitCoordinator(AbstractConnection connection,
                FirebirdLocalTransaction localTransaction) {
            super(connection, localTransaction);
        }

        public void executionStarted(AbstractStatement stmt) throws SQLException {
            List<AbstractStatement> tempList = new ArrayList<AbstractStatement>(statements);
            SQLExceptionChainBuilder<SQLException> chain = new SQLExceptionChainBuilder<SQLException>();

            // complete all open statements for the connection (there should be only one anyway)
            for (Iterator<AbstractStatement> iter = tempList.iterator(); iter.hasNext(); ) {
                AbstractStatement tempStatement = iter.next();

                // enable re-entrancy for the same statement
                if (tempStatement == stmt) {
                    iter.remove();
                    continue;
                }
                // Autocommit, so reason of completion is COMMIT
                try {
                    tempStatement.completeStatement(CompletionReason.COMMIT);
                } catch (SQLException e) {
                    chain.append(e);
                }
            }

            statements.removeAll(tempList);
            if (chain.hasException()) {
                throw chain.getException();
            }

            if (!statements.contains(stmt)) {
                statements.add(stmt);
            }

            ensureTransaction();
        }

        public void statementClosed(AbstractStatement stmt) throws SQLException {
            stmt.completeStatement();
            connection.notifyStatementClosed(stmt);
        }

        public void statementCompleted(AbstractStatement stmt) throws SQLException {
            statementCompleted(stmt, true);
        }

        public void statementCompleted(AbstractStatement stmt, boolean success) throws SQLException {
            statements.remove(stmt);
            try {
                if (!localTransaction.inTransaction()) {
                    return;
                }

                if (success) {
                    localTransaction.commit();
                } else {
                    localTransaction.rollback();
                }
            } catch (ResourceException ex) {
                SQLException sqlException = new FBSQLException(ex);
                try {
                    internalRollback();
                } catch (SQLException ex2) {
                    sqlException.setNextException(ex2);
                }

                throw sqlException;
            }
        }

        public void executionCompleted(FirebirdBlob blob) throws SQLException {

        }

        public void executionStarted(FirebirdBlob blob) throws SQLException {
            ensureTransaction();
        }

        public void commit() throws SQLException {
            throw new FBSQLException("Calling commit() in auto-commit mode is not allowed.");
        }

        public void rollback() throws SQLException {
            throw new FBSQLException("Calling rollback() in auto-commit mode is not allowed.");
        }

        @Override
        void handleConnectionClose() throws SQLException {
            try {
                if (localTransaction.inTransaction()) {
                    try {
                        completeStatements(CompletionReason.COMMIT);
                    } finally {
                        internalCommit();
                    }
                }
            } catch (ResourceException e) {
                throw new FBSQLException(e);
            }
        }

        @Override
        boolean isAutoCommit() {
            return true;
        }
    }

    static class LocalTransactionCoordinator extends AbstractTransactionCoordinator {

        public LocalTransactionCoordinator(AbstractConnection connection, FirebirdLocalTransaction localTransaction) {
            super(connection, localTransaction);
        }

        public void commit() throws SQLException {
            try {
                completeStatements(CompletionReason.COMMIT);
            } finally {
                internalCommit();
            }
        }

        public void rollback() throws SQLException {
            try {
                completeStatements(CompletionReason.ROLLBACK);
            } finally {
                internalRollback();
            }
        }

        @Override
        void handleConnectionClose() throws SQLException {
            try {
                if (localTransaction.inTransaction()) {
                    rollback();
                }
            } catch (ResourceException e) {
                throw new FBSQLException(e);
            }
        }

        public void executionStarted(AbstractStatement stmt) throws SQLException {
            if (!statements.contains(stmt)) {
                statements.add(stmt);
            }
            ensureTransaction();
        }

        public void statementClosed(AbstractStatement stmt) throws SQLException {
            stmt.completeStatement();
            connection.notifyStatementClosed(stmt);
        }

        public void statementCompleted(AbstractStatement stmt) throws SQLException {
            statementCompleted(stmt, true);
        }

        public void statementCompleted(AbstractStatement stmt, boolean success) throws SQLException {
            statements.remove(stmt);
        }

        public void executionCompleted(FirebirdBlob blob) throws SQLException {

        }

        public void executionStarted(FirebirdBlob blob) throws SQLException {
            ensureTransaction();
        }
    }

    static class FirebirdAutoCommitCoordinator extends LocalTransactionCoordinator {

        public FirebirdAutoCommitCoordinator(AbstractConnection connection,
                FirebirdLocalTransaction localTransaction) {
            super(connection, localTransaction);
        }

        @Override
        public void executionStarted(AbstractStatement stmt) throws SQLException {
            List<AbstractStatement> tempList = new ArrayList<AbstractStatement>(statements);
            SQLExceptionChainBuilder<SQLException> chain = new SQLExceptionChainBuilder<SQLException>();

            // complete all open statements for the connection (there should be only one anyway)
            for (Iterator<AbstractStatement> iter = tempList.iterator(); iter.hasNext(); ) {
                AbstractStatement tempStatement = iter.next();

                // enable re-entrancy for the same statement
                if (tempStatement == stmt) {
                    iter.remove();
                    continue;
                }
                // Firebird Autocommit doesn't really commit, but we use CompletionReason.COMMIT to get desired behavior
                // for holdable result sets, and the performance win of not having a server roundtrip to close
                // the cursor. This might lead to a slight cursor 'leak' until statement or connection close
                try {
                    tempStatement.completeStatement(CompletionReason.COMMIT);
                } catch (SQLException e) {
                    chain.append(e);
                }
            }

            statements.removeAll(tempList);
            if (chain.hasException()) {
                throw chain.getException();
            }

            if (!statements.contains(stmt)) {
                statements.add(stmt);
            }

            ensureTransaction();
        }

        @Override
        public void statementCompleted(AbstractStatement stmt, boolean success) throws SQLException {
            statements.remove(stmt);

            if (stmt.getStatementType() == ISCConstants.isc_info_sql_stmt_ddl) {
                try {
                    if (!localTransaction.inTransaction()) {
                        return;
                    }

                    if (success) {
                        localTransaction.commit();
                    } else {
                        localTransaction.rollback();
                    }
                } catch (ResourceException ex) {
                    SQLException sqlException = new FBSQLException(ex);
                    try {
                        internalRollback();
                    } catch (SQLException ex2) {
                        sqlException.setNextException(ex2);
                    }

                    throw sqlException;
                }
            }
        }

        @Override
        public void commit() throws SQLException {
            throw new FBSQLException("Calling commit() in auto-commit mode is not allowed.");
        }

        @Override
        public void rollback() throws SQLException {
            throw new FBSQLException("Calling rollback() in auto-commit mode is not allowed.");
        }

        @Override
        void handleConnectionClose() throws SQLException {
            try {
                if (localTransaction.inTransaction()) {
                    try {
                        completeStatements(CompletionReason.COMMIT);
                    } finally {
                        internalCommit();
                    }
                }
            } catch (ResourceException e) {
                throw new FBSQLException(e);
            }
        }

        @Override
        boolean isAutoCommit() {
            return true;
        }
    }

    static class ManagedTransactionCoordinator extends LocalTransactionCoordinator {

        /**
         * Create instance of this class for the specified connection.
         *
         * @param connection
         *         connection to coordinate.
         */
        public ManagedTransactionCoordinator(AbstractConnection connection) {
            super(connection, null);
        }

        @Override
        public void ensureTransaction() throws SQLException {
            // do nothing, we are in managed environment.
        }

        public void executionStarted(AbstractStatement stmt) throws SQLException {
            // NO TRANSACTION MANAGEMENT HERE - empty method
        }

        public void commit() throws SQLException {
            // do nothing, we are in managed environment.
        }

        public void rollback() throws SQLException {
            // do nothing, we are in managed environment.
        }

        public void executionStarted(FirebirdBlob blob) throws SQLException {
            // empty
        }

        @Override
        void handleConnectionClose() throws SQLException {
            // do nothing, we are in a managed environment
        }
    }

    static class MetaDataTransactionCoordinator extends AbstractTransactionCoordinator {

        private final InternalTransactionCoordinator tc;

        public MetaDataTransactionCoordinator(InternalTransactionCoordinator tc) {
            super(tc.connection, tc.connection.getLocalTransaction());
            this.tc = tc;
        }

        /**
         * Create coordinator for the unspecified connection.
         */
        public MetaDataTransactionCoordinator() {
            super(null, null);
            tc = null;
        }

        @Override
        public void ensureTransaction() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void commit() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void rollback() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        void handleConnectionClose() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void executionStarted(AbstractStatement stmt) throws SQLException {
            if (tc != null) {
                tc.ensureTransaction();
            }
        }

        public void statementClosed(AbstractStatement stmt) throws SQLException {
            if (tc != null) {
                stmt.completeStatement();
                tc.connection.notifyStatementClosed(stmt);
            }
        }

        public void statementCompleted(AbstractStatement stmt) throws SQLException {
            statementCompleted(stmt, true);
        }

        public void statementCompleted(AbstractStatement stmt, boolean success) throws SQLException {
            if (tc != null) {
                tc.statementCompleted(stmt, success);
            }
        }

        public void executionCompleted(FirebirdBlob blob) throws SQLException {

        }

        public void executionStarted(FirebirdBlob blob) throws SQLException {

        }

        boolean isAutoCommit() throws SQLException {
            return tc.getAutoCommit();
        }
    }
}
