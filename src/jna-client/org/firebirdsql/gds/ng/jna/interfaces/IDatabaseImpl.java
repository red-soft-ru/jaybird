package org.firebirdsql.gds.ng.jna.interfaces;

import org.firebirdsql.gds.*;
import org.firebirdsql.gds.impl.DatabaseParameterBufferExtension;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.jna.FbException;
import org.firebirdsql.gds.ng.listeners.TransactionListener;
import org.firebirdsql.jdbc.SQLStateConstants;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.sql.SQLTransientException;

import static org.firebirdsql.gds.ISCConstants.fb_cancel_abort;
import static org.firebirdsql.gds.ng.TransactionHelper.checkTransactionActive;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbDatabase} for native OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IDatabaseImpl extends AbstractFbDatabase<IDatabaseConnectionImpl>
        implements FbAttachment, TransactionListener {

    private static final ParameterConverter<IDatabaseConnectionImpl, ?> PARAMETER_CONVERTER = new IParameterConverterImpl();

    private final FbClientLibrary clientLibrary;
    private final IMaster master;
    private final IProvider provider;
    private final IStatus status;
    private final IUtil util;
    private IAttachment attachment;
    private IEvents events;

    public IDatabaseImpl(IDatabaseConnectionImpl connection) {
        super(connection, connection.createDatatypeCoder());
        clientLibrary = connection.getClientLibrary();
        master = clientLibrary.fb_get_master_interface();
        status = master.getStatus();
        provider = master.getDispatcher();
        util = master.getUtilInterface();
        attachment = null;
    }

    /**
     * @return The client library instance associated with the database.
     */
    protected final FbClientLibrary getClientLibrary() {
        return clientLibrary;
    }

    @Override
    public void attach() throws SQLException {
        try {
            final DatabaseParameterBuffer dpb = ((DatabaseParameterBufferExtension) PARAMETER_CONVERTER
                    .toDatabaseParameterBuffer(connection))
                    .removeExtensionParams();
            attachOrCreate(dpb, false);
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    protected void internalDetach() throws SQLException {
        synchronized (getSynchronizationObject()) {
            try {
                attachment.detach(status);
            } catch (SQLException e) {
                throw e;
            } finally {
                setDetached();
            }
        }
    }

    @Override
    public void createDatabase() throws SQLException {
        try {
            final DatabaseParameterBuffer dpb = ((DatabaseParameterBufferExtension) PARAMETER_CONVERTER
                    .toDatabaseParameterBuffer(connection))
                    .removeExtensionParams();
            attachOrCreate(dpb, true);
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void dropDatabase() throws SQLException {
        try {
            checkConnected();
            synchronized (getSynchronizationObject()) {
                try {
                    attachment.dropDatabase(status);
                } finally {
                    setDetached();
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void cancelOperation(int kind) throws SQLException {
        try {
            checkConnected();
            // No synchronization, otherwise cancel will never work;
            // might conflict with sync policy of JNA (TODO: find out)
            try {
                attachment.cancelOperation(status, kind);
            } finally {
                if (kind == fb_cancel_abort) {
                    attachment.detach(status);
                    setDetached();
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public FbTransaction startTransaction(TransactionParameterBuffer tpb) throws SQLException {
        try {
            checkConnected();
            final byte[] tpbArray = tpb.toBytesWithType();
            synchronized (getSynchronizationObject()) {
                ITransaction transaction = attachment.startTransaction(status, tpbArray.length, tpbArray);

                final ITransactionImpl transactionImpl = new ITransactionImpl(this, transaction,
                        TransactionState.ACTIVE);
                transactionAdded(transactionImpl);
                return transactionImpl;
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public FbTransaction reconnectTransaction(long transactionId) throws SQLException {
        try {
            checkConnected();
            final byte[] transactionIdBuffer = getTransactionIdBuffer(transactionId);

            synchronized (getSynchronizationObject()) {
                ITransaction iTransaction = attachment.reconnectTransaction(status, transactionIdBuffer.length,
                        transactionIdBuffer);

                final ITransactionImpl transaction =
                        new ITransactionImpl(this, iTransaction, TransactionState.PREPARED);
                transactionAdded(transaction);
                return transaction;
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    protected byte[] getTransactionIdBuffer(long transactionId) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        try {
            VaxEncoding.encodeVaxLongWithoutLength(bos, transactionId);
        } catch (IOException e) {
            // ignored: won't happen with a ByteArrayOutputStream
        }
        return bos.toByteArray();
    }

    @Override
    public FbStatement createStatement(FbTransaction transaction) throws SQLException {
        try {
            checkConnected();
            final IStatementImpl statement = new IStatementImpl(this);
            statement.addExceptionListener(exceptionListenerDispatcher);
            statement.setTransaction(transaction);
            return statement;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public FbBlob createBlobForOutput(FbTransaction transaction, BlobParameterBuffer blobParameterBuffer) {
        final IBlobImpl blob = new IBlobImpl(this, (ITransactionImpl) transaction, blobParameterBuffer);
        blob.addExceptionListener(exceptionListenerDispatcher);
        return blob;
    }

    @Override
    public FbBlob createBlobForInput(FbTransaction transaction, BlobParameterBuffer blobParameterBuffer, long blobId) {
        final IBlobImpl blob = new IBlobImpl(this, (ITransactionImpl) transaction, blobParameterBuffer, blobId);
        blob.addExceptionListener(exceptionListenerDispatcher);
        return blob;
    }

    @Override
    public byte[] getDatabaseInfo(byte[] requestItems, int maxBufferLength) throws SQLException {
        try {
            final byte[] responseArray = new byte[maxBufferLength];
            synchronized (getSynchronizationObject()) {
                attachment.getInfo(status, requestItems.length, requestItems, (short) maxBufferLength, responseArray);
            }

            return responseArray;
        } catch (FbException e) {
            exceptionListenerDispatcher.errorOccurred(new SQLException(e));
            throw new SQLException(e);
        }
    }

    @Override
    public void executeImmediate(String statementText, FbTransaction transaction) throws SQLException {
        try {
            if (isAttached()) {
                if (transaction == null) {
                    throw FbExceptionBuilder
                            .forException(JaybirdErrorCodes.jb_executeImmediateRequiresTransactionAttached)
                            .toFlatSQLException();
                } else if (!(transaction instanceof ITransactionImpl)) {
                    throw new SQLNonTransientException(
                            String.format("Invalid transaction handle type: %s, expected: %s",
                                    transaction.getClass(), ITransactionImpl.class),
                            SQLStateConstants.SQL_STATE_GENERAL_ERROR);
                }
                checkTransactionActive(transaction);
            } else if (transaction != null) {
                throw FbExceptionBuilder
                        .forException(JaybirdErrorCodes.jb_executeImmediateRequiresNoTransactionDetached)
                        .toFlatSQLException();
            }

            synchronized (getSynchronizationObject()) {
                attachment = util.executeCreateDatabase(status, statementText.length(),
                        statementText, getConnectionDialect(), new boolean[]{false});
                attachment.execute(status,
                        transaction != null ? ((ITransactionImpl) transaction).getTransaction() :
                                attachment.startTransaction(status, 0, null),
                        statementText.length(),
                        statementText, getConnectionDialect(), null, null,
                        null, null);

                if (!isAttached()) {
                    setAttached();
                    afterAttachActions();
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public int getHandle() {
        return -1;
    }

    protected IEventBlockImpl validateEventHandle(EventHandle eventHandle) throws SQLException {
        if (!(eventHandle instanceof IEventBlockImpl)) {
            // TODO SQLState and/or Firebird specific error
            throw new SQLNonTransientException(String.format("Invalid event handle type: %s, expected: %s",
                    eventHandle.getClass(), IEventBlockImpl.class));
        }
        IEventBlockImpl event = (IEventBlockImpl) eventHandle;
        if (event.getSize() == -1) {
            // TODO SQLState and/or Firebird specific error
            throw new SQLTransientException("Event handle hasn't been initialized");
        }
        return event;
    }

    @Override
    public EventHandle createEventHandle(String eventName, EventHandler eventHandler) throws SQLException {
        final IEventBlockImpl eventHandle = new IEventBlockImpl(eventName, eventHandler, getEncoding());
        synchronized (getSynchronizationObject()) {
            synchronized (eventHandle) {
                IUtil util = master.getUtilInterface();
                IEventBlock eventBlock = util.createEventBlock(status, new String[]{eventName});
                eventHandle.setEventBlock(eventBlock);
            }
        }
        return eventHandle;
    }

    @Override
    public void countEvents(EventHandle eventHandle) throws SQLException {
        try {
            final IEventBlockImpl eventBlock = validateEventHandle(eventHandle);
            int count;
            synchronized (getSynchronizationObject()) {
                synchronized (eventBlock) {
                    count = eventBlock.getEventBlock().getCount();
                }
            }
            eventBlock.setEventCount(count);
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void queueEvent(EventHandle eventHandle) throws SQLException {
        try {
            checkConnected();
            final IEventBlockImpl eventBlock = validateEventHandle(eventHandle);

            synchronized (getSynchronizationObject()) {
                synchronized (eventBlock) {
                    int length = eventBlock.getEventBlock().getLength();
                    byte[] array = eventBlock.getEventBlock().getValues().getByteArray(0, length);
                    events = attachment.queEvents(status, eventBlock.getCallback(),
                            length,
                            array);
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void cancelEvent(EventHandle eventHandle) throws SQLException {
        try {
            checkConnected();
            final IEventBlockImpl eventBlock = validateEventHandle(eventHandle);

            synchronized (getSynchronizationObject()) {
                synchronized (eventBlock) {
                    try {
                        events.cancel(status);
                    } finally {
                        eventBlock.releaseMemory();
                    }
                }
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public FbBatch createBatch(FbTransaction transaction, String statement, FbMessageMetadata metadata, BatchParameterBuffer parameters) throws SQLException {
        return new IBatchImpl(this, transaction, statement, metadata, parameters);
    }

    @Override
    public FbBatch createBatch(FbTransaction transaction, String statement, BatchParameterBuffer parameters) throws SQLException {
        return new IBatchImpl(this, transaction, statement, parameters);
    }

    @Override
    public FbMetadataBuilder getMetadataBuilder(int fieldCount) throws SQLException {
        return new IMetadataBuilderImpl(this, fieldCount);
    }


    @Override
    protected void checkConnected() throws SQLException {
        if (!isAttached()) {
            throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_notAttachedToDatabase)
                    .toFlatSQLException();
        }
    }

    protected void attachOrCreate(final DatabaseParameterBuffer dpb, final boolean create) throws SQLException {
        if (isAttached()) {
            throw new SQLException("Already attached to a database");
        }
        final String dbName = connection.getAttachUrl();
        final byte[] dpbArray = dpb.toBytesWithType();

        synchronized (getSynchronizationObject()) {
            try {
                if (create) {
                    attachment = provider.createDatabase(status, dbName, (short) dpbArray.length, dpbArray);
                } else {
                    attachment = provider.attachDatabase(status, dbName, (short) dpbArray.length, dpbArray);
                }
            } catch (SQLException e) {
                safelyDetach();
                throw e;
            }
            setAttached();
            afterAttachActions();
        }
    }

    protected void afterAttachActions() throws SQLException {
        getDatabaseInfo(getDescribeDatabaseInfoBlock(), 1024, getDatabaseInformationProcessor());
    }

    public IMaster getMaster() {
        return master;
    }

    public IStatus getStatus() {
        return status;
    }

    public IAttachment getAttachment() {
        return attachment;
    }
}
