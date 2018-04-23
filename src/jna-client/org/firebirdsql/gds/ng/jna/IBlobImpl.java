package org.firebirdsql.gds.ng.jna;

import com.sun.jna.ptr.LongByReference;
import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ng.AbstractFbBlob;
import org.firebirdsql.gds.ng.FbBlob;
import org.firebirdsql.gds.ng.listeners.DatabaseListener;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface.*;

import java.nio.ByteBuffer;
import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IBlobImpl extends AbstractFbBlob implements FbBlob, DatabaseListener {

    private final LongByReference blobId;
    private final boolean outputBlob;
    private ByteBuffer byteBuffer;
    private IBlob blob;

    public IBlobImpl(IDatabaseImpl database, ITransactionImpl transaction, BlobParameterBuffer blobParameterBuffer) {
        this(database, transaction, blobParameterBuffer, NO_BLOB_ID);
    }

    public IBlobImpl(IDatabaseImpl database, ITransactionImpl transaction, BlobParameterBuffer blobParameterBuffer,
                   long blobId) {
        super(database, transaction, blobParameterBuffer);
        this.blobId = new LongByReference(blobId);
        outputBlob = blobId == NO_BLOB_ID;
    }

    @Override
    protected void closeImpl() throws SQLException {

    }

    @Override
    protected void cancelImpl() throws SQLException {

    }

    @Override
    public long getBlobId() {
        return blobId.getValue();
    }

    @Override
    public int getHandle() {
        return 0;
    }

    @Override
    public void open() throws SQLException {

    }

    @Override
    public boolean isOutput() {
        return false;
    }

    @Override
    public byte[] getSegment(int sizeRequested) throws SQLException {
        return new byte[0];
    }

    @Override
    public void putSegment(byte[] segment) throws SQLException {

    }

    @Override
    public void seek(int offset, SeekMode seekMode) throws SQLException {

    }

    @Override
    public byte[] getBlobInfo(byte[] requestItems, int bufferLength) throws SQLException {
        return new byte[0];
    }
}
