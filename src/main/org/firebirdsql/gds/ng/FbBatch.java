package org.firebirdsql.gds.ng;


import org.firebirdsql.gds.ng.listeners.ExceptionListenable;
import org.firebirdsql.jna.fbclient.ISC_QUAD;

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
    void addBlob(int length, byte[] inBuffer, ISC_QUAD blobId, int parLength, byte[] par) throws SQLException;

    /**
     * Append blob data.
     */
    void appendBlobData(int length, byte[] inBuffer) throws SQLException;

    /**
     * Add blob stream.
     */
    void addBlobStream(int length, byte[] inBuffer) throws SQLException;

    /**
     * Register existing blob.
     */
    void registerBlob(ISC_QUAD existingBlob, ISC_QUAD blobId) throws SQLException;

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
