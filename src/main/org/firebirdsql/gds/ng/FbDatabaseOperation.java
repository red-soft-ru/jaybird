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
package org.firebirdsql.gds.ng;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.JaybirdErrorCodes;
import org.firebirdsql.gds.ng.monitor.Operation;

import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * {@link Operation} implementation wrapping {@link FbDatabase}.
 * <p>
 * This implementation will automatically signal completion on {@link #close()}.
 * </p>
 *
 * @author Vasiliy Yashkov
 * @author Mark Rotteveel
 * @since 4.0
 */
final class FbDatabaseOperation implements Operation, OperationCloseHandle {

    private static final Runnable NO_OP = () -> {};

    private final Type type;
    @SuppressWarnings("java:S3077")
    private volatile FbDatabase fbDatabase;
    private volatile FbTransaction fbTransaction;
    private volatile boolean cancelled;
    private Runnable onCompletion;

    private FbDatabaseOperation(Operation.Type type, FbDatabase fbDatabase, FbTransaction fbTransaction,
                                Runnable onCompletion) {
        this.type = requireNonNull(type, "type");
        this.onCompletion = onCompletion != null ? onCompletion : NO_OP;
        this.fbDatabase = requireNonNull(fbDatabase, "fbDatabase");
        this.fbTransaction = fbTransaction;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void cancel() throws SQLException {
        final FbDatabase current = this.fbDatabase;
        if (current == null) {
            throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_operationClosed)
                    .messageParameter("cancel")
                    .toSQLException();
        }
        if (type.isCancellable()) {
            cancelled = true;
            current.cancelOperation(ISCConstants.fb_cancel_raise);
        } else {
            throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_operationNotCancellable)
                    .messageParameter(type)
                    .toSQLException();
        }
    }

    @Override
    public FbAttachment getAttachment() {
        return fbDatabase;
    }

    @Override
    public FbTransaction getTransaction() {
        return fbTransaction;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Close this operation and signal 'end-of-operation'.
     * <p>
     * Closing will prevent further actions on this operation like cancellation.
     * </p>
     */
    @Override
    public void close() {
        final FbDatabase previous = fbDatabase;
        final Runnable onCompletion = this.onCompletion;
        this.onCompletion = null;
        fbDatabase = null;
        if (previous != null) {
            OperationMonitor.endOperation(this);
            onCompletion.run();
        }
    }

    static OperationCloseHandle signalExecute(FbDatabase fbDatabase, FbTransaction fbTransaction) {
        return signalOperation(fbDatabase, fbTransaction, Type.STATEMENT_EXECUTE);
    }

    static OperationCloseHandle signalFetch(FbDatabase fbDatabase, FbTransaction fbTransaction, Runnable onCompletion) {
        return signalOperation(fbDatabase, fbTransaction, Type.STATEMENT_FETCH, onCompletion);
    }

    static OperationCloseHandle signalAsyncFetchStart(FbDatabase fbDatabase, FbTransaction fbTransaction,
                                                      Runnable onCompletion) {
        return signalOperation(fbDatabase, fbTransaction, Type.STATEMENT_ASYNC_FETCH_START, onCompletion);
    }

    static OperationCloseHandle signalAsyncFetchComplete(FbDatabase fbDatabase, FbTransaction fbTransaction) {
        return signalOperation(fbDatabase, fbTransaction, Type.STATEMENT_ASYNC_FETCH_COMPLETE);
    }

    private static OperationCloseHandle signalOperation(FbDatabase fbDatabase, FbTransaction fbTransaction, Type type) {
        return signalOperation(fbDatabase, fbTransaction, type, NO_OP);
    }

    private static OperationCloseHandle signalOperation(FbDatabase fbDatabase, FbTransaction fbTransaction,
                                                        Type type, Runnable onCompletion) {
        FbDatabaseOperation fbDatabaseOperation = new FbDatabaseOperation(type, fbDatabase, fbTransaction, onCompletion);
        OperationMonitor.startOperation(fbDatabaseOperation);
        return fbDatabaseOperation;
    }

}
