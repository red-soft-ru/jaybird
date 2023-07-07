/*
 * Firebird Open Source JDBC Driver
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
package org.firebirdsql.gds.ng.wire.version11;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.JaybirdErrorCodes;
import org.firebirdsql.gds.ng.wire.AsyncFetchStatus;
import org.firebirdsql.gds.ng.CursorFlag;
import org.firebirdsql.gds.ng.DeferredResponse;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.FetchDirection;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.OperationCloseHandle;
import org.firebirdsql.gds.ng.StatementState;
import org.firebirdsql.gds.ng.WarningMessageCallback;
import org.firebirdsql.gds.ng.wire.DeferredAction;
import org.firebirdsql.gds.ng.wire.FbWireDatabase;
import org.firebirdsql.gds.ng.wire.Response;
import org.firebirdsql.gds.ng.wire.version10.V10Statement;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.util.Optional;

import static org.firebirdsql.gds.ng.TransactionHelper.checkTransactionActive;

/**
 * {@link org.firebirdsql.gds.ng.wire.FbWireStatement} implementation for the version 11 wire protocol.
 *
 * @author Mark Rotteveel
 * @since 3.0
 */
public class V11Statement extends V10Statement {

    protected AsyncFetchStatus asyncFetchStatus = AsyncFetchStatus.nonePending();

    /**
     * Creates a new instance of V11Statement for the specified database.
     *
     * @param database
     *         FbWireDatabase implementation
     */
    public V11Statement(FbWireDatabase database) {
        super(database);
    }

    @Override
    public void prepare(final String statementText) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkTransactionActive(getTransaction());
            final StatementState initialState = getState();
            if (!isPrepareAllowed(initialState)) {
                throw new SQLNonTransientException(
                        "Current statement state (%s) does not allow call to prepare".formatted(initialState));
            }
            resetAll();

            int expectedResponseCount = 0;
            try {
                if (initialState == StatementState.NEW) {
                    sendAllocate();
                    // We're assuming allocation is successful, as changing it when processing the response breaks
                    // the state transition in sendPrepare
                    switchState(StatementState.ALLOCATED);
                    expectedResponseCount++;
                } else {
                    checkStatementValid();
                }
                sendPrepare(statementText);
                expectedResponseCount++;

                getXdrOut().flush();
            } catch (IOException e) {
                switchState(StatementState.ERROR);
                throw FbExceptionBuilder.ioWriteError(e);
            }

