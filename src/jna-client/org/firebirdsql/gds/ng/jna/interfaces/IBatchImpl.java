package org.firebirdsql.gds.ng.jna.interfaces;

import com.sun.jna.ptr.LongByReference;
import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.jna.CloseableMemory;
import org.firebirdsql.jna.fbclient.FbInterface.*;

import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IBatchImpl extends AbstractFbBatch {

    private IDatabaseImpl database = null;
    private IAttachment attachment = null;
    private FbTransaction transaction = null;
    private String statement = null;
    private IMessageMetadataImpl metadata = null;
    private BatchParameterBuffer parameterBuffer;
    private IBatch batch = null;
    private IStatus status = null;

    public IBatchImpl(FbDatabase database, FbTransaction transaction, String statement, FbMessageMetadata metadata, BatchParameterBuffer parameters) throws SQLException {
        super(database, transaction, statement, metadata, parameters);

        this.transaction = transaction;
        this.database = (IDatabaseImpl)database;
        this.attachment = this.database.getAttachment();
        this.statement = statement;
        this.metadata = (IMessageMetadataImpl) metadata;
        this.parameterBuffer = parameters;

        init();
    }

    public IBatchImpl(FbDatabase database, FbTransaction transaction, String statement, BatchParameterBuffer parameters) throws SQLException {
        super(database, transaction, statement, parameters);

        this.transaction = transaction;
        this.database = (IDatabaseImpl)database;
        this.attachment = this.database.getAttachment();
        this.statement = statement;
        this.parameterBuffer = parameters;

        init();
    }

    private void init() throws SQLException {
        IMaster master = database.getMaster();
        status = master.getStatus();

        if (metadata == null) {
            IStatementImpl statementImpl = new IStatementImpl(database);
            statementImpl.setTransaction(transaction);
            statementImpl.prepare(statement);
            metadata = (IMessageMetadataImpl) statementImpl.getInputMetadata();
        }

        batch = attachment.createBatch(status, ((ITransactionImpl)transaction).getTransaction(), statement.length(), statement, database.getDatabaseDialect(),
                metadata.getMetadata(), parameterBuffer.toBytesWithType().length, parameterBuffer.toBytesWithType());
    }

    @Override
    public FbTransaction getTransaction() {
        return super.getTransaction();
    }

    @Override
    public FbDatabase getDatabase() {
        return super.getDatabase();
    }

    @Override
    public void add(int count, byte[] inBuffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {
            memory.write(0, inBuffer, 0, inBuffer.length);
            batch.add(database.getStatus(), count, memory);
        }
    }

    @Override
    public FbBlob addBlob(byte[] inBuffer, long blobId, BlobParameterBuffer buffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {
            memory.write(0, inBuffer, 0, inBuffer.length);
            LongByReference longByReference = new LongByReference(blobId);

            if (buffer == null)
                batch.addBlob(status, inBuffer.length, memory, longByReference, 0, null);
            else
                batch.addBlob(status, inBuffer.length, memory, longByReference, buffer.toBytesWithType().length, buffer.toBytesWithType());

            return new IBlobImpl(database, (ITransactionImpl) transaction, buffer, longByReference.getValue());
        }
    }

    @Override
    public void appendBlobData(byte[] inBuffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {
            memory.write(0, inBuffer, 0, inBuffer.length);
            batch.appendBlobData(status, inBuffer.length, memory);
        }
    }

    @Override
    public void addBlobStream(byte[] inBuffer) throws SQLException {
        try (CloseableMemory memory = new CloseableMemory(inBuffer.length)) {
            memory.write(0, inBuffer, 0, inBuffer.length);
            batch.addBlobStream(status, inBuffer.length, memory);
        }
    }

    @Override
    public void registerBlob(long existingBlob, long blobId) throws SQLException {
        LongByReference longByReference = new LongByReference(blobId);
        LongByReference existLong = new LongByReference(existingBlob);
        batch.registerBlob(status, existLong, longByReference);
    }

    @Override
    public FbBatchCompletionState execute() throws SQLException {
        IBatchCompletionState execute = batch.execute(status, ((ITransactionImpl)transaction).getTransaction());

        return new IBatchCompletionStateImpl(database, execute, status);
    }

    @Override
    public void cancel() throws SQLException {
        batch.cancel(status);
    }

    @Override
    public int getBlobAlignment() throws SQLException {
        return batch.getBlobAlignment(status);
    }

    @Override
    public FbMessageMetadata getMetadata() throws SQLException {
        return metadata;
    }

    @Override
    public void setDefaultBpb(int parLength, byte[] par) throws SQLException {
        batch.setDefaultBpb(status, parLength, par);
    }
}
