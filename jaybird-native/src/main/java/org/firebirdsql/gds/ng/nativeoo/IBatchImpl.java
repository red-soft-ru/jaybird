package org.firebirdsql.gds.ng.nativeoo;

import com.sun.jna.ptr.LongByReference;
import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.GDSHelper;
import org.firebirdsql.gds.ng.AbstractFbBatch;
import org.firebirdsql.gds.ng.FbBatchCompletionState;
import org.firebirdsql.gds.ng.FbBlob;
import org.firebirdsql.gds.ng.FbDatabase;
import org.firebirdsql.gds.ng.FbMessageMetadata;
import org.firebirdsql.gds.ng.FbStatement;
import org.firebirdsql.gds.ng.FbTransaction;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.fields.RowValue;
import org.firebirdsql.jdbc.FBBlob;
import org.firebirdsql.jdbc.FirebirdBlob;
import org.firebirdsql.jna.fbclient.CloseableMemory;
import org.firebirdsql.jna.fbclient.FbInterface.IAttachment;
import org.firebirdsql.jna.fbclient.FbInterface.IBatch;
import org.firebirdsql.jna.fbclient.FbInterface.IBatchCompletionState;
import org.firebirdsql.jna.fbclient.FbInterface.IStatus;

import java.io.IOException;
import java.sql.SQLException;

