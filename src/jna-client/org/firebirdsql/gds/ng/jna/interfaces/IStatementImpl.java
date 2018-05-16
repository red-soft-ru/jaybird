package org.firebirdsql.gds.ng.jna.interfaces;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.JaybirdErrorCodes;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.fields.*;
import org.firebirdsql.gds.ng.jna.FbException;
import org.firebirdsql.gds.ng.jna.JnaDatabase;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface.*;
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

    private IDatabaseImpl database;
    private final FbClientLibrary clientLibrary;
    private IStatement statement;
    private IResultSet cursor;
    private IMessageMetadata inMeta;
    private IMessageMetadata outMeta;

    public IStatementImpl(IDatabaseImpl database) {
        super(database.getSynchronizationObject());
        this.database = database;
        clientLibrary = database.getClientLibrary();
    }

    @Override
    protected void free(int option) throws SQLException {
        synchronized (getSynchronizationObject()) {
            statement.free(database.getStatus());
            // Reset statement information
            reset(option == ISCConstants.DSQL_drop);
        }
    }

    @Override
    protected boolean isValidTransactionClass(Class<? extends FbTransaction> transactionClass) {
        return ITransactionImpl.class.isAssignableFrom(transactionClass);
    }

    @Override
    public FbDatabase getDatabase() {
        return database;
    }

    @Override
    public int getHandle() {
        return -1;
    }

    @Override
    public void prepare(String statementText) throws SQLException {
        try {
            final byte[] statementArray = getDatabase().getEncoding().encodeToCharset(statementText);
            if (statementArray.length > JnaDatabase.MAX_STATEMENT_LENGTH) {
                throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_maxStatementLengthExceeded)
                        .messageParameter(JnaDatabase.MAX_STATEMENT_LENGTH)
                        .messageParameter(statementArray.length)
                        .toFlatSQLException();
            }
            synchronized (getSynchronizationObject()) {
                checkTransactionActive(getTransaction());
                final StatementState currentState = getState();
                if (!isPrepareAllowed(currentState)) {
                    throw new SQLNonTransientException(String.format("Current statement state (%s) does not allow call to prepare", currentState));
                }
                resetAll();

                final IDatabaseImpl db = (IDatabaseImpl)getDatabase();
                if (currentState == StatementState.NEW) {

                    // TODO check for allocating statement
                    switchState(StatementState.ALLOCATED);
                    setType(StatementType.NONE);
                } else {
                    checkStatementValid();
                }

                IStatus status = db.getStatus();
                ITransactionImpl transaction = (ITransactionImpl) getTransaction();
                statement = db.getAttachment().prepare(status, transaction.getTransaction(), statementText.length(), statementText,
                        db.getConnectionDialect(), IStatement.PREPARE_PREFETCH_METADATA);

                outMeta = statement.getOutputMetadata(status);
                inMeta = statement.getInputMetadata(status);

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

                setMetaData(getParameterDescriptor(), parameters);

                IDatabaseImpl db = (IDatabaseImpl)getDatabase();
                IStatus status = db.getStatus();

                final StatementType statementType = getType();
                final boolean hasSingletonResult = hasSingletonResult();
                ITransactionImpl transaction = (ITransactionImpl)getTransaction();

                ByteBuffer inMessage = ByteBuffer.allocate(inMeta.getMessageLength(status) + 1);
                Pointer inPtr = new Memory(inMessage.array().length);
                inPtr.write(0, inMessage.array(), 0, inMessage.array().length);
                Pointer outPtr = null;

                if (hasSingletonResult) {

                    ByteBuffer outMessage = ByteBuffer.allocate(inMeta.getMessageLength(status) + 1);
                    outPtr = new Memory(outMessage.array().length);
                    outPtr.write(0, outMessage.array(), 0, outMessage.array().length);
                    cursor = statement.openCursor(status, transaction.getTransaction(), inMeta, inPtr, outMeta, 0);
                } else {
                    statement.execute(status, transaction.getTransaction(), inMeta, inPtr, outMeta, outPtr);
                }

                if (hasSingletonResult) {
                    /* A type with a singleton result (ie an execute procedure with return fields), doesn't actually
                     * have a result set that will be fetched, instead we have a singleton result if we have fields
                     */
                    statementListenerDispatcher.statementExecuted(this, false, true);
                    queueRowData(toRowValue(getFieldDescriptor(), outMeta, outPtr));
                    setAllRowsFetched(true);
                } else {
                    // A normal execute is never a singleton result (even if it only produces a single result)
                    statementListenerDispatcher.statementExecuted(this, hasFields(), false);
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

    protected void setMetaData(final RowDescriptor rowDescriptor, final RowValue parameters) throws FbException {

        IMaster master = database.getMaster();
        IStatus status = database.getStatus();
        IMetadataBuilder metadataBuilder = master.getMetadataBuilder(status, parameters.getCount());

        for (int idx = 0; idx < parameters.getCount(); idx++) {

            FieldValue value = parameters.getFieldValue(idx);
            byte[] fieldData = value.getFieldData();
            if (fieldData == null) {
                // Note this only works because we mark the type as nullable in allocateXSqlDa

            } else {

                // TODO Throw truncation error if fieldData longer than sqllen?

                final FieldDescriptor fieldDescriptor = rowDescriptor.getFieldDescriptor(idx);
                if (fieldDescriptor.isVarying()) {
                    // Only send the data we need
                    metadataBuilder.setLength(status, idx, Math.min(fieldDescriptor.getLength(), fieldData.length));
                    metadataBuilder.setType(status, idx, ISCConstants.SQL_VARYING);
                } else if (fieldDescriptor.isFbType(ISCConstants.SQL_TEXT)) {
                    // Only send the data we need
                    metadataBuilder.setLength(status, idx, Math.min(fieldDescriptor.getLength(), fieldData.length));
                    metadataBuilder.setType(status, idx, ISCConstants.SQL_TEXT);
                    metadataBuilder.setSubType(status, idx, fieldDescriptor.getSubType());
                }
            }
        }
        inMeta = metadataBuilder.getMetadata(status);

        metadataBuilder.release();
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

                final IDatabaseImpl db = (IDatabaseImpl)getDatabase();
                IStatus status = db.getStatus();

                ByteBuffer message = ByteBuffer.allocate(outMeta.getMessageLength(status) + 1);
                Pointer ptr = new Memory(message.array().length);
//                ptr.write(0, message.array(), 0, message.array().length);
                int fetchStatus = cursor.fetchNext(status, ptr);
                if (fetchStatus == IStatus.RESULT_OK) {
                    queueRowData(toRowValue(getFieldDescriptor(), outMeta, ptr));
                } else if (fetchStatus == IStatus.RESULT_NO_DATA) {
                    setAllRowsFetched(true);
                    // Note: we are not explicitly 'closing' the cursor here
                } else {
                    final String errorMessage = "Unexpected fetch status (expected 0 or 100): " + fetchStatus;
                    log.error(errorMessage);
                    throw new SQLException(errorMessage);
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    protected RowValue toRowValue(RowDescriptor rowDescriptor, IMessageMetadata meta, Pointer ptr) throws FbException {
        final RowValueBuilder row = new RowValueBuilder(rowDescriptor);

        IStatus status = database.getStatus();

        int columns = meta.getCount(status);

        for (int idx = 0; idx < columns; idx++) {

            row.setFieldIndex(idx);
            int nullOffset = meta.getNullOffset(status, idx);

            if (ptr.getShort(nullOffset) == XSQLVAR.SQLIND_NULL) {
                row.set(null);
            } else {
                int bufferLength = meta.getLength(status, idx);
                int offset = meta.getOffset(status, idx);

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
            final IDatabaseImpl db = (IDatabaseImpl)getDatabase();
            IStatus status = db.getStatus();

            synchronized (getSynchronizationObject()) {
                checkStatementValid();
                statement.getInfo(status, requestItems.length, requestItems,
                        bufferLength, responseArr);
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
                final IDatabaseImpl db = (IDatabaseImpl)getDatabase();
                IStatus status = db.getStatus();
                statement.setCursorName(status, cursorName + '\0');
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

    public FbMessageMetadata getInputMetadata() throws FbException {
        return new IMessageMetadataImpl(database, inMeta);
    }
}
