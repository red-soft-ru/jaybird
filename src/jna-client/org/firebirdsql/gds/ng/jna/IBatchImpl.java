package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.BatchParameterBuffer;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.jna.fbclient.FbInterface.*;
import org.firebirdsql.jna.fbclient.ISC_QUAD;

import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IBatchImpl extends AbstractFbBatch {

    private IDatabaseImpl database = null;
    private IAttachment attachment = null;
    private ITransaction transaction = null;
    private String statement = null;
    private IMessageMetadataImpl metadata = null;
    private BatchParameterBuffer parameterBuffer;
    private IBatch batch = null;

    public IBatchImpl(FbDatabase database, FbTransaction transaction, String statement, FbMessageMetadata metadata, BatchParameterBuffer parameters) throws FbException {
        super(database, transaction, statement, metadata, parameters);

        this.transaction = ((ITransactionImpl)transaction).getTransaction();
        this.database = (IDatabaseImpl)database;
        this.attachment = this.database.getAttachment();
        this.statement = statement;
        this.metadata = (IMessageMetadataImpl) metadata;
        this.parameterBuffer = parameters;

        init();
    }

    private void init() throws FbException {
        IMaster master = database.getMaster();
        IStatus status = master.getStatus();

        batch = attachment.createBatch(status, transaction, statement.length(), statement, database.getDatabaseDialect(),
                metadata.getMetadata(), parameterBuffer.size(), parameterBuffer.toBytesWithType());
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
        CloseableMemory memory = new CloseableMemory(inBuffer.length);
        memory.write(0, inBuffer, 0, inBuffer.length);
        batch.add(database.getStatus(), count, memory);
    }

    @Override
    public void addBlob(int length, byte[] inBuffer, ISC_QUAD blobId, int parLength, byte[] par) throws SQLException {

    }

    @Override
    public void appendBlobData(int length, byte[] inBuffer) throws SQLException {

    }

    @Override
    public void addBlobStream(int length, byte[] inBuffer) throws SQLException {

    }

    @Override
    public void registerBlob(ISC_QUAD existingBlob, ISC_QUAD blobId) throws SQLException {

    }

    @Override
    public FbBatchCompletionState execute() throws SQLException {
        IStatus status = database.getStatus();

        IBatchCompletionState execute = batch.execute(status, transaction);

        return new IBatchCompletionStateImpl(database, execute);
    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public int getBlobAlignment() throws SQLException {
        return 0;
    }

    @Override
    public FbMessageMetadata getMetadata() throws SQLException {
        return null;
    }

    @Override
    public void setDefaultBpb(int parLength, byte[] par) throws SQLException {

    }
}