            try {
                final FbWireDatabase db = getDatabase();
                try {
                    if (initialState == StatementState.NEW) {
                        try {
                            expectedResponseCount--;
                            processAllocateResponse(db.readGenericResponse(getStatementWarningCallback()));
                        } catch (SQLException e) {
                            forceState(StatementState.NEW);
                            throw e;
                        }
                    }
                    try {
                        expectedResponseCount--;
                        processPrepareResponse(db.readGenericResponse(getStatementWarningCallback()));
                    } catch (SQLException e) {
                        switchState(StatementState.ALLOCATED);
                        throw e;
                    }
                } finally {
                    db.consumePackets(expectedResponseCount, getStatementWarningCallback());
                }
            } catch (IOException e) {
                switchState(StatementState.ERROR);
                throw FbExceptionBuilder.ioReadError(e);
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void fetchRows(int fetchSize) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            if (!completeAsyncFetch()) {
                super.fetchRows(fetchSize);
            }
        }
    }

    @Override
    public final void asyncFetchRows(int fetchSize) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            if (fetchSize < 1) {
                throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_invalidFetchSize)
                        .messageParameter(fetchSize)
                        .toSQLException();
            }
            if (!getState().isCursorOpen()) {
                throw FbExceptionBuilder.forException(ISCConstants.isc_cursor_not_open).toSQLException();
            }
            // Conditions for not performing an async fetch
            if (isAfterLast()
                || asyncFetchStatus.isPending()
                || fetchSize == 1
                || getCursorName() != null
                || isCursorFlagSet(CursorFlag.CURSOR_TYPE_SCROLLABLE)) return;

            try (OperationCloseHandle operationCloseHandle = signalAsyncFetchStart()){
                if (operationCloseHandle.isCancelled()) {
                    // operation was synchronously cancelled from an OperationAware implementation
                    throw FbExceptionBuilder.forException(ISCConstants.isc_cancelled).toSQLException();
                }
                sendFetch(fetchSize);
                getXdrOut().flush();
            } catch (IOException e) {
                switchState(StatementState.ERROR);
                throw FbExceptionBuilder.ioWriteError(e);
            }
            asyncFetchStatus = AsyncFetchStatus.pending();
            getDatabase().enqueueDeferredAction(wrapDeferredResponse(
                    new DeferredResponse<>() {
                        @Override
                        public void onResponse(AsyncFetchStatus responseStatus) {
                            asyncFetchStatus = responseStatus;
                        }

                        @Override
                        public void onException(Exception exception) {
                            SQLException sqlException;
                            if (exception instanceof SQLException sqle) {
                                sqlException = sqle;
                            } else if (exception instanceof IOException ioe) {
                                sqlException = FbExceptionBuilder.ioReadError(ioe);
                            } else {
                                sqlException = new SQLException("Unexpected exception occurred during async fetch",
                                        exception);
                            }
                            asyncFetchStatus = AsyncFetchStatus.completedWithException(sqlException);
                        }
                    },
                    this::processAsyncFetchResponse, false));
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private AsyncFetchStatus processAsyncFetchResponse(Response initialResponse) {
        try (OperationCloseHandle ignored = signalAsyncFetchComplete()){
            processFetchResponse(FetchDirection.FORWARD, initialResponse);
            return AsyncFetchStatus.completed();
        } catch (IOException e) {
            try {
                switchState(StatementState.ERROR);
            } catch (SQLException ignored) {
                // ERROR is always a valid transition, so no exception should get thrown
            }
            return AsyncFetchStatus.completedWithException(FbExceptionBuilder.ioReadError(e));
        } catch (SQLException e) {
            return AsyncFetchStatus.completedWithException(e);
        }
    }

    /**
     * Completes a pending async fetch.
     *
     * @return {@code true} if a pending async fetch was processed, {@code false} if there was no pending async fetch.
     * @throws SQLException
     *         from unsuccessfully completed async fetch, or for errors try to complete deferred actions
     * @since 6
     */
    protected boolean completeAsyncFetch() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            if (!asyncFetchStatus.isPending()) return false;
            try {
                // Throw exception from previously unsuccessfully completed async fetch
                Optional<SQLException> fetchException = asyncFetchStatus.exception();
                if (fetchException.isPresent()) {
                    throw fetchException.get();
                }
                getDatabase().completeDeferredActions();
                // Throw exception from unsuccessfully completed async fetch
                fetchException = asyncFetchStatus.exception();
                if (fetchException.isPresent()) {
                    throw fetchException.get();
                }
                return true;
            } finally {
                asyncFetchStatus = AsyncFetchStatus.completed();
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    protected void free(final int option) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            try {
                doFreePacket(option);
                // Don't flush close of cursor, only flush drop or unprepare of statement. This balances network
                // efficiencies with preventing statements retaining locks on metadata objects too long
                if (option != ISCConstants.DSQL_close) {
                    getXdrOut().flush();
                }
                // process response later
                getDatabase().enqueueDeferredAction(new DeferredAction() {
                    @Override
                    public void processResponse(Response response) {
                        processFreeResponse(response);
                    }

                    @Override
                    public WarningMessageCallback getWarningMessageCallback() {
                        return getStatementWarningCallback();
                    }

                    @Override
                    public boolean requiresSync() {
                        return option == ISCConstants.DSQL_close;
                    }
                });
            } catch (IOException e) {
                switchState(StatementState.ERROR);
                throw FbExceptionBuilder.ioWriteError(e);
            }
        }
    }

    @Override
    protected void reset(boolean resetAll) {
        try (LockCloseable ignored = withLock()) {
            try {
                super.reset(resetAll);
            } finally {
                asyncFetchStatus.exception().ifPresent(e -> System.getLogger(getClass().getName())
                        .log(System.Logger.Level.TRACE, "Ignored pending async fetch exception during reset", e));
                asyncFetchStatus = AsyncFetchStatus.nonePending();
            }
        }
    }

}
