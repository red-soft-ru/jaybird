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
package org.firebirdsql.management;

import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.ServiceParameterBuffer;
import org.firebirdsql.gds.ServiceRequestBuffer;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSServerVersion;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.ng.*;
import org.firebirdsql.jdbc.FBConnectionProperties;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import static org.firebirdsql.gds.ISCConstants.*;
import static org.firebirdsql.gds.VaxEncoding.iscVaxInteger2;

/**
 * An implementation of the basic Firebird Service API functionality.
 *
 * @author <a href="mailto:rrokytskyy@users.sourceforge.net">Roman Rokytskyy</a>
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 */
public class FBServiceManager implements ServiceManager {

    private final FirebirdConnectionProperties serviceProperties = new FBConnectionProperties();
    private FbDatabaseFactory dbFactory;
    private String database;
    private OutputStream logger;

    public final static int BUFFER_SIZE = 1024; //1K

    /**
     * Create a new instance of <code>FBServiceManager</code> based on
     * the default GDSType.
     */
    public FBServiceManager() {
        this(GDSFactory.getDefaultGDSType());
    }

    /**
     * Create a new instance of <code>FBServiceManager</code> based on
     * a given GDSType.
     *
     * @param gdsType
     *         type must be PURE_JAVA, EMBEDDED, or NATIVE
     */
    public FBServiceManager(String gdsType) {
        this(GDSType.getType(gdsType));
    }

    /**
     * Create a new instance of <code>FBServiceManager</code> based on
     * a given GDSType.
     *
     * @param gdsType
     *         The GDS implementation type to use
     */
    public FBServiceManager(GDSType gdsType) {
        dbFactory = GDSFactory.getDatabaseFactoryForType(gdsType);
    }

    @Override
    public void setCharSet(String charSet) {
        serviceProperties.setCharSet(charSet);
    }

    @Override
    public String getCharSet() {
        return serviceProperties.getCharSet();
    }
    
    @Override
    public String getEncoding() {
        return serviceProperties.getEncoding();
    }

    @Override
    public void setEncoding(String encoding) {
        serviceProperties.setEncoding(encoding);
    }

    /**
     * Set the name of the user that performs the operation.
     *
     * @param user
     *         name of the user.
     */
    public void setUserName(String user) {
        serviceProperties.setUserName(user);
    }

    /**
     * Get name of the user that performs the operation.
     *
     * @return name of the user that performs the operation.
     */
    public String getUserName() {
        return serviceProperties.getUserName();
    }

    /**
     * @param password
     *         The password to set.
     */
    public void setPassword(String password) {
        serviceProperties.setPassword(password);
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return serviceProperties.getPassword();
    }
    
    @Override
    public int getBuffersNumber() {
        return serviceProperties.getBuffersNumber();
    }

    @Override
    public void setBuffersNumber(int buffersNumber) {
        serviceProperties.setBuffersNumber(buffersNumber);
    }

    @Override
    public String getNonStandardProperty(String key) {
        return serviceProperties.getNonStandardProperty(key);
    }

    @Override
    public void setNonStandardProperty(String key, String value) {
        serviceProperties.setNonStandardProperty(key, value);
    }

    @Override
    public void setNonStandardProperty(String propertyMapping) {
        serviceProperties.setNonStandardProperty(propertyMapping);
    }

    @Override
    public DatabaseParameterBuffer getDatabaseParameterBuffer() throws SQLException {
        return serviceProperties.getDatabaseParameterBuffer();
    }

    @Override
    public String getTpbMapping() {
        return serviceProperties.getTpbMapping();
    }

    @Override
    public void setTpbMapping(String tpbMapping) {
        serviceProperties.setTpbMapping(tpbMapping);
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return serviceProperties.getDefaultTransactionIsolation();
    }

    @Override
    public void setDefaultTransactionIsolation(int defaultIsolationLevel) {
        serviceProperties.setDefaultTransactionIsolation(defaultIsolationLevel);
    }

    @Override
    public String getDefaultIsolation() {
        return serviceProperties.getDefaultIsolation();
    }

    @Override
    public void setDefaultIsolation(String isolation) {
        serviceProperties.setDefaultIsolation(isolation);
    }

