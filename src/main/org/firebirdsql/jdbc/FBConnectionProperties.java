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
package org.firebirdsql.jdbc;

import org.firebirdsql.encodings.EncodingDefinition;
import org.firebirdsql.encodings.EncodingFactory;
import org.firebirdsql.gds.*;
import org.firebirdsql.gds.impl.DatabaseParameterBufferImp;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.jca.FBResourceException;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.firebirdsql.gds.ISCConstants.*;
import static org.firebirdsql.jdbc.FBDriverPropertyManager.getCanonicalName;

public class FBConnectionProperties extends AbstractFBConnectionProperties implements FirebirdConnectionProperties, Serializable, Cloneable {
    private static final Logger log = LoggerFactory.getLogger(FBConnectionProperties.class);

    private static final long serialVersionUID = 611228437520889118L;

    private FBImmutableConnectionProperties immutableFBConnectionPropertiesCache;

    /**
     * Default constructor for FBConnectionProperties.
     */
    public FBConnectionProperties() {
        super();
    }

    /**
     * Copy constructor for FBConnectionProperties.
     * <p>
     * All properties defined in {@link org.firebirdsql.jdbc.FirebirdConnectionProperties} are
     * copied from <code>src</code> to the new instance.
     * </p>
     *
     * @param src Source to copy from
     */
    public FBConnectionProperties(AbstractFBConnectionProperties src) {
        super(src);
    }

    private void setIntProperty(String name, int value) {
        if (PORT_PROPERTY.equals(name)) {
            setPort(value);
        }
        if (SOCKET_BUFFER_SIZE_PROPERTY.equals(name)) {
            setSocketBufferSize(value);
        }
        if (SO_TIMEOUT.equals(name)) {
            setSoTimeout(value);
        }
        if (CONNECT_TIMEOUT.equals(name)) {
            setConnectTimeout(value);
        }
        if (CONNECT_DIALECT_PROPERTY.equals(name)) {
            setConnectionDialect((short) value);
        } else {
            properties.put(getCanonicalName(name), value);
        }
    }

    private void setStringProperty(String name, String value) {
        if (DATABASE_PROPERTY.equals(name)) {
            setDatabase(value);
        } else if (SERVER_PROPERTY.equals(name)) {
            setServer(value);
        } else if (TYPE_PROPERTY.equals(name)) {
            setType(value);
        }

        name = getCanonicalName(name);
        Object objValue = ParameterBufferHelper.parseDpbString(name, value);

        properties.put(name, objValue);
    }

    private void setBooleanProperty(String name, boolean value) {
        if (value) {
            properties.put(getCanonicalName(name), Boolean.TRUE);
        } else {
            properties.remove(name);
        }
    }

