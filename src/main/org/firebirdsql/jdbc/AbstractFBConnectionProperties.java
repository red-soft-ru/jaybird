package org.firebirdsql.jdbc;

import org.firebirdsql.encodings.EncodingFactory;
import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.ParameterBufferHelper;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.DatabaseParameterBufferImp;
import org.firebirdsql.jca.FBResourceException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.firebirdsql.jdbc.FBDriverPropertyManager.getCanonicalName;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public abstract class AbstractFBConnectionProperties implements FirebirdConnectionProperties, Serializable, Cloneable {

    private static final long serialVersionUID = 3930908474394844438L;

    protected Map<String, Object> properties = new HashMap<>();
    protected String type;
    protected String database;
    protected String server = FirebirdConnectionProperties.DEFAULT_SERVER;
    protected int port = FirebirdConnectionProperties.DEFAULT_PORT;
    protected String serviceName = FirebirdConnectionProperties.DEFAULT_SERVICE_NAME;

    protected String tpbMapping;
    protected int defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
    protected Map<Integer, TransactionParameterBuffer> customMapping = new HashMap<>();
    protected FBTpbMapper mapper;
    protected String sessionTimeZone = TimeZone.getDefault().getID();
    protected DatabaseParameterBuffer extraDatabaseParameters = new DatabaseParameterBufferImp(
            DatabaseParameterBufferImp.DpbMetaData.DPB_VERSION_1,
            EncodingFactory.getPlatformEncoding());
    protected short connectionDialect = FirebirdConnectionProperties.DEFAULT_DIALECT;
    protected int socketBufferSize = FirebirdConnectionProperties.DEFAULT_SOCKET_BUFFER_SIZE;
    protected int soTimeout = FirebirdConnectionProperties.DEFAULT_SO_TIMEOUT;
    protected int connectTimeout = FirebirdConnectionProperties.DEFAULT_CONNECT_TIMEOUT;

    public AbstractFBConnectionProperties() {

    }

    public AbstractFBConnectionProperties(AbstractFBConnectionProperties src) {
        this.properties = src.properties;
        this.type = src.type;
        this.database = src.database;
        this.server = src.server;
        this.port = src.port;
        this.tpbMapping = src.tpbMapping;
        this.defaultTransactionIsolation = src.defaultTransactionIsolation;
        this.customMapping = src.customMapping;
        this.mapper = src.mapper;
        this.connectionDialect = src.connectionDialect;
        this.socketBufferSize = src.socketBufferSize;
        this.soTimeout = src.soTimeout;
        this.connectTimeout = src.connectTimeout;
        this.sessionTimeZone = src.sessionTimeZone;
        this.extraDatabaseParameters = src.extraDatabaseParameters.deepCopy();
    }

    protected int getIntProperty(String name) {
        Integer value = (Integer) properties.get(getCanonicalName(name));
        return value != null ? value : 0;
    }

    protected String getStringProperty(String name) {
        Object value = properties.get(getCanonicalName(name));
        return value != null ? value.toString() : null;
    }

    protected boolean getBooleanProperty(String name) {
        String canonicalName = getCanonicalName(name);
        return properties.containsKey(canonicalName) && (Boolean) properties.get(canonicalName);
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        immutable();
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        immutable();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        immutable();
    }

    public short getConnectionDialect() {
        return connectionDialect;
    }

    @Override
    public void setConnectionDialect(short connectionDialect) {
        immutable();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        immutable();
    }

    public int getBlobBufferSize() {
        return getIntProperty(BLOB_BUFFER_SIZE_PROPERTY);
    }

    public void setBlobBufferSize(int bufferSize) {
        immutable();
    }

    public String getCharSet() {
        return getStringProperty(LOCAL_ENCODING_PROPERTY);
    }

    public void setCharSet(String charSet) {
        immutable();
    }

    public String getEncoding() {
        return getStringProperty(ENCODING_PROPERTY);
    }

    public void setEncoding(String encoding) {
        immutable();
    }

    public String getRoleName() {
        return getStringProperty(ROLE_NAME_PROPERTY);
    }

    public void setRoleName(String roleName) {
        immutable();
    }

    public String getSqlDialect() {
        return getStringProperty(SQL_DIALECT_PROPERTY);
    }

    public void setSqlDialect(String sqlDialect) {
        immutable();
    }

    public String getUseTranslation() {
        return getStringProperty(USE_TRANSLATION_PROPERTY);
    }

    public void setUseTranslation(String translationPath) {
        immutable();
    }

    public boolean isUseStreamBlobs() {
        return getBooleanProperty(USE_STREAM_BLOBS_PROPERTY);
    }

    public void setUseStreamBlobs(boolean useStreamBlobs) {
        immutable();
    }

    public boolean isUseStandardUdf() {
        return getBooleanProperty(USE_STANDARD_UDF_PROPERTY);
    }

    public void setUseStandardUdf(boolean useStandardUdf) {
        immutable();
    }

    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    public void setSocketBufferSize(int socketBufferSize) {
        immutable();
    }

    public boolean isTimestampUsesLocalTimezone() {
        return getBooleanProperty(TIMESTAMP_USES_LOCAL_TIMEZONE_PROPERTY);
    }

    public void setTimestampUsesLocalTimezone(boolean timestampUsesLocalTimezone) {
        immutable();
    }

    public String getUserName() {
        return getStringProperty(USER_NAME_PROPERTY);
    }

    public void setUserName(String userName) {
        immutable();
    }

    public String getPassword() {
        return getStringProperty(PASSWORD_PROPERTY);
    }

    public void setPassword(String password) {
        immutable();
    }

    public int getBuffersNumber() {
        return getIntProperty(BUFFERS_NUMBER_PROPERTY);
    }

    public void setBuffersNumber(int buffersNumber) {
        immutable();
    }

    public String getNonStandardProperty(String key) {
        return getStringProperty(key);
    }

    public void setNonStandardProperty(String key, String value) {
        immutable();
    }

    public boolean isDefaultResultSetHoldable() {
        return getBooleanProperty(DEFAULT_HOLDABLE_RS_PROPERTY);
    }

    public void setDefaultResultSetHoldable(boolean isHoldable) {
        immutable();
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        immutable();
    }

    public boolean isUseGSSAuth() {
        return getBooleanProperty(USE_GSS_AUTH);
    }

    public void setUseGSSAuth(boolean useGSSAuth) {
        immutable();
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        immutable();
    }

    @Override
    public boolean isUseFirebirdAutocommit() {
        return getBooleanProperty(USE_FIREBIRD_AUTOCOMMIT);
    }

    @Override
    public void setUseFirebirdAutocommit(boolean useFirebirdAutocommit) {
        immutable();
    }

    @Override
    public String getWireCrypt() {
        return getStringProperty(WIRE_CRYPT_LEVEL);
    }

    @Override
    public void setWireCrypt(String wireCrypt) {
        immutable();
    }

    @Override
    public String getCertificate() {
        return getStringProperty(CERTIFICATE);
    }

    @Override
    public String getDbCryptConfig() {
        return getStringProperty(DB_CRYPT_CONFIG);
    }

    @Override
    public void setDbCryptConfig(String dbCryptConfig) {
        immutable();
    }

    @Override
    public boolean isIgnoreProcedureType() {
        return getBooleanProperty(IGNORE_PROCEDURE_TYPE);
    }

    @Override
    public void setIgnoreProcedureType(boolean ignoreProcedureType) {
        immutable();
    }

    @Override
    public void setCertificate(String certificate) {
        immutable();
    }

    @Override
    public String getRepositoryPin() {
        return getStringProperty(REPOSITORY_PIN);
    }

    @Override
    public void setRepositoryPin(String pin) {
        immutable();
    }

    @Override
    public boolean getVerifyServerCertificate() {
        return getBooleanProperty(SERVER_CERTIFICATE);
    }

    @Override
    public void setVerifyServerCertificate(boolean verify) {
        immutable();
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getCertificateBase64() {
        return getStringProperty(CERTIFICATE_BASE64);
    }

    @Override
    public void setCertificateBase64(String certificateBase64) {
        immutable();
    }

    @Override
    public String getEffectiveLogin() {
        return getStringProperty(EFFECTIVE_LOGIN);
    }

    @Override
    public void setEffectiveLogin(String effectiveLogin) {
        immutable();
    }

    @Override
    public String getExcludeCryptoPlugins() {
        return getStringProperty(EXCLUDE_CRYPTOPLUGINS);
    }

    @Override
    public void setExcludeCryptoPlugins(String excludeCryptoPlugins) {
        immutable();
    }

    @Override
    public boolean isNotEncryptedPassword() {
        return getBooleanProperty(NOT_ENCRYPTED_PASSWORD);
    }

    @Override
    public void setNotEncryptedPassword(final boolean notEncryptPassword) {
        immutable();
    }

    @Override
    public int getProviderID() {
        return getIntProperty(PROVIDER_ID);
    }

    @Override
    public void setProviderID(int providerID) {
        immutable();
    }

    @Override
    public int getPageCacheSize() {
        return getIntProperty(PAGE_CACHE_SIZE_PROPERTY);
    }

    @Override
    public void setPageCacheSize(int pageCacheSize) {
        immutable();
    }

    @Override
    public void setResultSetDefaultHoldable(boolean holdable) {
        immutable();
    }

    @Override
    public boolean isResultSetDefaultHoldable() {
        return getBooleanProperty(RESULTSET_HOLDABLE_PROPERTY);
    }

    @Override
    public void setColumnLabelForName(boolean columnLabelForName) {
        immutable();
    }

    @Override
    public boolean isColumnLabelForName() {
        return getBooleanProperty(COLUMN_LABEL_PROPERTY);
    }

    @Override
    public DatabaseParameterBuffer getExtraDatabaseParameters() {
        return extraDatabaseParameters;
    }

    @Override
    public FirebirdConnectionProperties asImmutable() {
        // Immutable already, so just return this
        return this;
    }

    @Override
    public FirebirdConnectionProperties asNewMutable() {
        return new FBConnectionProperties(this);
    }

    public void setNonStandardProperty(String propertyMapping) {
        immutable();
    }

    @Override
    public String getTpbMapping() {
        return tpbMapping;
    }

    public void setTpbMapping(String tpbMapping) {
        immutable();
    }

    public int getDefaultTransactionIsolation() {
        if (mapper != null) {
            return mapper.getDefaultTransactionIsolation();
        }
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(int defaultIsolationLevel) {
        immutable();
    }

    public String getDefaultIsolation() {
        return FBTpbMapper.getTransactionIsolationName(getDefaultTransactionIsolation());
    }

    public void setDefaultIsolation(String isolation) {
        immutable();
    }

    public TransactionParameterBuffer getTransactionParameters(int isolation) {
        if (mapper != null) {
            return mapper.getMapping(isolation);
        }
        return customMapping.get(isolation);
    }

    public void setTransactionParameters(int isolation, TransactionParameterBuffer tpb) {
        immutable();
    }

    public FBTpbMapper getMapper() throws FBResourceException {
        if (mapper != null) {
            return mapper;
        }

        if (tpbMapping == null) {
            mapper = FBTpbMapper.getDefaultMapper();
        } else {
            mapper = new FBTpbMapper(tpbMapping, getClass().getClassLoader());
        }

        mapper.setDefaultTransactionIsolation(defaultTransactionIsolation);

        for (Map.Entry<Integer, TransactionParameterBuffer> entry : customMapping.entrySet()) {
            Integer isolation = entry.getKey();
            TransactionParameterBuffer tpb = entry.getValue();

            mapper.setMapping(isolation, tpb);
        }

        return mapper;
    }

    /**
     * @deprecated TODO Usage of this method should be removed or revised as current use of default encoding is not correct.
     */
    @Deprecated
    public DatabaseParameterBuffer getDatabaseParameterBuffer() throws SQLException {
        // TODO Instance creation should be done through FbDatabase or database factory?
        DatabaseParameterBuffer dpb = new DatabaseParameterBufferImp(
                DatabaseParameterBufferImp.DpbMetaData.DPB_VERSION_1,
                EncodingFactory.getPlatformEncoding());
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            Object value = entry.getValue();

            Integer dpbType = ParameterBufferHelper.getDpbKey(propertyName);

            if (dpbType == null)
                continue;

            if (value instanceof Boolean) {
                if ((Boolean) value)
                    dpb.addArgument(dpbType);
            } else if (value instanceof Byte) {
                dpb.addArgument(dpbType, new byte[] { (Byte) value });
            } else if (value instanceof Integer) {
                dpb.addArgument(dpbType, (Integer) value);
            } else if (value instanceof String) {
                dpb.addArgument(dpbType, (String) value);
            } else if (value == null)
                dpb.addArgument(dpbType);
        }
        return dpb;
    }

    /**
     * Throws an UnsupportedOperationException
     */
    protected final void immutable() {
        throw new UnsupportedOperationException("this object is immutable");
    }

    protected abstract void dirtied();
}