    @Override
    public TransactionParameterBuffer getTransactionParameters(int isolation) {
        return serviceProperties.getTransactionParameters(isolation);
    }

    @Override
    public void setTransactionParameters(int isolation, TransactionParameterBuffer tpb) {
        serviceProperties.setTransactionParameters(isolation, tpb);
    }

    @Override
    public boolean isDefaultResultSetHoldable() {
        return serviceProperties.isDefaultResultSetHoldable();
    }

    @Override
    public void setDefaultResultSetHoldable(boolean isHoldable) {
        serviceProperties.setDefaultResultSetHoldable(isHoldable);
    }

    @Override
    public int getSoTimeout() {
        return serviceProperties.getSoTimeout();
    }

    @Override
    public void setSoTimeout(int soTimeout) {
        serviceProperties.setSoTimeout(soTimeout);
    }

    @Override
    public int getConnectTimeout() {
        return serviceProperties.getConnectTimeout();
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        serviceProperties.setConnectTimeout(connectTimeout);
    }

    @Override
    public boolean isUseFirebirdAutocommit() {
        return serviceProperties.isUseFirebirdAutocommit();
    }

    @Override
    public void setUseFirebirdAutocommit(boolean useFirebirdAutocommit) {
        serviceProperties.setUseFirebirdAutocommit(useFirebirdAutocommit);
    }

    @Override
    public boolean isUseGSSAuth() {
        return serviceProperties.isUseGSSAuth();
    }

    @Override
    public void setUseGSSAuth(boolean useGSSAuth) {
        serviceProperties.setUseGSSAuth(useGSSAuth);
    }

    @Override
    public String getCertificate() {
        return serviceProperties.getCertificate();
    }

    @Override
    public void setCertificate(String certificate) {
        serviceProperties.setCertificate(certificate);
    }

    @Override
    public String getRepositoryPin() {
        return serviceProperties.getRepositoryPin();
    }

    @Override
    public void setRepositoryPin(String pin) {
        serviceProperties.setRepositoryPin(pin);
    }

    @Override
    public int getProviderID() {
        return serviceProperties.getProviderID();
    }

    @Override
    public void setProviderID(int providerID) {
        serviceProperties.setProviderID(providerID);
    }

    @Override
    public boolean isNotEncryptedPassword() {
        return serviceProperties.isNotEncryptedPassword();
    }

    @Override
    public void setNotEncryptedPassword(final boolean notEncryptPassword) {
        serviceProperties.setNotEncryptedPassword(notEncryptPassword);
    }

    @Override
    public boolean getVerifyServerCertificate() {
        return serviceProperties.getVerifyServerCertificate();
    }

    @Override
    public void setVerifyServerCertificate(boolean verify) {
        serviceProperties.setVerifyServerCertificate(verify);
    }

    public void setDatabase(String database) {
        this.database = database;
        this.serviceProperties.setDatabase(database);
    }

    public String getDatabase() {
        return database;
    }

    /**
     * @return Returns the host.
     */
    public String getServer() {
        return serviceProperties.getServer();
    }

    /**
     * @param host
     *         The host to set.
     */
    public void setServer(String host) {
        serviceProperties.setServer(host);
    }

    /**
     * @return Returns the port.
     */
    public int getPort() {
        return serviceProperties.getPort();
    }

    /**
     * @param port
     *         The port to set.
     */
    public void setPort(int port) {
        serviceProperties.setPort(port);
    }

    @Override
    public String getType() {
        return serviceProperties.getType();
    }

    @Override
    public void setType(String type) {
        serviceProperties.setType(type);
    }

    @Override
    public int getBlobBufferSize() {
        return serviceProperties.getBlobBufferSize();
    }

    @Override
    public void setBlobBufferSize(int bufferSize) {
        serviceProperties.setBlobBufferSize(bufferSize);
    }

    /**
     * @return Returns the role.
     */
    public String getRoleName() {
        return serviceProperties.getRoleName();
    }

    /**
     * @param role
     *         The role to set.
     */
    public void setRoleName(String role) {
        serviceProperties.setRoleName(role);
    }

    @Override
    public String getSqlDialect() {
        return serviceProperties.getSqlDialect();
    }

    @Override
    public void setSqlDialect(String sqlDialect) {
        serviceProperties.setSqlDialect(sqlDialect);
    }

