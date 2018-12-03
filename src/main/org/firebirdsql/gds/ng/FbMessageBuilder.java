package org.firebirdsql.gds.ng;

import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.ng.fields.FieldDescriptor;

import java.io.IOException;
import java.sql.SQLException;

public interface FbMessageBuilder {

    void addData(int index, byte[] data, FieldDescriptor parameterDescriptor) throws SQLException;

    byte[] getData();

    void clear();

    void addStreamData(byte[] data) throws IOException;

    byte[] getStreamData();

    void clearStream();

    void addBlobData(byte[] data, long blobId) throws IOException;

    long addBlobHeader(long blobId, BlobParameterBuffer buffer) throws IOException;

    void addBlobSegment(byte[] data, boolean lastSegment) throws IOException;

    byte[] getBlobStreamData();

    void clearBlobStream();
}
