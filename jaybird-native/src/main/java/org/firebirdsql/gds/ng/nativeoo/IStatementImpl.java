package org.firebirdsql.gds.ng.nativeoo;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.ng.AbstractFbStatement;
import org.firebirdsql.gds.ng.BatchCompletion;
import org.firebirdsql.gds.ng.DeferredResponse;
import org.firebirdsql.gds.ng.FbBatch;
import org.firebirdsql.gds.ng.FbBatchConfig;
import org.firebirdsql.gds.ng.FbBatchCompletionState;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.FbMessageMetadata;
import org.firebirdsql.gds.ng.FbTransaction;
import org.firebirdsql.gds.ng.FetchDirection;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.OperationCloseHandle;
import org.firebirdsql.gds.ng.StatementState;
import org.firebirdsql.gds.ng.StatementType;
import org.firebirdsql.gds.ng.fields.*;
import org.firebirdsql.gds.impl.BatchParameterBufferImp;
import org.firebirdsql.jna.fbclient.XSQLVAR;
import org.firebirdsql.jna.fbclient.CloseableMemory;
import org.firebirdsql.jna.fbclient.FbInterface.IBatch;
import org.firebirdsql.jna.fbclient.FbInterface.IMaster;
import org.firebirdsql.jna.fbclient.FbInterface.IMessageMetadata;
import org.firebirdsql.jna.fbclient.FbInterface.IMetadataBuilder;
import org.firebirdsql.jna.fbclient.FbInterface.IResultSet;
import org.firebirdsql.jna.fbclient.FbInterface.IStatement;
import org.firebirdsql.jna.fbclient.FbInterface.IStatementIntf;
import org.firebirdsql.jna.fbclient.FbInterface.IStatus;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.util.Collection;