    @Override
    public String getUseTranslation() {
        return serviceProperties.getUseTranslation();
    }

    @Override
    public void setUseTranslation(String translationPath) {
        serviceProperties.setUseTranslation(translationPath);
    }

    @Override
    public boolean isUseStreamBlobs() {
        return serviceProperties.isUseStreamBlobs();
    }

    @Override
    public void setUseStreamBlobs(boolean useStreamBlobs) {
        serviceProperties.setUseStreamBlobs(useStreamBlobs);
    }

    @Override
    public boolean isUseStandardUdf() {
        return serviceProperties.isUseStandardUdf();
    }

    @Override
    public void setUseStandardUdf(boolean useStandardUdf) {
        serviceProperties.setUseStandardUdf(useStandardUdf);
    }

    @Override
    public int getSocketBufferSize() {
        return serviceProperties.getSocketBufferSize();
    }

    @Override
    public void setSocketBufferSize(int socketBufferSize) {
        serviceProperties.setSocketBufferSize(socketBufferSize);
    }

    @Override
    public boolean isTimestampUsesLocalTimezone() {
        return serviceProperties.isTimestampUsesLocalTimezone();
    }

    @Override
    public void setTimestampUsesLocalTimezone(boolean timestampUsesLocalTimezone) {
        serviceProperties.setTimestampUsesLocalTimezone(timestampUsesLocalTimezone);
    }

    @Override
    public String getWireCrypt() {
        return serviceProperties.getWireCrypt();
    }

    @Override
    public void setWireCrypt(String wireCrypt) {
        serviceProperties.setWireCrypt(wireCrypt);
    }

    @Override
    public String getDbCryptConfig() {
        return serviceProperties.getDbCryptConfig();
    }

    @Override
    public void setDbCryptConfig(String dbCryptConfig) {
        serviceProperties.setDbCryptConfig(dbCryptConfig);
    }

    @Override
    public boolean isIgnoreProcedureType() {
        return serviceProperties.isIgnoreProcedureType();
    }

    @Override
    public void setIgnoreProcedureType(boolean ignoreProcedureType) {
        serviceProperties.setIgnoreProcedureType(ignoreProcedureType);
    }

    /**
     * @return Returns the out.
     */
    public synchronized OutputStream getLogger() {
        return logger;
    }

    /**
     * @param logger
     *         The out to set.
     */
    public synchronized void setLogger(OutputStream logger) {
        this.logger = logger;
    }

    public String getServiceName() {
        StringBuilder sb = new StringBuilder();
        if (getServer() != null) {

            sb.append(getServer());

            if (getPort() != 3050) {
                sb.append('/');
                sb.append(getPort());
            }

            sb.append(':');
        }
        sb.append("service_mgr");
        return sb.toString();
    }

    @Override
    public void setServiceName(String serviceName) {
        serviceProperties.setServiceName(serviceName);
    }

    @Override
    public short getConnectionDialect() {
        return serviceProperties.getConnectionDialect();
    }

    @Override
    public void setConnectionDialect(short connectionDialect) {
        serviceProperties.setConnectionDialect(connectionDialect);
    }

    @Override
    public int getPageCacheSize() {
        return serviceProperties.getPageCacheSize();
    }

    @Override
    public void setPageCacheSize(int pageCacheSize) {
        serviceProperties.setPageCacheSize(pageCacheSize);
    }

    @Override
    public boolean isResultSetDefaultHoldable() {
        return serviceProperties.isResultSetDefaultHoldable();
    }

    @Override
    public void setResultSetDefaultHoldable(boolean holdable) {
        serviceProperties.setResultSetDefaultHoldable(holdable);
    }

    @Override
    public boolean isColumnLabelForName() {
        return serviceProperties.isColumnLabelForName();
    }

    @Override
    public void setColumnLabelForName(boolean columnLabelForName) {
        serviceProperties.setColumnLabelForName(columnLabelForName);
    }

    @Override
    public String getCertificateBase64() {
        return serviceProperties.getCertificateBase64();
    }

    @Override
    public void setCertificateBase64(String certificateBase64) {
        serviceProperties.setCertificateBase64(certificateBase64);
    }

    @Override
    public String getEffectiveLogin() {
        return serviceProperties.getEffectiveLogin();
    }

