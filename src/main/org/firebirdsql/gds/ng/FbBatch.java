package org.firebirdsql.gds.ng;


import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ng.listeners.ExceptionListenable;

import java.sql.SQLException;

/**
 * Interface for batch operations.
 * <p>
 * All methods defined in this interface are required to notify all {@code SQLException} thrown from the methods
 * defined in this interface.
 * </p>
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public interface FbBatch extends ExceptionListenable {

    int VERSION1 = 1;
    int TAG_MULTIERROR = 1;
    int TAG_RECORD_COUNTS = 2;
    int TAG_BUFFER_BYTES_SIZE = 3;
    int TAG_BLOB_POLICY = 4;
    int TAG_DETAILED_ERRORS = 5;
    int BLOB_NONE = 0;
    int BLOB_ID_ENGINE = 1;
    int BLOB_ID_USER = 2;
    int BLOB_STREAM = 3;
    int BLOB_SEGHDR_ALIGN = 2;

    /**
     * @return Transaction currently associated with this batch.
     */
    FbTransaction getTransaction();

    /**
     * @return The database connection that created this batch.
     */
    FbDatabase getDatabase();

    /**
     * Add data in this batch for record number.
     */
    void add(int count, byte[] inBuffer) throws SQLException;

    /**
     * Add blob in this batch.
     */
    FbBlob addBlob(byte[] inBuffer, long blobId, BlobParameterBuffer buffer) throws SQLException;

    /**
     * Append blob data.
     */
    void appendBlobData(byte[] inBuffer) throws SQLException;

    /**
     * Add blob stream.
     */
    void addBlobStream(byte[] inBuffer) throws SQLException;

    /**
     * Register existing blob.
     */
    void registerBlob(long existingBlob, long blobId) throws SQLException;

    /**
     * Execute tis batch and
     * @return completion state.
     */
    FbBatchCompletionState execute() throws SQLException;

    /**
     * Cancel the batch execution.
     */
    void cancel() throws SQLException;

    /**
     * @return Blob alignment.
     */
    int getBlobAlignment() throws SQLException;

    /**
     * @return The metadata that contained in this batch.
     */
    FbMessageMetadata getMetadata() throws SQLException;

    /**
     * Set default batch parameters buffer.
     */
    void setDefaultBpb(int parLength, byte[] par) throws SQLException;
}