import static java.util.Objects.requireNonNull;
import static org.firebirdsql.gds.ng.TransactionHelper.checkTransactionActive;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbStatement} for native client access using OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IStatementImpl extends AbstractFbStatement {

    private static final System.Logger log = System.getLogger(IStatementImpl.class.getName());

    private final IDatabaseImpl database;
    private final IStatus status;
    private IStatement statement;
    private IResultSet cursor;
    private IMessageMetadata inMetadata;
    private IMessageMetadata outMetadata;
    private FbBatch batch;

    public IStatementImpl(IDatabaseImpl database) {
        this.database = requireNonNull(database, "database");
        this.status = this.database.getStatus();
    }

    @Override
    public final LockCloseable withLock() {
        return database.withLock();
    }

    @Override
    protected void free(int option) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            if (cursor != null && option == ISCConstants.DSQL_close) {
                cursor.close(getStatus());
                cursor = null;
                processStatus();
            } else if (statement != null) {
                statement.free(getStatus());
                statement = null;
                processStatus();
                inMetadata.release();
                outMetadata.release();
            }
            // Reset statement information
            reset(option == ISCConstants.DSQL_drop);
        }
    }

    @Override
    protected boolean isValidTransactionClass(Class<? extends FbTransaction> transactionClass) {
        return ITransactionImpl.class.isAssignableFrom(transactionClass);
    }

    @Override
    public IDatabaseImpl getDatabase() {
        return database;
    }

    @Override
    public int getHandle() {
        throw new UnsupportedOperationException( "Native OO API not support statement handle" );
    }

    @Override
    public void prepare(String statementText) throws SQLException {
        try {
            final byte[] statementArray = getDatabase().getEncoding().encodeToCharset(statementText);
            try (LockCloseable ignored = withLock()) {
                checkTransactionActive(getTransaction());
                final StatementState initialState = getState();
                if (!isPrepareAllowed(initialState)) {
                    throw new SQLNonTransientException(String.format(
                            "Current statement state (%s) does not allow call to prepare", initialState));
                }
                resetAll();

                if (initialState == StatementState.NEW) {
                    try {
                        // allocated when prepare call
                        switchState(StatementState.ALLOCATED);
                        setType(StatementType.NONE);
                    } catch (SQLException e) {
                        forceState(StatementState.NEW);
                        throw e;
                    }
                } else {
                    checkStatementValid();
                    if (statement != null) {
                        statement.free(getStatus());
                        processStatus();
                    }
                }

                switchState(StatementState.PREPARING);
                try (CloseableMemory memStatementArray = new CloseableMemory(statementArray.length)) {
                    memStatementArray.write(0, statementArray, 0, statementArray.length);
                    ITransactionImpl transaction = (ITransactionImpl) getTransaction();
                    statement = getDatabase().getAttachment().prepare(getStatus(), transaction.getTransaction(),
                            statementArray.length, memStatementArray, getDatabase().getConnectionDialect(),
                            IStatement.PREPARE_PREFETCH_METADATA);
                    processStatus();
                    outMetadata = statement.getOutputMetadata(getStatus());
                    processStatus();
                    inMetadata = statement.getInputMetadata(getStatus());
                    processStatus();

                    final byte[] statementInfoRequestItems = getStatementInfoRequestItems();
                    final int responseLength = getDefaultSqlInfoSize();
                    byte[] statementInfo = getSqlInfo(statementInfoRequestItems, responseLength);
                    parseStatementInfo(statementInfo);
                    switchState(StatementState.PREPARED);
                } catch (SQLException e) {
                    switchState(StatementState.ALLOCATED);
                    throw e;
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void execute(RowValue parameters) throws SQLException {
        final StatementState initialState = getState();
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            checkTransactionActive(getTransaction());
            validateParameters(parameters);
            reset(false);

            switchState(StatementState.EXECUTING);
            if (this.statement.getVTable().version >= IStatementIntf.VERSION)
                updateStatementTimeout();

            setMetaData(getParameterDescriptor(), parameters);

            ByteBuffer inMessage = ByteBuffer.allocate(inMetadata.getMessageLength(getStatus()));
            processStatus();

            setDataToBuffer(getParameterDescriptor(), parameters, inMessage);

            final StatementType statementType = getType();
            final boolean hasSingletonResult = hasSingletonResult();
            ITransactionImpl transaction = (ITransactionImpl) getTransaction();

            Memory inPtr = null;
            int inLength = inMessage.position();
            // Actually the message size may be smaller than previously declared,
            // so we take pointer position as message size
            if (inLength > 0) {
                inPtr = new Memory(inLength);
                inPtr.write(0, inMessage.array(), 0, inMessage.position());
            }
            Memory outPtr = null;
            int outLength = 0;

            try (OperationCloseHandle operationCloseHandle = signalExecute()) {
                if (operationCloseHandle.isCancelled()) {
                    // operation was synchronously cancelled from an OperationAware implementation
                    throw FbExceptionBuilder.forException(ISCConstants.isc_cancelled).toFlatSQLException();
                }
                if ((statement.getFlags(getStatus()) & IStatement.FLAG_HAS_CURSOR) == IStatement.FLAG_HAS_CURSOR) {
                    cursor = statement.openCursor(getStatus(), transaction.getTransaction(), inMetadata, inPtr,
                            outMetadata.getPointer(), 0);
                } else {
                    ByteBuffer outMessage = ByteBuffer.allocate(getMaxSqlInfoSize());
                    outLength = outMessage.array().length;
                    outPtr = new Memory(outLength);
                    outPtr.write(0, outMessage.array(), 0, outMessage.array().length);
                    statement.execute(getStatus(), transaction.getTransaction(), inMetadata, inPtr, outMetadata,
                            outPtr);
                }

                if (hasSingletonResult) {
                    /* A type with a singleton result (ie an execute procedure with return fields), doesn't actually
                     * have a result set that will be fetched, instead we have a singleton result if we have fields
                     */
                    statementListenerDispatcher.statementExecuted(this, false, true);
                    processStatus();
                    queueRowData(toRowValue(getRowDescriptor(), outMetadata, outPtr));
                    setAfterLast();
                } else {
                    // A normal execute is never a singleton result (even if it only produces a single result)
                    statementListenerDispatcher.statementExecuted(this, hasFields(), false);
                    processStatus();
                }
            } finally {
                if (inPtr != null) {
                    inPtr.close();
                    inPtr = null;
                }
                if (outPtr != null) {
                    outPtr.close();
                    outPtr = null;
                }
            }

            if (getState() != StatementState.ERROR) {
                switchState(statementType.isTypeWithCursor() ? StatementState.CURSOR_OPEN : StatementState.PREPARED);
            }
        } catch (SQLException e) {
            if (getState() != StatementState.ERROR) {
                switchState(initialState);
            }
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    protected void setMetaData(final RowDescriptor rowDescriptor, final RowValue parameters) throws SQLException {

        IMetadataBuilder metadataBuilder = inMetadata.getBuilder(getStatus());
        processStatus();
        for (int idx = 0; idx < parameters.getCount(); idx++) {
            byte[] fieldData = parameters.getFieldData(idx);
            final FieldDescriptor fieldDescriptor = rowDescriptor.getFieldDescriptor(idx);
            if (fieldData == null) {
                // Although we pass a null value, length and type must still be specified
                metadataBuilder.setType(getStatus(), idx, inMetadata.getType(getStatus(), idx) | 1);
                metadataBuilder.setLength(getStatus(), idx, inMetadata.getLength(getStatus(), idx));
                metadataBuilder.setCharSet(getStatus(), idx, inMetadata.getCharSet(getStatus(), idx));
            } else {
                if (fieldDescriptor.isVarying() || fieldDescriptor.isFbType(ISCConstants.SQL_TEXT)) {
                    metadataBuilder.setType(getStatus(), idx, inMetadata.getType(getStatus(), idx) | 1);
                    metadataBuilder.setLength(getStatus(), idx, Math.min(fieldDescriptor.getLength(), fieldData.length));
                    metadataBuilder.setCharSet(getStatus(), idx, inMetadata.getCharSet(getStatus(), idx));
                } else {
                    metadataBuilder.setType(getStatus(), idx, inMetadata.getType(getStatus(), idx) | 1);
                    metadataBuilder.setLength(getStatus(), idx, inMetadata.getLength(getStatus(), idx));
                    metadataBuilder.setScale(getStatus(), idx, inMetadata.getScale(getStatus(), idx));
                    metadataBuilder.setCharSet(getStatus(), idx, inMetadata.getCharSet(getStatus(), idx));
                }
            }
        }
        inMetadata.release();
        inMetadata = metadataBuilder.getMetadata(getStatus());
        processStatus();
        metadataBuilder.release();
    }

    private void setDataToBuffer(RowDescriptor rowDescriptor, RowValue parameters, ByteBuffer inMessage) {
        final byte[] nulls = new byte[] {0, 0};

        for (int index = 0; index < parameters.getCount(); index++) {
            byte[] data = parameters.getFieldData(index);
            final FieldDescriptor fieldDescriptor = rowDescriptor.getFieldDescriptor(index);
            int nullOffset = inMetadata.getNullOffset(getStatus(), index);
            int offset = inMetadata.getOffset(getStatus(), index);

            if (fieldDescriptor.isVarying()) {
                byte[] dataLen;
                if (data == null)
                    dataLen = fieldDescriptor.getDatatypeCoder().encodeShort(0);
                else
                    dataLen = fieldDescriptor.getDatatypeCoder().encodeShort(data.length);
                inMessage.position(offset);
                inMessage.put(dataLen);
                offset += dataLen.length;
            }

            inMessage.position(offset);
            if (data == null) {
                inMessage.position(nullOffset);
                inMessage.put(fieldDescriptor.getDatatypeCoder().encodeShort(1));
            } else {
                inMessage.put(data);
                inMessage.position(nullOffset);
                inMessage.put(nulls);
            }
        }
    }

    @Override
    public void fetchRows(int fetchSize) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            if (!getState().isCursorOpen()) {
                throw FbExceptionBuilder.forException(ISCConstants.isc_cursor_not_open).toSQLException();
            }
            checkFetchSize(fetchSize);
            if (isAfterLast()) return;

            try (OperationCloseHandle operationCloseHandle = signalFetch()) {
                if (operationCloseHandle.isCancelled()) {
                    // operation was synchronously cancelled from an OperationAware implementation
                    throw FbExceptionBuilder.forException(ISCConstants.isc_cancelled).toFlatSQLException();
                }

                ByteBuffer message = ByteBuffer.allocate(outMetadata.getMessageLength(getStatus()) + 1);
                int messageLength = message.array().length;
                processStatus();

                Memory ptr = new Memory(messageLength);
                try {
                    int fetchStatus = cursor.fetchNext(getStatus(), ptr);
                    processStatus();
                    if (fetchStatus == IStatus.RESULT_OK) {
                        queueRowData(toRowValue(getRowDescriptor(), outMetadata, ptr));
                        statementListenerDispatcher.fetchComplete(this, FetchDirection.FORWARD, 1);
                    } else if (fetchStatus == IStatus.RESULT_NO_DATA) {
                        statementListenerDispatcher.fetchComplete(this, FetchDirection.FORWARD, 0);
                        setAfterLast();
                        // Note: we are not explicitly 'closing' the cursor here
                    } else {
                        final String errorMessage = "Unexpected fetch status (expected 0 or 100): " + fetchStatus;
                        log.log(System.Logger.Level.DEBUG, errorMessage);
                        throw new SQLException(errorMessage);
                    }
                } finally {
                    ptr.close();
                    ptr = null;
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    protected RowValue toRowValue(RowDescriptor rowDescriptor, IMessageMetadata meta, Pointer ptr) throws SQLException {
        final RowValue row = rowDescriptor.createDefaultFieldValues();
        int columns = meta.getCount(getStatus());
        processStatus();
        for (int idx = 0; idx < columns; idx++) {
            int nullOffset = meta.getNullOffset(getStatus(), idx);
            processStatus();
            if (ptr.getShort(nullOffset) == XSQLVAR.SQLIND_NULL) {
                row.setFieldData(idx, null);
            } else {
                int bufferLength = meta.getLength(getStatus(), idx);
                processStatus();
                int offset = meta.getOffset(getStatus(), idx);
                processStatus();
                if (rowDescriptor.getFieldDescriptor(idx).isVarying()) {
                    bufferLength = ptr.getShort(offset) & 0xffff;
                    offset += 2;
                }
                byte[] data = new byte[bufferLength];
                ptr.read(offset, data, 0, bufferLength);
                row.setFieldData(idx, data);
            }
        }
        return row;
    }

    @Override
    public byte[] getSqlInfo(byte[] requestItems, int bufferLength) throws SQLException {
        try {
            try (LockCloseable ignored = withLock();
                 CloseableMemory memRequestItems = new CloseableMemory(requestItems.length);
                 CloseableMemory memResponseArr = new CloseableMemory(bufferLength)) {
                memRequestItems.write(0, requestItems, 0, requestItems.length);
                checkStatementValid();
                statement.getInfo(getStatus(), requestItems.length, memRequestItems,
                        bufferLength, memResponseArr);
                processStatus();
                return memResponseArr.getByteArray(0, bufferLength);
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public int getDefaultSqlInfoSize() {
        // TODO Test for an optimal buffer size
        return getMaxSqlInfoSize();
    }

    @Override
    public int getMaxSqlInfoSize() {
        // TODO check this
        return 65535;
    }

    @Override
    public void setCursorNameImpl(String cursorName) throws SQLException {
        final byte[] cursorNameBytes = getDatabase().getEncoding().encodeToCharset(cursorName + '\0');
        try (LockCloseable ignored = withLock();
             CloseableMemory memCursorNameBytes = new CloseableMemory(cursorNameBytes.length)) {
            memCursorNameBytes.write(0, cursorNameBytes, 0, cursorNameBytes.length);
            checkStatementValid();
            statement.setCursorName(getStatus(), memCursorNameBytes);
            processStatus();
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public boolean supportBatchUpdates() {
        return true;
    }

    @Override
    public BatchParameterBuffer createBatchParameterBuffer() throws SQLException {
        checkStatementValid();
        return new BatchParameterBufferImp();
    }

    @Override
    public void deferredBatchCreate(FbBatchConfig batchConfig, DeferredResponse<Void> onResponse) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            try {
                BatchParameterBuffer batchPb = createBatchParameterBuffer();
                batchConfig.populateBatchParameterBuffer(batchPb);
                batch = createBatch(batchPb);
            } catch (SQLException e) {
                switchState(StatementState.ERROR);
                throw e;
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void deferredBatchSend(Collection<RowValue> rowValues, DeferredResponse<Void> onResponse) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            try {
                for (RowValue rowValue : rowValues) {
                    batch.addBatch(rowValue);
                }
            } catch (SQLException e) {
                switchState(StatementState.ERROR);
                throw e;
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public BatchCompletion batchExecute() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            try {
                FbBatchCompletionState state = batch.execute();
                return state.getBatchCompletion();
            } catch (SQLException e) {
                switchState(StatementState.ERROR);
                throw e;
            }
        }  catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void batchCancel() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            try {
                batch.cancel();
            } catch (SQLException e) {
                switchState(StatementState.ERROR);
                throw FbExceptionBuilder.forException(ISCConstants.isc_net_read_err).cause(e)
                        .toSQLException();
            }
        }  catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void deferredBatchRelease(DeferredResponse<Void> onResponse) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkStatementValid();
            try {
                batch.release();
            } catch (SQLException e) {
                switchState(StatementState.ERROR);
                throw e;
            }
        }  catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public RowDescriptor emptyRowDescriptor() {
        return database.emptyRowDescriptor();
    }

    @Override
    public FbBatch createBatch(BatchParameterBuffer parameters) throws SQLException {
        final byte[] BPBArray = parameters.toBytesWithType();
        try (CloseableMemory memBPBArray = new CloseableMemory(BPBArray.length)) {
            memBPBArray.write(0, BPBArray, 0, BPBArray.length);
            IBatch batch = statement.createBatch(getStatus(),
                    inMetadata, BPBArray.length, memBPBArray);
            return new IBatchImpl(batch, this, parameters);
        }
    }

    public FbMessageMetadata getInputMetadata() throws SQLException {
        return new IMessageMetadataImpl(database, inMetadata);
    }

    private IStatus getStatus() {
        status.init();
        return status;
    }

    private void processStatus() throws SQLException {
        getDatabase().processStatus(status, getStatementWarningCallback());
    }

    private void updateStatementTimeout() throws SQLException {
        int allowedTimeout = (int) getAllowedTimeout();
        statement.setTimeout(getStatus(), allowedTimeout);
        processStatus();
    }
}
