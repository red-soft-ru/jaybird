/*
 * Firebird Open Source JDBC Driver
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
package org.firebirdsql.gds.ng.wire.version10;

import org.firebirdsql.gds.ClumpletReader;
import org.firebirdsql.gds.ServiceParameterBuffer;
import org.firebirdsql.gds.ServiceRequestBuffer;
import org.firebirdsql.gds.impl.wire.XdrOutputStream;
import org.firebirdsql.gds.impl.wire.auth.AuthSspi;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.dbcrypt.DbCryptCallback;
import org.firebirdsql.gds.ng.wire.*;
import org.firebirdsql.jaybird.fb.constants.DpbItems;
import org.firebirdsql.jaybird.fb.constants.SpbItems;

import java.io.IOException;
import java.sql.SQLException;

import static org.firebirdsql.gds.impl.wire.WireProtocolConstants.*;

/**
 * {@link FbWireService} implementation for the V10 wire protocol
 *
 * @author Mark Rotteveel
 * @since 3.0
 */
public class V10Service extends AbstractFbWireService implements FbWireService {

    public V10Service(WireServiceConnection connection, ProtocolDescriptor descriptor) {
        super(connection, descriptor);
    }

    @Override
    @SuppressWarnings("java:S1141")
    public void attach() throws SQLException {
        try {
            checkConnected();
            requireNotAttached();
            try (var ignored = withLock()) {
                try {
                    sendAttach();
                    receiveAttachResponse();
                } catch (SQLException e) {
                    safelyDetach();
                    throw e;
                }
                setAttached();
                afterAttachActions();
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private void sendAttach() throws SQLException {
        try {
            sendAttachToBuffer(protocolDescriptor.createAttachServiceParameterBuffer(connection));
            getXdrOut().flush();
        } catch (IOException e) {
            throw FbExceptionBuilder.ioWriteError(e);
        }
    }

    private void receiveAttachResponse() throws SQLException {
        try {
            authReceiveResponse(null);
        } catch (IOException e) {
            throw FbExceptionBuilder.ioReadError(e);
        }
    }

    /**
     * Processes the response from the server to the attach or create operation.
     *
     * @param genericResponse
     *         GenericResponse received from the server.
     */
    @SuppressWarnings("unused")
    protected void processAttachResponse(GenericResponse genericResponse) {
        // nothing to do
    }

    protected void afterAttachActions() throws SQLException {
        getServiceInfo(null, getDescribeServiceRequestBuffer(), 1024, getServiceInformationProcessor());
        // During connect and attach the socketTimeout might be set to the connectTimeout, now reset to 'normal' socketTimeout
        connection.resetSocketTimeout();
    }

    /**
     * Sends the buffer for op_service_attach
     *
     * @param spb
     *         Service parameter buffer
     * @throws SQLException
     *         If the connection is not open
     * @throws IOException
     *         For errors writing to the connection
     */
    protected void sendAttachToBuffer(ServiceParameterBuffer spb) throws SQLException, IOException {
        final XdrOutputStream xdrOut = getXdrOut();
        
        final boolean trustedAuth = spb.hasArgument(SpbItems.isc_spb_trusted_auth);
        final boolean multifactor = spb.hasArgument(SpbItems.isc_spb_multi_factor_auth);

        if (connection.getProtocolVersion() < PROTOCOL_VERSION13) {
            if (trustedAuth && !multifactor)
                throw new SQLException("Trusted authorization is not supported. Use multi factor authorization instead of this one.");

            AuthSspi sspi;
            if (multifactor) {
                if (!spb.hasArgument(DpbItems.isc_dpb_password) && connection.getAttachProperties().getPassword() != null)
                    spb.addArgument(DpbItems.isc_dpb_password, connection.getAttachProperties().getPassword());
                sspi = new AuthSspi();
                sspi.setClumpletReaderType(ClumpletReader.Kind.Tagged);
                sspi.setSkipWireKeyTag(true);
                if (spb.hasArgument(DpbItems.isc_dpb_repository_pin))
                    sspi.setRepositoryPin(connection.getAttachProperties().getRepositoryPin());
                if (spb.hasArgument(SpbItems.isc_spb_provider_id)) {
                    sspi.setProviderID(spb.getArgumentAsInt(SpbItems.isc_spb_provider_id));
                    spb.removeArgument(SpbItems.isc_spb_provider_id);
                }
                sspi.fillFactors(spb);
            } else sspi = null;

            connection.setSspi(sspi);
        } else {
            if (spb.hasArgument(SpbItems.isc_spb_multi_factor_auth))
                spb.removeArgument(SpbItems.isc_spb_multi_factor_auth); // no need to send it to server
        }


        xdrOut.writeInt(op_service_attach);
        xdrOut.writeInt(0); // Service object ID
        xdrOut.writeString(connection.getAttachObjectName(), getEncoding());
        xdrOut.writeTyped(spb);
    }

    @Override
    @SuppressWarnings("java:S1141")
    protected void internalDetach() throws SQLException {
        try (LockCloseable ignored = withLock()) {
            try {
                sendDetachDisconnect();
                if (isAttached()) {
                    receiveDetachResponse();
                }
                try {
                    closeConnection();
                } catch (IOException e) {
                    throw FbExceptionBuilder.ioWriteError(e);
                }
            } catch (SQLException ex) {
                try {
                    closeConnection();
                } catch (Exception ex2) {
                    // ignore
                }
                throw ex;
            } finally {
                setDetached();
            }
        }
    }

    private void sendDetachDisconnect() throws SQLException {
        try {
            XdrOutputStream xdrOut = getXdrOut();
            if (isAttached()) {
                xdrOut.writeInt(op_service_detach);
                xdrOut.writeInt(0);
            }
            xdrOut.writeInt(op_disconnect);
            xdrOut.flush();
        } catch (IOException e) {
            throw FbExceptionBuilder.ioWriteError(e);
        }
    }

    private void receiveDetachResponse() throws SQLException {
        try {
            // Consume op_service_detach response
            wireOperations.readResponse(null);
        } catch (IOException e) {
            throw FbExceptionBuilder.ioReadError(e);
        }
    }

    @Override
    public byte[] getServiceInfo(ServiceParameterBuffer serviceParameterBuffer,
            ServiceRequestBuffer serviceRequestBuffer, int maxBufferLength) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkAttached();
            sendServiceInfo(serviceParameterBuffer, serviceRequestBuffer, maxBufferLength);
            return receiveServiceInfoResponse();
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private void sendServiceInfo(ServiceParameterBuffer serviceParameterBuffer,
            ServiceRequestBuffer serviceRequestBuffer, int maxBufferLength) throws SQLException {
        try {
            XdrOutputStream xdrOut = getXdrOut();
            xdrOut.writeInt(op_service_info);
            xdrOut.writeInt(0);
            xdrOut.writeInt(0); // incarnation
            xdrOut.writeBuffer(serviceParameterBuffer != null ? serviceParameterBuffer.toBytes() : null);
            xdrOut.writeBuffer(serviceRequestBuffer.toBytes());
            xdrOut.writeInt(maxBufferLength);

            xdrOut.flush();
        } catch (IOException e) {
            throw FbExceptionBuilder.ioWriteError(e);
        }
    }

    private byte[] receiveServiceInfoResponse() throws SQLException {
        try {
            return readGenericResponse(null).data();
        } catch (IOException e) {
            throw FbExceptionBuilder.ioReadError(e);
        }
    }

    @Override
    public void startServiceAction(ServiceRequestBuffer serviceRequestBuffer) throws SQLException {
        try (LockCloseable ignored = withLock()) {
            checkAttached();
            sendServiceStart(serviceRequestBuffer);
            receiveServiceStartResponse();
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    private void sendServiceStart(ServiceRequestBuffer serviceRequestBuffer) throws SQLException {
        try {
            final XdrOutputStream xdrOut = getXdrOut();
            xdrOut.writeInt(op_service_start);
            xdrOut.writeInt(0);
            xdrOut.writeInt(0); // incarnation
            xdrOut.writeBuffer(serviceRequestBuffer.toBytes());

            xdrOut.flush();
        } catch (IOException e) {
            throw FbExceptionBuilder.ioWriteError(e);
        }
    }

    private void receiveServiceStartResponse() throws SQLException {
        try {
            readGenericResponse(null);
        } catch (IOException e) {
            throw FbExceptionBuilder.ioReadError(e);
        }
    }

    @Override
    public final void authReceiveResponse(AcceptPacket acceptPacket) throws IOException, SQLException {
        final DbCryptCallback dbCryptCallback = connection.createDbCryptCallback();
        wireOperations.authReceiveResponse(acceptPacket, dbCryptCallback, V10Service.this::processAttachResponse);
    }
}
