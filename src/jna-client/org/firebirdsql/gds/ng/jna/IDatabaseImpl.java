package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.*;
import org.firebirdsql.gds.impl.DatabaseParameterBufferExtension;
import org.firebirdsql.gds.impl.GDSServerVersion;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.listeners.TransactionListener;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import static org.firebirdsql.gds.ISCConstants.fb_cancel_abort;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IDatabaseImpl extends AbstractFbDatabase<IDatabaseConnectionImpl>
        implements JnaAttachment, TransactionListener {

    private static final ParameterConverter<IDatabaseConnectionImpl, ?> PARAMETER_CONVERTER = new IParameterConverterImpl();

    private final FbClientLibrary clientLibrary;
    private final IMaster master;
    private final IProvider provider;
    private final IStatus status;
    private IAttachment attachment;

    public IDatabaseImpl(IDatabaseConnectionImpl connection) {
        super(connection, connection.createDatatypeCoder());
        clientLibrary = connection.getClientLibrary();
        master = clientLibrary.fb_get_master_interface();
        status = master.getStatus();
        provider = master.getDispatcher();
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
            } catch (SQLException ex) {
                throw ex;
            } catch (Exception ex) {
                // TODO Replace with specific error (eg native client error)
                throw new FbExceptionBuilder()
                        .exception(ISCConstants.isc_network_error)
                        .messageParameter(connection.getServerName())
                        .cause(ex)
                        .toSQLException();
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
            // TODO Test what happens with 2.1 and earlier client library
            // No synchronization, otherwise cancel will never work; might conflict with sync policy of JNA (TODO: find out)
            try {
                attachment.cancelOperation(status, kind);
            } finally {
                if (kind == fb_cancel_abort) {
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
                ITransaction transaction = attachment.startTransaction(status, (short) tpbArray.length, tpbArray);

                final ITransactionImpl transactionImpl = new ITransactionImpl(this, transaction, TransactionState.ACTIVE);
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
                ITransaction iTransaction = attachment.reconnectTransaction(status, (short) transactionIdBuffer.length, transactionIdBuffer);

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
        // Note: This uses an atypical encoding (as this is actually a TPB without a type)
        if (transactionId <= 0xffffffffL) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
            try {
                VaxEncoding.encodeVaxIntegerWithoutLength(bos, (int) transactionId);
            } catch (IOException e) {
                // ignored: won't happen with a ByteArrayOutputStream
            }
            return bos.toByteArray();
        } else {
            // assuming this is FB 3, because FB 2.5 and lower only have 31 bits tx ids; might fail if this path is triggered on FB 2.5 and lower
            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            try {
                VaxEncoding.encodeVaxLongWithoutLength(bos, transactionId);
            } catch (IOException e) {
                // ignored: won't happen with a ByteArrayOutputStream
            }
            return bos.toByteArray();
        }
    }

    @Override
    public FbStatement createStatement(FbTransaction transaction) throws SQLException {
        try {
            checkConnected();
            final IStatementImpl stmt = new IStatementImpl(this);
            stmt.addExceptionListener(exceptionListenerDispatcher);
            stmt.setTransaction(transaction);
            return stmt;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public FbBlob createBlobForOutput(FbTransaction transaction, BlobParameterBuffer blobParameterBuffer) {
        return null;
    }

    @Override
    public FbBlob createBlobForInput(FbTransaction transaction, BlobParameterBuffer blobParameterBuffer, long blobId) {
        return null;
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

    }

    @Override
    public int getHandle() {
        return 0;
    }

    @Override
    public EventHandle createEventHandle(String eventName, EventHandler eventHandler) throws SQLException {
        return null;
    }

    @Override
    public void countEvents(EventHandle eventHandle) throws SQLException {

    }

    @Override
    public void queueEvent(EventHandle eventHandle) throws SQLException {

    }

    @Override
    public void cancelEvent(EventHandle eventHandle) throws SQLException {

    }

    @Override
    public FbBatch createBatch(FbTransaction transaction, String statement, FbMessageMetadata metadata, BatchParameterBuffer parameters) throws SQLException {
        return new IBatchImpl(this, transaction, statement, metadata, parameters);
    }

    @Override
    public FbMetadataBuilder getMetadataBuilder(int fieldCount) throws SQLException  {
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
            } catch (SQLException ex) {
                safelyDetach();
                throw ex;
            } catch (Exception ex) {
                safelyDetach();
                // TODO Replace with specific error (eg native client error)
                throw new FbExceptionBuilder()
                        .exception(ISCConstants.isc_network_error)
                        .messageParameter(connection.getServerName())
                        .cause(ex)
                        .toSQLException();
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
