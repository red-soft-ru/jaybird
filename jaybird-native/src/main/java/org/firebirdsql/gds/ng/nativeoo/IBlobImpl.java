package org.firebirdsql.gds.ng.nativeoo;

import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;
import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.ng.AbstractFbBlob;
import org.firebirdsql.gds.ng.FbBlob;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.listeners.DatabaseListener;
import org.firebirdsql.jdbc.SQLStateConstants;
import org.firebirdsql.jna.fbclient.CloseableMemory;
import org.firebirdsql.jna.fbclient.FbInterface.IAttachment;
import org.firebirdsql.jna.fbclient.FbInterface.IBlob;
import org.firebirdsql.jna.fbclient.FbInterface.IStatus;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;

import static org.firebirdsql.gds.JaybirdErrorCodes.jb_blobGetSegmentNegative;
import static org.firebirdsql.gds.JaybirdErrorCodes.jb_blobPutSegmentEmpty;
import static org.firebirdsql.gds.JaybirdErrorCodes.jb_blobPutSegmentTooLong;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbBlob} for native OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IBlobImpl extends AbstractFbBlob implements FbBlob, DatabaseListener {

    private final LongByReference blobId;
    private final boolean outputBlob;
    private final IStatus status;
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
        this.status = database.getStatus();
    }

    @Override
    protected void closeImpl() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            blob.close(getStatus());
            processStatus();
            blob = null;
        } finally {
            byteBuffer = null;
        }
    }

    @Override
    protected void cancelImpl() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            blob.cancel(getStatus());
            processStatus();
        } finally {
            byteBuffer = null;
        }
    }

    @Override
    protected void releaseResources() {
        byteBuffer = null;
    }

    @Override
    public long getBlobId() {
        return blobId.getValue();
    }

    @Override
    public int getHandle() {
        throw new UnsupportedOperationException( "Native OO API not support blob handle" );
    }

    @Override
    public void open() throws SQLException {
        try {
            if (isOutput() && getBlobId() != NO_BLOB_ID) {
                throw FbExceptionBuilder.forNonTransientException(ISCConstants.isc_segstr_no_op).toSQLException();
            }

            final BlobParameterBuffer blobParameterBuffer = getBlobParameterBuffer();
            final byte[] bpb;
            if (blobParameterBuffer != null) {
                bpb = blobParameterBuffer.toBytesWithType();
            } else {
                bpb = new byte[0];
            }
            try (LockCloseable ignored = withLock()) {
                checkDatabaseAttached();
                checkTransactionActive();
                checkBlobClosed();

                IAttachment attachment = getDatabase().getAttachment();
                if (isOutput()) {
                    blob = attachment.createBlob(getStatus(), ((ITransactionImpl)getTransaction()).getTransaction(),
                            blobId, bpb.length, bpb);
                } else {
                    blob = attachment.openBlob(getStatus(), ((ITransactionImpl)getTransaction()).getTransaction(),
                            blobId, bpb.length, bpb);
                }
                processStatus();
                setOpen(true);
                resetEof();
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public boolean isOutput() {
        return outputBlob;
    }

    @Override
    public byte[] getSegment(int sizeRequested) throws SQLException {
        try {
            if (sizeRequested <= 0) {
                throw FbExceptionBuilder.forException(jb_blobGetSegmentNegative)
                        .messageParameter(sizeRequested)
                        .toSQLException();
            }
            sizeRequested = Math.min(sizeRequested, getMaximumSegmentSize());
            final ByteBuffer responseBuffer;
            final CloseableMemory actualLength = new CloseableMemory(1024);
            try (LockCloseable ignored = withLock()) {
                checkDatabaseAttached();
                checkTransactionActive();
                checkBlobOpen();
                responseBuffer = getByteBuffer(sizeRequested);
                try (CloseableMemory memory = new CloseableMemory(sizeRequested)) {
                    int result = blob.getSegment(getStatus(), sizeRequested, memory, actualLength);
                    processStatus();
                    // result 0 means: more to come, isc_segment means: buffer was too small,
                    // rest will be returned on next call
                    if (!(IStatus.RESULT_OK == result || result == IStatus.RESULT_SEGMENT)) {
                        if (result == IStatus.RESULT_NO_DATA) {
                            setEof();
                        }
                    }
                    memory.read(0, responseBuffer.array(), 0, sizeRequested);
                }
            }
            final int actualLengthInt = actualLength.getInt(0) & 0xFFFF;
            actualLength.close();
            final byte[] segment = new byte[actualLengthInt];
            responseBuffer.get(segment);
            responseBuffer.clear();
            return segment;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private ByteBuffer getSegment0(int sizeRequested, ShortByReference actualLength) throws SQLException {
        sizeRequested = Math.min(sizeRequested, getMaximumSegmentSize());
        ByteBuffer responseBuffer = getByteBuffer(sizeRequested);
        try (LockCloseable ignored = withLock()) {
            checkDatabaseAttached();
            checkTransactionActive();
            checkBlobOpen();
            try (CloseableMemory memory = new CloseableMemory(sizeRequested)) {
                int result = blob.getSegment(getStatus(), sizeRequested, memory, actualLength.getPointer());
                processStatus();
                // result 0 means: more to come, isc_segment means: buffer was too small,
                // rest will be returned on next call
                if (!(IStatus.RESULT_OK == result || result == IStatus.RESULT_SEGMENT)) {
                    if (result == IStatus.RESULT_NO_DATA) {
                        setEof();
                    }
                }
                memory.read(0, responseBuffer.array(), 0, sizeRequested);
            }
            processStatus();
        }
        return responseBuffer;
    }

    @Override
    protected int get(final byte[] b, final int off, final int len, final int minLen) throws SQLException {
        try (LockCloseable ignored = withLock())  {
            validateBufferLength(b, off, len);
            if (len == 0) return 0;
            if (minLen <= 0 || minLen > len ) {
                throw new SQLNonTransientException(
                        "Value out of range 0 < minLen <= %d, minLen was: %d".formatted(len, minLen),
                        SQLStateConstants.SQL_STATE_INVALID_STRING_LENGTH);
            }
            checkDatabaseAttached();
            checkTransactionActive();
            checkBlobOpen();
            ShortByReference actualLength = new ShortByReference();
            int count = 0;
            while (count < minLen && !isEof()) {
                // We honor the configured buffer size unless we somehow already allocated a bigger buffer earlier
                ByteBuffer segmentBuffer = getSegment0(
                        Math.min(len - count, Math.max(getBlobBufferSize(), currentBufferCapacity())),
                        actualLength);
                int dataLength = actualLength.getValue() & 0xFFFF;
                segmentBuffer.get(b, off + count, dataLength);
                count += dataLength;
            }
            return count;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private int getBlobBufferSize() throws SQLException {
        return getDatabase().getConnectionProperties().getBlobBufferSize();
    }

    @Override
    public void put(byte[] b, int off, int len) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            validateBufferLength(b, off, len);
            if (len == 0) {
                throw FbExceptionBuilder.forException(jb_blobPutSegmentEmpty).toSQLException();
            }
            checkDatabaseAttached();
            checkTransactionActive();
            checkBlobOpen();

            int count = 0;
            if (off == 0) {
                // no additional buffer allocation needed, so we can send with max segment size
                count = Math.min(len, getMaximumSegmentSize());
                try (CloseableMemory memory = new CloseableMemory(count)) {
                    memory.write(0, b, 0, count);
                    blob.putSegment(getStatus(), count, memory);
                    processStatus();
                }
                if (count == len) {
                    // put complete
                    return;
                }
            }

            byte[] segmentBuffer =
                    new byte[Math.min(len - count, Math.min(getBlobBufferSize(), getMaximumSegmentSize()))];
            while (count < len) {
                int segmentLength = Math.min(len - count, segmentBuffer.length);
                System.arraycopy(b, off + count, segmentBuffer, 0, segmentLength);
                try (CloseableMemory memory = new CloseableMemory(segmentLength)) {
                    memory.write(0, segmentBuffer, 0, segmentLength);
                    blob.putSegment(getStatus(), segmentLength, memory);
                    processStatus();
                }
                count += segmentLength;
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void seek(int offset, SeekMode seekMode) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkDatabaseAttached();
            checkTransactionActive();
            // result is the current position in the blob
            // We ignore the result
            blob.seek(getStatus(), seekMode.getSeekModeId(), offset);
            processStatus();
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public byte[] getBlobInfo(byte[] requestItems, int bufferLength) throws SQLException {
        try {
            byte[] responseArr = new byte[bufferLength];
            try (LockCloseable ignored = withLock()) {
                checkDatabaseAttached();
                checkBlobOpen();
                blob.getInfo(getStatus(), requestItems.length, requestItems, bufferLength, responseArr);
                processStatus();
            }
            return responseArr;
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private ByteBuffer getByteBuffer(int requiredSize) {
        if (byteBuffer == null || byteBuffer.capacity() < requiredSize) {
            byteBuffer = ByteBuffer.allocate(requiredSize);
        } else {
            byteBuffer.clear();
        }
        return byteBuffer;
    }

    private int currentBufferCapacity() {
        ByteBuffer byteBuffer = this.byteBuffer;
        return byteBuffer != null ? byteBuffer.capacity() : 0;
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
