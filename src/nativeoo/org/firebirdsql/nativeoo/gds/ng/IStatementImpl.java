package org.firebirdsql.nativeoo.gds.ng;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.JaybirdErrorCodes;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.fields.*;
import org.firebirdsql.gds.ng.jna.JnaDatabase;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.nativeoo.gds.ng.FbInterface.*;
import org.firebirdsql.jna.fbclient.XSQLVAR;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;

import static org.firebirdsql.gds.ng.TransactionHelper.checkTransactionActive;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbStatement} for native client access using OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IStatementImpl extends AbstractFbStatement {

    private static final Logger log = LoggerFactory.getLogger(IStatementImpl.class);

    private final IDatabaseImpl database;
    private final IStatus status;
    private IStatement statement;
    private IResultSet cursor;
    private IMessageMetadata inMetadata;
    private IMessageMetadata outMetadata;

    public IStatementImpl(IDatabaseImpl database) {
        super(database.getSynchronizationObject());
        this.database = database;
        this.status = this.database.getStatus();
    }

    @Override
    protected void free(int option) throws SQLException {
        synchronized (getSynchronizationObject()) {
            if (option == ISCConstants.DSQL_close) {
                cursor.close(getStatus());
                cursor = null;
                processStatus();
            } else if (statement != null) {
                statement.free(getStatus());
                statement = null;
                processStatus();
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
            synchronized (getSynchronizationObject()) {
                checkTransactionActive(getTransaction());
                final StatementState currentState = getState();
                if (!isPrepareAllowed(currentState)) {
                    throw new SQLNonTransientException(String.format("Current statement state (%s) " +
                            "does not allow call to prepare", currentState));
                }
                resetAll();

                if (currentState == StatementState.NEW) {
                    // allocated when prepare call
                    switchState(StatementState.ALLOCATED);
                    setType(StatementType.NONE);
                } else {
                    checkStatementValid();
                }

                ITransactionImpl transaction = (ITransactionImpl) getTransaction();
                statement = getDatabase().getAttachment().prepare(getStatus(), transaction.getTransaction(),
                        statementArray.length, statementArray, getDatabase().getConnectionDialect(),
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
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void execute(RowValue parameters) throws SQLException {
        final StatementState initialState = getState();
        try {
            synchronized (getSynchronizationObject()) {
                checkStatementValid();
                checkTransactionActive(getTransaction());
                validateParameters(parameters);
                reset(false);

                switchState(StatementState.EXECUTING);
                if (this.statement.vTable.version >= IStatementIntf.VERSION)
                    updateStatementTimeout();

                setMetaData(getParameterDescriptor(), parameters);

                ByteBuffer inMessage = ByteBuffer.allocate(inMetadata.getMessageLength(getStatus()));
                processStatus();

                setDataToBuffer(getParameterDescriptor(), parameters, inMessage);

                final StatementType statementType = getType();
                final boolean hasSingletonResult = hasSingletonResult();
                ITransactionImpl transaction = (ITransactionImpl) getTransaction();

                Pointer inPtr = null;
                // Actually the message size may be smaller than previously declared,
                // so we take pointer position as message size
                if (inMessage.position() > 0) {
                    inPtr = new Memory(inMessage.position());
                    inPtr.write(0, inMessage.array(), 0, inMessage.position());
                }
                Pointer outPtr = null;

                try (OperationCloseHandle operationCloseHandle = signalExecute()) {
                    if (operationCloseHandle.isCancelled()) {
                        // operation was synchronously cancelled from an OperationAware implementation
                        throw FbExceptionBuilder.forException(ISCConstants.isc_cancelled).toFlatSQLException();
                    }
                    if ((statement.getFlags(getStatus()) & IStatement.FLAG_HAS_CURSOR) == IStatement.FLAG_HAS_CURSOR) {
                        cursor = statement.openCursor(getStatus(), transaction.getTransaction(), inMetadata, inPtr,
                                outMetadata, 0);
                    } else {
                        ByteBuffer outMessage = ByteBuffer.allocate(getMaxSqlInfoSize());
                        outPtr = new Memory(outMessage.array().length);
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
                        queueRowData(toRowValue(getFieldDescriptor(), outMetadata, outPtr));
                        setAllRowsFetched(true);
                    } else {
                        // A normal execute is never a singleton result (even if it only produces a single result)
                        statementListenerDispatcher.statementExecuted(this, hasFields(), false);
                        processStatus();                    }
                }

                if (getState() != StatementState.ERROR) {
                    switchState(statementType.isTypeWithCursor() ? StatementState.CURSOR_OPEN : StatementState.PREPARED);
                }
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

        IMaster master = database.getMaster();
        IMetadataBuilder metadataBuilder = master.getMetadataBuilder(getStatus(), parameters.getCount());
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
        try {
            synchronized (getSynchronizationObject()) {
                checkStatementValid();
                if (!getState().isCursorOpen()) {
                    throw new FbExceptionBuilder().exception(ISCConstants.isc_cursor_not_open).toSQLException();
                }
                if (isAllRowsFetched()) return;

                try (OperationCloseHandle operationCloseHandle = signalFetch()) {
                    if (operationCloseHandle.isCancelled()) {
                        // operation was synchronously cancelled from an OperationAware implementation
                        throw FbExceptionBuilder.forException(ISCConstants.isc_cancelled).toFlatSQLException();
                    }

                    ByteBuffer message = ByteBuffer.allocate(outMetadata.getMessageLength(getStatus()) + 1);
                    processStatus();
                    Pointer ptr = new Memory(message.array().length);
                    int fetchStatus = cursor.fetchNext(getStatus(), ptr);
                    processStatus();
                    if (fetchStatus == IStatus.RESULT_OK) {
                        queueRowData(toRowValue(getFieldDescriptor(), outMetadata, ptr));
                    } else if (fetchStatus == IStatus.RESULT_NO_DATA) {
                        setAllRowsFetched(true);
                        // Note: we are not explicitly 'closing' the cursor here
                    } else {
                        final String errorMessage = "Unexpected fetch status (expected 0 or 100): " + fetchStatus;
                        log.error(errorMessage);
                        throw new SQLException(errorMessage);
                    }
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    protected RowValue toRowValue(RowDescriptor rowDescriptor, IMessageMetadata meta, Pointer ptr) throws SQLException {
        final RowValueBuilder row = new RowValueBuilder(rowDescriptor);
        int columns = meta.getCount(getStatus());
        processStatus();
        for (int idx = 0; idx < columns; idx++) {
            row.setFieldIndex(idx);
            int nullOffset = meta.getNullOffset(getStatus(), idx);
            processStatus();
            if (ptr.getShort(nullOffset) == XSQLVAR.SQLIND_NULL) {
                row.set(null);
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
                row.set(data);
            }
        }
        return row.toRowValue(false);
    }

    @Override
    public byte[] getSqlInfo(byte[] requestItems, int bufferLength) throws SQLException {
        try {
            final byte[] responseArr = new byte[bufferLength];
            synchronized (getSynchronizationObject()) {
                checkStatementValid();
                statement.getInfo(getStatus(), requestItems.length, requestItems,
                        bufferLength, responseArr);
                processStatus();
            }
            return responseArr;
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
    public void setCursorName(String cursorName) throws SQLException {
        try {
            synchronized (getSynchronizationObject()) {
                checkStatementValid();
                statement.setCursorName(getStatus(), cursorName + '\0');
                processStatus();
            }
        } catch (SQLException e) {
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
        IBatch batch = statement.createBatch(getStatus(),
                inMetadata, parameters.toBytesWithType().length, parameters.toBytesWithType());
        return new IBatchImpl(batch, this, parameters);
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