    @Override
    public void setEffectiveLogin(String effectiveLogin) {
        serviceProperties.setEffectiveLogin(effectiveLogin);
    }

    @Override
    public String getExcludeCryptoPlugins() {
        return serviceProperties.getExcludeCryptoPlugins();
    }

    @Override
    public void setExcludeCryptoPlugins(String excludeCryptoPlugins) {
        serviceProperties.setExcludeCryptoPlugins(excludeCryptoPlugins);
    }

    @Override
    public DatabaseParameterBuffer getExtraDatabaseParameters() {
        return serviceProperties.getExtraDatabaseParameters();
    }

    @Override
    public FirebirdConnectionProperties asImmutable() {
        return serviceProperties.asImmutable();
    }

    @Override
    public FirebirdConnectionProperties asNewMutable() {
        return serviceProperties.asNewMutable();
    }

    public FbService attachServiceManager() throws SQLException {
        FbService fbService = dbFactory.serviceConnect(serviceProperties);
        fbService.attach();
        return fbService;
    }

    protected FbDatabase attachDatabase() throws SQLException {
        if (database == null) {
            throw new SQLException("Property database needs to be set.");
        }
        FbDatabase fbDatabase = dbFactory.connect(serviceProperties);
        fbDatabase.attach();
        return fbDatabase;
    }

    public void queueService(FbService service) throws SQLException, IOException {
        OutputStream currentLogger = getLogger();

        ServiceRequestBuffer infoSRB = service.createServiceRequestBuffer();
        infoSRB.addArgument(isc_info_svc_to_eof);

        ServiceParameterBuffer reqSPB = service.createServiceParameterBuffer();

        // use one second timeout to poll service
        byte sendTimeout[] = { 1 };

        reqSPB.addArgument(isc_info_svc_timeout, sendTimeout);

        int bufferSize = BUFFER_SIZE;

        boolean processing = true;
        while (processing) {
            byte[] buffer = service.getServiceInfo(reqSPB, infoSRB, bufferSize);

            switch (buffer[0]) {
            case isc_info_svc_to_eof:

                int dataLength = iscVaxInteger2(buffer, 1);
                if (dataLength == 0) {
                    if (buffer[3] == isc_info_svc_timeout) {
                        break;
                    }
                    else if (buffer[3] != isc_info_end)
                        throw new SQLException("Unexpected end of stream reached.");
                    else {
                        processing = false;
                        break;
                    }
                }

                if (currentLogger != null) {
                    currentLogger.write(buffer, 3, dataLength);
                }

                break;

            case isc_info_truncated:
                bufferSize = bufferSize * 2;
                break;

            case isc_info_end:
                processing = false;
                break;
            }
        }
    }

    /**
     * Execute a Services API operation in the database. All output from the
     * operation is sent to this <code>ServiceManager</code>'s logger.
     *
     * @param srb
     *         The buffer containing the task request
     * @throws SQLException
     *         if a database access error occurs or
     *         incorrect parameters are supplied
     * @deprecated Use {@link #executeServicesOperation(FbService, ServiceRequestBuffer)}.
     */
    @SuppressWarnings("unused")
    @Deprecated
    protected void executeServicesOperation(ServiceRequestBuffer srb) throws SQLException {
        try (FbService service = attachServiceManager()) {
            service.startServiceAction(srb);
            queueService(service);
        } catch (IOException ioe) {
            throw new SQLException(ioe);
        }
    }

    protected final void executeServicesOperation(FbService service, ServiceRequestBuffer srb) throws SQLException {
        try {
            service.startServiceAction(srb);
            queueService(service);
        } catch (IOException ioe) {
            throw new SQLException(ioe);
        }
    }

    protected ServiceRequestBuffer createRequestBuffer(FbService service, int operation, int options) {
        ServiceRequestBuffer srb = service.createServiceRequestBuffer();
        srb.addArgument(operation);
        if (getDatabase() != null) {
            srb.addArgument(isc_spb_dbname, getDatabase());
        }
        srb.addArgument(isc_spb_options, options);
        return srb;
    }

    @Override
    public GDSServerVersion getServerVersion() throws SQLException {
        try (FbService service = attachServiceManager()) {
            return service.getServerVersion();
        }
    }
}