    public int hashCode() {
        return Objects.hash(type, database);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof FBConnectionProperties)) {
            return false;
        }

        FBConnectionProperties that = (FBConnectionProperties) obj;

        boolean result = this.properties.equals(that.properties);
        result &= this.extraDatabaseParameters.equals(that.extraDatabaseParameters);
        result &= Objects.equals(this.type, that.type);
        result &= Objects.equals(this.database, that.database);
        result &= Objects.equals(this.server, that.server);
        result &= Objects.equals(this.sessionTimeZone, that.sessionTimeZone);
        result &= Objects.equals(this.tpbMapping, that.tpbMapping);
        result &= this.port == that.port;
        result &= this.connectionDialect == that.connectionDialect;
        result &= this.socketBufferSize == that.socketBufferSize;
        result &= this.soTimeout == that.soTimeout;
        result &= this.connectTimeout == that.connectTimeout;
        result &= this.defaultTransactionIsolation == that.defaultTransactionIsolation;
        result &= this.customMapping.equals(that.customMapping);
        // If one or both are null we are identical (see also JDBC-249)
        result &= (this.mapper == null || that.mapper == null) || this.mapper.equals(that.mapper);

        return result;
    }

    public Object clone() {
        try {
            FBConnectionProperties clone = (FBConnectionProperties) super.clone();

            clone.properties = new HashMap<>(properties);
            clone.customMapping = new HashMap<>(customMapping);
            clone.mapper = mapper != null ? (FBTpbMapper) mapper.clone() : null;

            clone.extraDatabaseParameters = extraDatabaseParameters.deepCopy();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new Error("Assertion failure: clone not supported"); // Can't happen
        }
    }

    public void setDatabase(String database) {
        this.database = database;
        dirtied();
    }

    public void setServer(String server) {
        this.server = server;
        dirtied();
    }

    public void setPort(int port) {
        this.port = port;
        dirtied();
    }

    public void setConnectionDialect(short connectionDialect) {
        this.connectionDialect = connectionDialect;
        dirtied();
    }

    public void setType(String type) {
        this.type = type;
        dirtied();
    }

    public void setBlobBufferSize(int bufferSize) {
        setIntProperty(BLOB_BUFFER_SIZE_PROPERTY, bufferSize);
        dirtied();
    }

    public void setCharSet(String charSet) {
        if (charSet == null) {
            return;
        }
        dirtied();
        // Normalize the name of the encoding
        final EncodingDefinition encodingDefinition = EncodingFactory.getPlatformDefault()
                .getEncodingDefinitionByCharsetAlias(charSet);
        if (encodingDefinition == null) {
            setStringProperty(LOCAL_ENCODING_PROPERTY, charSet); // put the raw value
            return;
        }
        setStringProperty(LOCAL_ENCODING_PROPERTY, encodingDefinition.getJavaEncodingName());

        if (getStringProperty(ENCODING_PROPERTY) != null) {
            return;
        }

        String encoding = encodingDefinition.getFirebirdEncodingName();
        if (encoding != null) {
            setStringProperty(ENCODING_PROPERTY, encoding);
        }
    }

    public void setEncoding(String encoding) {
        if (encoding == null) {
            return;
        }
        setStringProperty(ENCODING_PROPERTY, encoding);
        dirtied();
        if (getStringProperty(LOCAL_ENCODING_PROPERTY) != null) {
            return;
        }

        final EncodingDefinition encodingDefinition = EncodingFactory.getPlatformDefault()
                .getEncodingDefinitionByFirebirdName(encoding);
        if (encodingDefinition != null && !encodingDefinition.isInformationOnly()) {
            setStringProperty(LOCAL_ENCODING_PROPERTY, encodingDefinition.getJavaEncodingName());
        }
    }

    public void setRoleName(String roleName) {
        if (roleName != null) {
            setStringProperty(ROLE_NAME_PROPERTY, roleName);
            dirtied();
        }
    }

    public void setSqlDialect(String sqlDialect) {
        if (sqlDialect != null) {
            setStringProperty(SQL_DIALECT_PROPERTY, sqlDialect);
            dirtied();
        }
    }

    public void setUseTranslation(String translationPath) {
        if (translationPath != null) {
            setStringProperty(USE_TRANSLATION_PROPERTY, translationPath);
            dirtied();
        }
    }

    public void setUseStreamBlobs(boolean useStreamBlobs) {
        setBooleanProperty(USE_STREAM_BLOBS_PROPERTY, useStreamBlobs);
        dirtied();
    }

    public boolean isUseStandardUdf() {
        return getBooleanProperty(USE_STANDARD_UDF_PROPERTY);
    }

    public void setUseStandardUdf(boolean useStandardUdf) {
        setBooleanProperty(USE_STANDARD_UDF_PROPERTY, useStandardUdf);
        dirtied();
    }

    public void setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
        dirtied();
    }

    public void setTimestampUsesLocalTimezone(boolean timestampUsesLocalTimezone) {
        setBooleanProperty(TIMESTAMP_USES_LOCAL_TIMEZONE_PROPERTY, timestampUsesLocalTimezone);
        dirtied();
    }

    public void setUserName(String userName) {
        setStringProperty(USER_NAME_PROPERTY, userName);
        dirtied();
    }

    public void setPassword(String password) {
        setStringProperty(PASSWORD_PROPERTY, password);
        dirtied();
    }

    public void setBuffersNumber(int buffersNumber) {
        setIntProperty(BUFFERS_NUMBER_PROPERTY, buffersNumber);
        dirtied();
    }

    public void setNonStandardProperty(String key, String value) {
        if (ISOLATION_PROPERTY.equals(key) || DEFAULT_ISOLATION_PROPERTY.equals(key)) {
            setDefaultIsolation(value);
        } else {
            setStringProperty(key, value);
        }
        dirtied();
    }

    public void setDefaultResultSetHoldable(boolean isHoldable) {
        setBooleanProperty(DEFAULT_HOLDABLE_RS_PROPERTY, isHoldable);
        dirtied();
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        dirtied();
    }

    public void setUseGSSAuth(boolean useGSSAuth) {
        setBooleanProperty(USE_GSS_AUTH, useGSSAuth);
        dirtied();
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        dirtied();
    }

    @Override
    public void setUseFirebirdAutocommit(boolean useFirebirdAutocommit) {
        setBooleanProperty(USE_FIREBIRD_AUTOCOMMIT, useFirebirdAutocommit);
        dirtied();
    }

    @Override
    public void setWireCrypt(String wireCrypt) {
        setStringProperty(WIRE_CRYPT_LEVEL, wireCrypt);
        dirtied();
    }

    @Override
    public void setDbCryptConfig(String dbCryptConfig) {
        setStringProperty(DB_CRYPT_CONFIG, dbCryptConfig);
        dirtied();
    }

    @Override
    public void setIgnoreProcedureType(boolean ignoreProcedureType) {
        setBooleanProperty(IGNORE_PROCEDURE_TYPE, ignoreProcedureType);
        dirtied();
    }

    @Override
    public void setCertificate(String certificate) {
        setStringProperty(CERTIFICATE, certificate);
        dirtied();
    }

    @Override
    public void setRepositoryPin(String pin) {
        setStringProperty(REPOSITORY_PIN, pin);
        dirtied();
    }

    @Override
    public void setVerifyServerCertificate(boolean verify) {
        setBooleanProperty(SERVER_CERTIFICATE, verify);
        dirtied();
    }

    public void setNonStandardProperty(String propertyMapping) {
        char[] chars = propertyMapping.toCharArray();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        boolean keyProcessed = false;
        for (char ch : chars) {
            boolean isSeparator = Character.isWhitespace(ch) || ch == '=' || ch == ':';

            // if no key was processed, ignore white spaces
            if (key.length() == 0 && isSeparator)
                continue;

            if (!keyProcessed && !isSeparator) {
                key.append(ch);
            } else if (!keyProcessed) {
                keyProcessed = true;
            } else if (value.length() != 0 || !isSeparator) {
                value.append(ch);
            }
        }

        String keyStr = key.toString().trim();
        String valueStr = value.length() > 0 ? value.toString().trim() : null;

        setNonStandardProperty(keyStr, valueStr);
        dirtied();
    }

    public void setTpbMapping(String tpbMapping) {
        if (mapper != null) {
            throw new IllegalStateException("Properties are already initialized.");
        }
        this.tpbMapping = tpbMapping;
        dirtied();
    }

    public void setDefaultTransactionIsolation(int defaultIsolationLevel) {
        defaultTransactionIsolation = defaultIsolationLevel;
        if (mapper != null) {
            mapper.setDefaultTransactionIsolation(defaultIsolationLevel);
        }
        dirtied();
    }

    public void setDefaultIsolation(String isolation) {
        setDefaultTransactionIsolation(FBTpbMapper.getTransactionIsolationLevel(isolation));
        dirtied();
    }

    public void setTransactionParameters(int isolation, TransactionParameterBuffer tpb) {
        customMapping.put(isolation, tpb);
        if (mapper != null) {
            mapper.setMapping(isolation, tpb);
        }
        dirtied();
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        dirtied();
    }

    public void setCertificateBase64(String certificateBase64) {
        setStringProperty(CERTIFICATE_BASE64, certificateBase64);
        dirtied();
    }

    public void setEffectiveLogin(String effectiveLogin) {
        setStringProperty(EFFECTIVE_LOGIN, effectiveLogin);
        dirtied();
    }

    public void setExcludeCryptoPlugins(String excludeCryptoPlugins) {
        setStringProperty(EXCLUDE_CRYPTOPLUGINS, excludeCryptoPlugins);
        dirtied();
    }

    public void setNotEncryptedPassword(final boolean notEncryptPassword) {
        setBooleanProperty(NOT_ENCRYPTED_PASSWORD, notEncryptPassword);
        dirtied();
    }

    public void setProviderID(int providerID) {
        setIntProperty(PROVIDER_ID, providerID);
        dirtied();
    }

    public void setPageCacheSize(int pageCacheSize) {
        setIntProperty(PAGE_CACHE_SIZE_PROPERTY, pageCacheSize);
        dirtied();
    }

    public void setResultSetDefaultHoldable(boolean holdable) {
        setBooleanProperty(RESULTSET_HOLDABLE_PROPERTY, holdable);
        dirtied();
    }

    public void setColumnLabelForName(boolean columnLabelForName) {
        setBooleanProperty(COLUMN_LABEL_PROPERTY, columnLabelForName);
        dirtied();
    }

    public FirebirdConnectionProperties asImmutable() {
        if (immutableFBConnectionPropertiesCache == null) {
            immutableFBConnectionPropertiesCache = new FBImmutableConnectionProperties(this);
        }
        return immutableFBConnectionPropertiesCache;
    }

    public FirebirdConnectionProperties asNewMutable() {
        return new FBConnectionProperties(this);
    }

    /**
     * Method to populate an FbConnectionProperties from a database parameter buffer.
     * <p>
     * Unsupported or unknown properties are ignored.
     * </p>
     *
     * @param dpb Database parameter buffer
     * @deprecated TODO: This method is only intended to simplify migration of the protocol implementation and needs to be removed.
     */
    @Deprecated
    public void fromDpb(DatabaseParameterBuffer dpb) throws SQLException {
        for (Parameter parameter : dpb) {
            final int parameterType = parameter.getType();
            switch (parameterType) {
                case isc_dpb_user_name:
                    setUserName(parameter.getValueAsString());
                    break;
                case isc_dpb_password:
                    setPassword(parameter.getValueAsString());
                    break;
                case isc_dpb_sql_role_name:
                    setRoleName(parameter.getValueAsString());
                    break;
                case isc_dpb_lc_ctype:
                    setEncoding(parameter.getValueAsString());
                    break;
                case isc_dpb_local_encoding:
                    setCharSet(parameter.getValueAsString());
                    break;
                case isc_dpb_sql_dialect:
                    setConnectionDialect((short) parameter.getValueAsInt());
                    break;
                case isc_dpb_num_buffers:
                    setPageCacheSize(parameter.getValueAsInt());
                    break;
                case isc_dpb_connect_timeout:
                    setConnectTimeout(parameter.getValueAsInt());
                    break;
                case isc_dpb_so_timeout:
                    setSoTimeout(parameter.getValueAsInt());
                    break;
                case isc_dpb_socket_buffer_size:
                    setSocketBufferSize(parameter.getValueAsInt());
                    break;
                case isc_dpb_result_set_holdable:
                    setResultSetDefaultHoldable(true);
                    break;
                case isc_dpb_column_label_for_name:
                    setColumnLabelForName(true);
                    break;
                case isc_dpb_wire_crypt_level:
                    String propertyValue = parameter.getValueAsString();
                    try {
                        setWireCrypt(propertyValue);
                    } catch (IllegalArgumentException e) {
                        throw FbExceptionBuilder.forException(JaybirdErrorCodes.jb_invalidConnectionPropertyValue)
                                .messageParameter(propertyValue)
                                .messageParameter("wireCrypt")
                                .toFlatSQLException();
                    }
                    break;
                case isc_dpb_db_crypt_config:
                    setDbCryptConfig(parameter.getValueAsString());
                    break;
                case isc_dpb_exclude_crypto_plugins:
                    setExcludeCryptoPlugins(parameter.getValueAsString());
                    break;
                case isc_dpb_utf8_filename:
                    // Filter out, handled explicitly in protocol implementation
                    break;
                case isc_dpb_specific_auth_data:
                    break;
                case isc_dpb_process_id:
                case isc_dpb_process_name:
                case isc_dpb_set_bind:
                case isc_dpb_decfloat_round:
                case isc_dpb_decfloat_traps:
                    parameter.copyTo(extraDatabaseParameters, null);
                    dirtied();
                    break;
                case isc_dpb_gss:
                    break;
                case isc_dpb_certificate:
                    setCertificate(parameter.getValueAsString());
                    break;
                case isc_dpb_certificate_base64:
                    setCertificateBase64(parameter.getValueAsString());
                    break;
                case isc_dpb_repository_pin:
                    setRepositoryPin(parameter.getValueAsString());
                    break;
                case isc_dpb_effective_login:
                    setEffectiveLogin(parameter.getValueAsString());
                    break;
                case isc_dpb_verify_server:
                    parameter.copyTo(getExtraDatabaseParameters(), null);
                    setVerifyServerCertificate(true);
                    break;
                case isc_dpb_trusted_auth:
                case isc_dpb_multi_factor_auth:
                    parameter.copyTo(getExtraDatabaseParameters(), null);
                    break;
                default:
                    if (parameterType < jaybirdMinIscDpbValue || parameterType > jaybirdMaxIscDpbValue) {
                        log.warn(String.format(
                                "Unknown or unsupported parameter with type %d added to extra database parameters",
                                parameterType));
                    }
                    parameter.copyTo(extraDatabaseParameters, null);
                    dirtied();
                    break;
            }
        }
    }

    protected void dirtied() {
        immutableFBConnectionPropertiesCache = null;
    }
}
