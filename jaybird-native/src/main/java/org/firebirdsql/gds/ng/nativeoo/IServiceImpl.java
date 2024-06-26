package org.firebirdsql.gds.ng.nativeoo;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.JaybirdErrorCodes;
import org.firebirdsql.gds.ServiceParameterBuffer;
import org.firebirdsql.gds.ServiceRequestBuffer;
import org.firebirdsql.gds.impl.ServiceParameterBufferImp;
import org.firebirdsql.gds.impl.ServiceRequestBufferImp;
import org.firebirdsql.gds.ng.AbstractFbService;
import org.firebirdsql.gds.ng.FbAttachment;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.LockCloseable;
import org.firebirdsql.gds.ng.ParameterConverter;
import org.firebirdsql.gds.ng.WarningMessageCallback;
import org.firebirdsql.jdbc.FBDriverNotCapableException;
import org.firebirdsql.jna.fbclient.CloseableMemory;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface;
import org.firebirdsql.jna.fbclient.FbInterface.IMaster;
import org.firebirdsql.jna.fbclient.FbInterface.IProvider;
import org.firebirdsql.jna.fbclient.FbInterface.IService;
import org.firebirdsql.jna.fbclient.FbInterface.IStatus;

import java.sql.SQLException;

/**
 * Implementation of {@link FbInterface.IService} for native client access using OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IServiceImpl extends AbstractFbService<IServiceConnectionImpl> implements FbAttachment {

    // TODO Find out if there are any exception from JNA that we need to be prepared to handle.

    private static final ParameterConverter<?, IServiceConnectionImpl> PARAMETER_CONVERTER = new IParameterConverterImpl();
    private FbClientLibrary clientLibrary;
    private IMaster master;
    private IProvider provider;
    private IStatus status;
    private IService service;

    public IServiceImpl(IServiceConnectionImpl connection) {
        super(connection, connection.createDatatypeCoder());
        clientLibrary = connection.getClientLibrary();
        master = ((FbInterface) clientLibrary).fb_get_master_interface();
        status = master.getStatus();
        provider = master.getDispatcher();
    }

    @Override
    public ServiceParameterBuffer createServiceParameterBuffer() {
        // TODO When Firebird 3, use UTF-8; implement similar mechanism as ProtocolDescriptor of wire?
        return new ServiceParameterBufferImp(ServiceParameterBufferImp.SpbMetaData.SPB_VERSION_2, getEncoding());
    }

    @Override
    public ServiceRequestBuffer createServiceRequestBuffer() {
        // TODO When Firebird 3, use UTF-8; implement similar mechanism as ProtocolDescriptor of wire?
        return new ServiceRequestBufferImp(ServiceRequestBufferImp.SrbMetaData.SRB_VERSION_2, getEncoding());
    }

    @Override
    protected void checkConnected() throws SQLException {
        if (!isAttached()) {
            throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_notAttachedToDatabase)
                    .toFlatSQLException();
        }
    }

    @Override
    public byte[] getServiceInfo(ServiceParameterBuffer serviceParameterBuffer,
                                 ServiceRequestBuffer serviceRequestBuffer, int maxBufferLength) throws SQLException {
        checkConnected();
        try {
            final byte[] serviceParameterBufferBytes = serviceParameterBuffer == null ? null
                    : serviceParameterBuffer.toBytes();
            final byte[] serviceRequestBufferBytes =
                    serviceRequestBuffer == null ? null : serviceRequestBuffer.toBytes();
            try (LockCloseable ignored = withLock();
                 CloseableMemory memResponseBuffer = new CloseableMemory(maxBufferLength)) {
                CloseableMemory memServiceParameterBufferBytes = null;
                if (serviceParameterBuffer != null) {
                    memServiceParameterBufferBytes = new CloseableMemory(serviceParameterBufferBytes.length);
                    memServiceParameterBufferBytes.write(0, serviceParameterBufferBytes, 0, serviceParameterBufferBytes.length);
                }
                CloseableMemory memServiceRequestBufferBytes = null;
                if (serviceRequestBufferBytes != null){
                    memServiceRequestBufferBytes = new CloseableMemory(serviceRequestBufferBytes.length);
                    memServiceRequestBufferBytes.write(0, serviceRequestBufferBytes, 0, serviceRequestBufferBytes.length);
                }

                service.query(getStatus(), (serviceParameterBufferBytes != null ? serviceParameterBufferBytes.length
                                : 0), memServiceParameterBufferBytes,
                        (serviceRequestBufferBytes != null ? serviceRequestBufferBytes.length
                                : 0), memServiceRequestBufferBytes,
                        maxBufferLength, memResponseBuffer);
                processStatus();
                if (memServiceParameterBufferBytes != null)
                    memServiceParameterBufferBytes.close();
                if (memServiceRequestBufferBytes != null)
                    memServiceRequestBufferBytes.close();
                return memResponseBuffer.getByteArray(0, maxBufferLength);
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void startServiceAction(ServiceRequestBuffer serviceRequestBuffer) throws SQLException {
        checkConnected();
        try {
            final byte[] serviceRequestBufferBytes = serviceRequestBuffer == null
                    ? null
                    : serviceRequestBuffer.toBytes();
            try (LockCloseable ignored = withLock();
                 CloseableMemory memServiceRequestBufferBytes = new CloseableMemory(serviceRequestBufferBytes.length)) {
                memServiceRequestBufferBytes.write(0, serviceRequestBufferBytes, 0, serviceRequestBufferBytes.length);
                service.start(getStatus(), (serviceRequestBufferBytes != null ? serviceRequestBufferBytes.length : 0),
                        memServiceRequestBufferBytes);
                processStatus();
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public void attach() throws SQLException {
        try {
            if (isAttached()) {
                throw new SQLException("Already attached to a service");
            }
            final ServiceParameterBuffer spb = PARAMETER_CONVERTER.toServiceParameterBuffer(connection);
            final byte[] serviceName = getEncoding().encodeToCharset(connection.getAttachUrl().concat("\0"));
            final byte[] spbArray = spb.toBytesWithType();

            try (LockCloseable ignored = withLock();
                 CloseableMemory memServiceName = new CloseableMemory(serviceName.length);
                 CloseableMemory memSBPArr = new CloseableMemory(spbArray.length)) {
                memServiceName.write(0, serviceName, 0, serviceName.length);
                memSBPArr.write(0, spbArray, 0, spbArray.length);
                try {
                    service = provider.attachServiceManager(getStatus(), memServiceName, spbArray.length, memSBPArr);
                    processStatus();
                } catch (SQLException ex) {
                    safelyDetach();
                    throw ex;
                } catch (Exception ex) {
                    safelyDetach();
                    // TODO Replace with specific error (eg native client error)
                    throw new FbExceptionBuilder()
                            .exception(ISCConstants.isc_network_error)
                            .messageParameter(connection.getAttachUrl())
                            .cause(ex)
                            .toSQLException();
                }
                setAttached();
                afterAttachActions();
            }
        } catch (SQLException e) {
            exceptionListenerDispatcher.errorOccurred(e);
            throw e;
        }
    }

    @Override
    public int getHandle() {
        throw new UnsupportedOperationException( "Native OO API not support service handle" );
    }

    @Override
    public void setNetworkTimeout(int milliseconds) throws SQLException {
        throw new FBDriverNotCapableException(
                "Setting network timeout not supported in native implementation");
    }

    /**
     * Additional tasks to execute directly after attach operation.
     * <p>
     * Implementation retrieves service information like server version.
     * </p>
     *
     * @throws SQLException
     *         For errors reading or writing database information.
     */
    protected void afterAttachActions() throws SQLException {
        getServiceInfo(null, getDescribeServiceRequestBuffer(), 1024, getServiceInformationProcessor());
    }

    @Override
    protected void internalDetach() throws SQLException {
        checkConnected();
        try (LockCloseable ignored = withLock()) {
            try {
                service.detach(getStatus());
                processStatus();
                clientLibrary = null;
                provider.release();
                provider = null;
            } catch (SQLException ex) {
                throw ex;
            } catch (Exception ex) {
                // TODO Replace with specific error (eg native client error)
                throw new FbExceptionBuilder()
                        .exception(ISCConstants.isc_network_error)
                        .messageParameter(connection.getAttachUrl())
                        .cause(ex)
                        .toSQLException();
            } finally {
                if (status != null) {
                    status.dispose();
                    status = null;
                }
                setDetached();
            }
        }
    }

    private IStatus getStatus() {
        status.init();
        return status;
    }

    private void processStatus() throws SQLException {
        processStatus(status, getServiceWarningCallback());
    }

    public void processStatus(IStatus status, WarningMessageCallback warningMessageCallback)
            throws SQLException {
        if (warningMessageCallback == null) {
            warningMessageCallback = getServiceWarningCallback();
        }
        connection.processStatus(status, warningMessageCallback);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (isAttached()) {
                safelyDetach();
            }
        } finally {
            super.finalize();
        }
    }
}
