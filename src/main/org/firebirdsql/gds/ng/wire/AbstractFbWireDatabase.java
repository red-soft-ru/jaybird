/*
 * Firebird Open Source JavaEE Connector - JDBC Driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.gds.ng.wire;

import org.firebirdsql.gds.BlobParameterBuffer;
import org.firebirdsql.gds.EventHandle;
import org.firebirdsql.gds.EventHandler;
import org.firebirdsql.gds.impl.wire.XdrInputStream;
import org.firebirdsql.gds.impl.wire.XdrOutputStream;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.jdbc.FBSQLException;

import java.io.IOException;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

/**
 * Abstract class for operations common to all version of the wire protocol implementation.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public abstract class AbstractFbWireDatabase extends AbstractFbDatabase<WireDatabaseConnection>
        implements FbWireDatabase {

    protected final ProtocolDescriptor protocolDescriptor;
    protected final FbWireOperations wireOperations;

    /**
     * Creates an AbstractFbWireDatabase instance.
     *
     * @param connection
     *         A WireConnection with an established connection to the server.
     * @param descriptor
     *         The ProtocolDescriptor that created this connection (this is
     *         used for creating further dependent objects).
     */
    protected AbstractFbWireDatabase(WireDatabaseConnection connection, ProtocolDescriptor descriptor) {
        super(connection, new DefaultDatatypeCoder(connection.getEncodingFactory()));
        protocolDescriptor = requireNonNull(descriptor, "parameter descriptor should be non-null");
        wireOperations = descriptor.createWireOperations(connection, getDatabaseWarningCallback(),
                getSynchronizationObject());
    }

    /**
     * Gets the XdrInputStream.
     *
     * @return Instance of XdrInputStream
     * @throws SQLException
     *         If no connection is opened or when exceptions occur
     *         retrieving the InputStream
     */
    protected final XdrInputStream getXdrIn() throws SQLException {
        return getXdrStreamAccess().getXdrIn();
    }

    /**
     * Gets the XdrOutputStream.
     *
     * @return Instance of XdrOutputStream
     * @throws SQLException
     *         If no connection is opened or when exceptions occur
     *         retrieving the OutputStream
     */
    protected final XdrOutputStream getXdrOut() throws SQLException {
        return getXdrStreamAccess().getXdrOut();
    }

    @Override
    public final XdrStreamAccess getXdrStreamAccess() {
        return connection.getXdrStreamAccess();
    }

    @Override
    public final boolean isAttached() {
        return super.isAttached() && connection.isConnected();
    }

    /**
     * Checks if a physical connection to the server is established.
     *
     * @throws SQLException
     *         If not connected.
     */
    @Override
    protected final void checkConnected() throws SQLException {
        if (!connection.isConnected()) {
            // TODO Update message / externalize
            throw new SQLException("No connection established to the database server", FBSQLException.SQL_STATE_CONNECTION_CLOSED);
        }
    }

    /**
     * Checks if a physical connection to the server is established and if the
     * connection is attached to a database.
     * <p>
     * This method calls {@link #checkConnected()}, so it is not necessary to
     * call both.
     * </p>
     *
     * @throws SQLException
     *         If the database not connected or attached.
     */
    protected final void checkAttached() throws SQLException {
        checkConnected();
        if (!isAttached()) {
            // TODO Update message / externalize + Check if SQL State right
            throw new SQLException("The connection is not attached to a database", FBSQLException.SQL_STATE_CONNECTION_ERROR);
        }
    }

    /**
     * Closes the WireConnection associated with this connection.
     *
     * @throws IOException
     *         For errors closing the connection.
     */
    protected final void closeConnection() throws IOException {
        if (!connection.isConnected()) return;
        synchronized (getSynchronizationObject()) {
            try {
                connection.close();
            } finally {
                setDetached();
            }
        }
    }

    @Override
    public final FbBlob createBlobForOutput(FbTransaction transaction, BlobParameterBuffer blobParameterBuffer)
            throws SQLException {
        return protocolDescriptor.createOutputBlob(this, (FbWireTransaction) transaction, blobParameterBuffer);
    }

    @Override
    public final FbBlob createBlobForInput(FbTransaction transaction, BlobParameterBuffer blobParameterBuffer,
            long blobId) throws SQLException {
        return protocolDescriptor.createInputBlob(this, (FbWireTransaction) transaction, blobParameterBuffer, blobId);
    }

    @Override
    public final void consumePackets(int numberOfResponses, WarningMessageCallback warningCallback) {
        wireOperations.consumePackets(numberOfResponses, warningCallback);
    }

    @Override
    public final GenericResponse readGenericResponse(WarningMessageCallback warningCallback)
            throws SQLException, IOException {
        return wireOperations.readGenericResponse(warningCallback);
    }

    @Override
    public final SqlResponse readSqlResponse(WarningMessageCallback warningCallback) throws SQLException, IOException {
        return wireOperations.readSqlResponse(warningCallback);
    }

    @Override
    public final Response readResponse(WarningMessageCallback warningCallback) throws SQLException, IOException {
        return wireOperations.readResponse(warningCallback);
    }

    public EventHandle createEventHandle(String eventName, EventHandler eventHandler) {
        return new WireEventHandle(eventName, eventHandler, getEncoding());
    }

    public void countEvents(EventHandle eventHandle) throws SQLException {
        if (!(eventHandle instanceof WireEventHandle))
            throw new SQLException("Invalid event handle, type: " + eventHandle.getClass().getName());

        ((WireEventHandle) eventHandle).calculateCount();
    }

    /**
     * Initializes the asynchronous channel (for event notification).
     *
     * @throws java.sql.SQLException
     *         For errors establishing the channel, or if the channel already exists.
     */
    public abstract FbWireAsynchronousChannel initAsynchronousChannel() throws SQLException;

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!connection.isConnected()) return;
            if (isAttached()) {
                safelyDetach();
            } else {
                closeConnection();
            }
        } finally {
            super.finalize();
        }
    }
}