import static org.firebirdsql.gds.ISCConstants.SQL_BLOB;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbBatch} for native OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IBatchImpl extends AbstractFbBatch {

    private final IAttachment attachment;
    private final BatchParameterBuffer parameterBuffer;
    private final IStatus status;
    private final String statementText;
    private IMessageMetadataImpl metadata;
    private IBatch batch;
    private IStatementImpl statement;
    private IMessageBuilderImpl messageBuilder;

    public IBatchImpl(FbDatabase database, FbTransaction transaction, String statementText, FbMessageMetadata metadata,
                      BatchParameterBuffer parameters) throws SQLException {
        super(database, parameters);
        this.transaction = transaction;
        this.attachment = getDatabase().getAttachment();
        this.metadata = (IMessageMetadataImpl) metadata;
        this.statementText = statementText;
        this.parameterBuffer = parameters;
        this.status = getDatabase().getStatus();

        init();
    }

    public IBatchImpl(FbDatabase database, FbTransaction transaction, String statementText,
                      BatchParameterBuffer parameters) throws SQLException {
        super(database, parameters);
        this.transaction = transaction;
        this.attachment = getDatabase().getAttachment();
        this.statementText = statementText;
        this.parameterBuffer = parameters;
        this.status = getDatabase().getStatus();
        metadata = null;

        init();
    }

    public IBatchImpl(IBatch batch, IStatementImpl statement, BatchParameterBuffer parameters) throws SQLException {
        super(statement.getDatabase(), parameters);
        this.transaction = statement.getTransaction();
        this.attachment = getDatabase().getAttachment();
        this.parameterBuffer = parameters;
        this.status = getDatabase().getStatus();
        this.batch = batch;
        this.statement = statement;
        this.metadata = (IMessageMetadataImpl) statement.getInputMetadata();
        this.statementText = null;
        this.messageBuilder = new IMessageBuilderImpl(this);
        prepareBatch();
    }

    /**
     * If batch is created from a database,
     * it is necessary to initialize it to obtain metadata.
     *
     * @throws SQLException For errors when initializing batch
     */
    private void init() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            if (metadata == null) {
                if (statement == null) {
                    statement = new IStatementImpl(getDatabase());
                    statement.setTransaction(transaction);
                    statement.prepare(statementText);
                }
                metadata = (IMessageMetadataImpl) statement.getInputMetadata();
            }
            final byte[] statementArray = getDatabase().getEncoding().encodeToCharset(statementText);
            if (parameterBuffer == null) {
                batch = attachment.createBatch(getStatus(), ((ITransactionImpl) transaction).getTransaction(),
                        statementArray.length, statementArray, getDatabase().getDatabaseDialect(),
                        metadata.getMetadata(), 0, null);
            } else {
                batch = attachment.createBatch(getStatus(), ((ITransactionImpl) transaction).getTransaction(),
                        statementArray.length, statementArray, getDatabase().getDatabaseDialect(), metadata.getMetadata(),
                        parameterBuffer.toBytesWithType().length, parameterBuffer.toBytesWithType());
            }
            processStatus();
        }
        this.messageBuilder = new IMessageBuilderImpl(this);
        prepareBatch();
    }

    /**
     * Build batch message from field values.
     *
     * @throws SQLException For errors when adding field values to batch
     */
    @Override
    public void addBatch() throws SQLException {
        RowValue fieldValues = getFieldValues();
        for (int i = 0; i < fieldValues.getCount(); i++) {
            messageBuilder.addData(i, fieldValues.getFieldData(i), getParameterDescriptor(i + 1));
        }
        byte[] data = messageBuilder.getData();
        try (CloseableMemory memory = new CloseableMemory(data.length)) {
            try (LockCloseable ignored = withLock()) {
                memory.write(0, data, 0, data.length);
                batch.add(getStatus(), 1, memory);
                processStatus();
                messageBuilder.clear();
            }
        }
        if (messageBuilder.getBlobStreamData().length != 0) {
            addBlobStream(messageBuilder.getBlobStreamData());
            messageBuilder.clearBlobStream();
        }
    }

    /**
     * Build batch message from field values.
     *
     * @throws SQLException For errors when adding fields values to batch
     */
    @Override
    public void addBatch(RowValue fieldValues) throws SQLException {
        for (int i = 0; i < fieldValues.getCount(); i++) {
            messageBuilder.addData(i, fieldValues.getFieldData(i), getParameterDescriptor(i + 1));
            if (fieldValues.getFieldData(i) != null && getParameterDescriptor(i + 1).isFbType(SQL_BLOB)) {
                long l = getParameterDescriptor(i + 1).getDatatypeCoder().decodeLong(fieldValues.getFieldData(i));
                LongByReference longByReference = new LongByReference(l);
                LongByReference existLong = new LongByReference(l);
                try (LockCloseable ignored = withLock()) {
                    batch.registerBlob(getStatus(), existLong, longByReference);
                    processStatus();
                }
            }
        }
        byte[] data = messageBuilder.getData();
        try (CloseableMemory memory = new CloseableMemory(data.length)) {
            try (LockCloseable ignored = withLock()) {
                memory.write(0, data, 0, data.length);
                batch.add(getStatus(), 1, memory);
                processStatus();
                messageBuilder.clear();
            }
        }
        if (messageBuilder.getBlobStreamData().length != 0) {
            addBlobStream(messageBuilder.getBlobStreamData());
            messageBuilder.clearBlobStream();
        }
    }

    @Override
    public void addBlob(int index, FirebirdBlob blob)
            throws SQLException {
        setBlob(index, blob);
    }

    /*
     *  Before use, сheck the buffer contains BLOB_ID_ENGINE that blob ID will be generated by engine
     */
    @Override
    public FbBlob addBlob(int index, byte[] inBuffer, BlobParameterBuffer buffer) throws SQLException {
        return addBlob(index, inBuffer, 0, buffer);
    }

    @Override
    public FbBlob addBlob(int index, byte[] inBuffer, long blobId, BlobParameterBuffer buffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {

            if (inBuffer != null)
                memory.write(0, inBuffer, 0, inBuffer.length);
            else
                memory.write(0, new byte[] {}, 0, 0);
            LongByReference longByReference = new LongByReference(blobId);

            try (LockCloseable ignored = withLock()) {
                if (buffer == null)
                    batch.addBlob(getStatus(), inBuffer.length, memory, longByReference, 0, null);
                else
                    batch.addBlob(getStatus(), inBuffer.length, memory, longByReference,
                            buffer.toBytesWithType().length, buffer.toBytesWithType());
                processStatus();
            }
            IBlobImpl blob = new IBlobImpl(getDatabase(), (ITransactionImpl) transaction, buffer,
                    longByReference.getValue());
            FBBlob tmpBlob = new FBBlob(new GDSHelper(getDatabase()), blob.getBlobId(), null,
                    FBBlob.createConfig(ISCConstants.BLOB_SUB_TYPE_BINARY, getDatabase().getConnectionProperties(),
                            getDatabase().getDatatypeCoder()));
            setBlob(index, tmpBlob);
            return blob;
        }
    }

    @Override
    public void addSegmentedBlob(int index, BlobParameterBuffer buffer, FirebirdBlob blob)
            throws SQLException, IOException {
        messageBuilder.addBlobHeader(((FBBlob) blob).getBlobId(), buffer);
        addBlob(index, blob);
    }

    @Override
    public void appendBlobData(byte[] inBuffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {
            try (LockCloseable ignored = withLock()) {
                memory.write(0, inBuffer, 0, inBuffer.length);
                batch.appendBlobData(getStatus(), inBuffer.length, memory);
                processStatus();
            }
        }
    }

    @Override
    public void appendBlobData(byte[] data, long blobId) throws IOException {
        messageBuilder.addBlobData(data, blobId);
    }

    @Override
    public void addBlobSegment(byte[] data, boolean lastSegment) throws IOException, SQLException {
        messageBuilder.addBlobSegment(data, lastSegment);
        if (lastSegment) {
            addBlobStream(messageBuilder.getBlobStreamData());
            messageBuilder.clearBlobStream();
        }
    }

    @Override
    public void addBlobStream(byte[] inBuffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {
            try (LockCloseable ignored = withLock()) {
                memory.write(0, inBuffer, 0, inBuffer.length);
                batch.addBlobStream(getStatus(), inBuffer.length, memory);
                processStatus();
            }
        }
    }

    @Override
    public void registerBlob(int index, long existingBlob, FirebirdBlob blob) throws SQLException {
        addBlob(index, blob);
        LongByReference longByReference = new LongByReference(((FBBlob) blob).getBlobId());
        LongByReference existLong = new LongByReference(existingBlob);
        try (LockCloseable ignored = withLock()) {
            batch.registerBlob(getStatus(), existLong, longByReference);
            processStatus();
        }
    }

    @Override
    public FbBatchCompletionState execute() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            IBatchCompletionState execute = batch.execute(getStatus(), ((ITransactionImpl)
                    transaction).getTransaction());
            processStatus();
            return new IBatchCompletionStateImpl(getDatabase(), execute, getDatabase().getStatus());
        }
    }

    @Override
    public void cancel() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            batch.cancel(getStatus());
            processStatus();
        }
    }

    @Override
    public int getBlobAlignment() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            int result = batch.getBlobAlignment(getStatus());
            processStatus();
            return result;
        }
    }

    @Override
    public FbMessageMetadata getMetadata() throws SQLException {
        return metadata;
    }

    @Override
    public void setDefaultBpb(int parLength, byte[] par) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            batch.setDefaultBpb(getStatus(), parLength, par);
            processStatus();
        }
    }

    @Override
    public FbStatement getStatement() throws SQLException {
        return this.statement;
    }

    @Override
    public void release() throws SQLException {
        batch.release();
    }

    private IStatus getStatus() {
        status.init();
        return status;
    }

    @Override
    public IDatabaseImpl getDatabase() {
        return (IDatabaseImpl) super.getDatabase();
    }

    private void processStatus() throws SQLException {
        getDatabase().processStatus(status, null);
    }
}
