package org.firebirdsql.jaybird.xca;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;

import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jaybird.props.def.ConnectionProperty;
import org.firebirdsql.jdbc.FBDriverNotCapableException;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

public class FBSADataSource implements DataSource, Serializable, Referenceable, FirebirdConnectionProperties, XcaConnectionEventListener {
    
    transient protected FBManagedConnectionFactory mcf;
    transient protected PrintWriter log;
    transient protected FBManagedConnection mc;
    
    protected Reference jndiReference;
    protected String description;
    protected int loginTimeout;
    private List<FBManagedConnection> connections = new ArrayList<FBManagedConnection>();

    /**
     * Create instance of this class.
     */
    public FBSADataSource() {
        this(GDSFactory.getDefaultGDSType());
    }

    /**
     * Create instance of this class.
     */
    public FBSADataSource(GDSType type) {
        mcf = new FBManagedConnectionFactory(type);
    }
    
    /**
     * Get buffer length for the BLOB fields. 
     * 
     * @return length of BLOB buffer.
     */
    public Integer getBlobBufferLength() {
        return mcf.getBlobBufferSize();
    }
    
    /**
     * Set BLOB buffer length. This value influences the performance when 
     * working with BLOB fields.
     * 
     * @param length new length of the BLOB buffer.
     */
    public void setBlobBufferLength(Integer length) {
        mcf.setBlobBufferSize(length.intValue());
    }
    
    
    
    /**
     * Get name of the database. 
     * 
     * @return database name, value is equal to the part of full JDBC URL without
     * the <code>jdbc:firebirdsql:</code> part.
     */
    public String getDatabaseName() {
        return mcf.getDatabaseName();
    }
    
    /**
     * Set database name.
     * 
     * @param name connection URL without <code>"jdbc:firebirdsql:"</code>
     * prefix (<code>"//localhost:3050/c:/database/employee.gdb"</code>) for
     * example).
     */
    public void setDatabaseName(String name) {
        mcf.setDatabaseName(name);
    }

    /**
     * Get user name that is used in {@link #getConnection()} method.
     * 
     * @return default user name.
     * 
     * @deprecated use {@link #getUserName()} instead for the sake of naming
     * compatibility.
     */
    public String getUser() {
        return getUserName();
    }
    
    /**
     * Set user name that will be used in {@link #getConnection()} method.
     * 
     * @param user default user name.
     * 
     * @deprecated use {@link #setUserName(String)} instead for the sake of
     * naming compatibility.
     */
    public void setUser(String user) {
        setUserName(user);
    }
    
    /**
     * Get user name that is used in {@link #getConnection()} method.
     * 
     * @return default user name.
     */
    public String getUserName() {
        return mcf.getUserName();
    }
    
    /**
     * Set user name that will be used in {@link #getConnection()} method.
     * 
     * @param userName default user name.
     */
    public void setUserName(String userName) {
        mcf.setUserName(userName);
    }
    
    /**
     * Get password used in {@link #getConnection()} method.
     * 
     * @return password corresponding to the user name returned by 
     * {@link #getUserName()}.
     */
    public String getPassword() {
        return mcf.getPassword();
    }
    
    /**
     * Set password that will be used in the {@link #getConnection()} method.
     * 
     * @param password password corresponding to the user name set in 
     * {@link #setUserName(String)}.
     */
    public void setPassword(String password) {
        mcf.setPassword(password);
    }
    
    /**
     * Get encoding for connections produced by this data source.
     * 
     * @return encoding for the connection.
     */
    public String getEncoding() {
        return mcf.getEncoding();
    }
    
    /**
     * Set encoding for connections produced by this data source.
     * 
     * @param encoding encoding for the connection.
     */
    public void setEncoding(String encoding) {
        mcf.setEncoding(encoding);
    }
    
    public String getTpbMapping() {
        return mcf.getTpbMapping();
    }
    
    public void setTpbMapping(String tpbMapping) {
        mcf.setTpbMapping(tpbMapping);
    }
    
    public int getBlobBufferSize() {
        return mcf.getBlobBufferSize();
    }

    public String getCharSet() {
        return mcf.getCharSet();
    }

    public String getDefaultIsolation() {
        return mcf.getDefaultIsolation();
    }

    public int getDefaultTransactionIsolation() {
        return mcf.getDefaultTransactionIsolation();
    }

    public String getRoleName() {
        return mcf.getRoleName();
    }

    public int getSocketBufferSize() {
        return mcf.getSocketBufferSize();
    }

    public int getSqlDialect() {
        return mcf.getSqlDialect();
    }

    public TransactionParameterBuffer getTransactionParameters(int isolation) {
        return mcf.getTransactionParameters(isolation);
    }

    public String getType() {
        return mcf.getType();
    }

    public boolean isUseStreamBlobs() {
        return mcf.isUseStreamBlobs();
    }

    public void setBlobBufferSize(int bufferSize) {
        mcf.setBlobBufferSize(bufferSize);
    }

    public void setCharSet(String charSet) {
        mcf.setCharSet(charSet);
    }

    public void setDefaultIsolation(String isolation) {
        mcf.setDefaultIsolation(isolation);
    }

    public void setDefaultTransactionIsolation(int defaultIsolationLevel) {
        mcf.setDefaultTransactionIsolation(defaultIsolationLevel);
    }

    public void setNonStandardProperty(String propertyMapping) {
        mcf.setNonStandardProperty(propertyMapping);
    }

    public void setRoleName(String roleName) {
        mcf.setRoleName(roleName);
    }

    public void setSocketBufferSize(int socketBufferSize) {
        mcf.setSocketBufferSize(socketBufferSize);
    }

    public void setSqlDialect(int sqlDialect) {
        mcf.setSqlDialect(sqlDialect);
    }

    public void setTransactionParameters(int isolation, TransactionParameterBuffer tpb) {
        mcf.setTransactionParameters(isolation, tpb);
    }

    public void setType(String type) {
        mcf.setType(type);
    }

    public void setUseStreamBlobs(boolean useStreamBlobs) {
        mcf.setUseStreamBlobs(useStreamBlobs);
    }

    public boolean isDefaultResultSetHoldable() {
        return mcf.isDefaultResultSetHoldable();
    }

    public void setDefaultResultSetHoldable(boolean isHoldable) {
        mcf.setDefaultResultSetHoldable(isHoldable);
    }    

    
    /*
     * INTERFACES IMPLEMENTATION
     */

    /**
     * Get previously set JNDI reference.
     * 
     * @return instance of {@link Reference} set previously.
     * 
     * @throws NamingException if something went wrong.
     */
    public Reference getReference() throws NamingException {
        return jndiReference;
    }

    /**
     * Set JNDI reference for this data source.
     * 
     * @param reference reference to set.
     */
    public void setReference(Reference reference) {
        jndiReference = reference;
    }

    /**
     * Get JDBC connection with default credentials.
     * 
     * @return new JDBC connection.
     * 
     * @throws SQLException if something went wrong.
     */
    public Connection getConnection() throws SQLException {
        FBConnectionRequestInfo subjectCri = mcf.getDefaultConnectionRequestInfo();
        FBManagedConnection mc = getManagedConnection(subjectCri).forkManagedConnection();
        mc.setManagedEnvironment(false);
        mc.addConnectionEventListener(this);
        Connection con = mc.getConnection();
        connections.add(mc);
        return con;
    }

    /**
     * Get JDBC connection with the specified credentials.
     * 
     * @param username user name for the connection.
     * @param password password for the connection.
     * 
     * @return new JDBC connection.
     * 
     * @throws SQLException if something went wrong.
     */
    public Connection getConnection(String username, String password) throws SQLException {
    	throw new FBDriverNotCapableException();
    }

    /**
     * Get log for this datasource.
     * 
     * @return log associated with this datasource.
     *
     * @throws SQLException if something went wrong.
     */
    public PrintWriter getLogWriter() throws SQLException {
        return log;
    }

    /**
     * Set log for this datasource.
     * 
     * @param log instance of {@link PrintWriter} that should be associated 
     * with this datasource.
     * 
     * @throws SQLException if something went wrong.
     */
    public void setLogWriter(PrintWriter log) throws SQLException {
        this.log = log;
    }

    /**
     * Get login timeout specified for this datasource.
     * 
     * @return login timeout of this datasource in seconds.
     * 
     * @throws SQLException if something went wrong.
     */
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public ConnectionBuilder createConnectionBuilder() throws SQLException {
        return null;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new FBDriverNotCapableException();
    }

    @Override
    public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return null;
    }

    /**
     * Set login timeout for this datasource.
     * 
     * @param loginTimeout login timeout in seconds.
     * @throws SQLException
     */
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        this.loginTimeout = loginTimeout;
    }
    
    /**
     * Get description of this datasource.
     * 
     * @return description of this datasource.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set description of this datasource.
     * 
     * @param description description of this datasource.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    

    /**
     * Frees phisical connection
     * @throws SQLException
     */
    public synchronized void close() throws SQLException
    {
    	List<FBManagedConnection> connections = new ArrayList<FBManagedConnection>(this.connections);
    	for(FBManagedConnection mc1:connections)
    	{
        try {
    		  mc1.cleanup();
        } catch (Exception ignored) {
          // ignored
        }
    	}
    	this.connections.clear();
        if (mc != null)
        {
            // Clean up method should cause the database to detach,
            // but if this does not happen, then do it manually
            if (mc.getGDSHelper().getCurrentDatabase().isAttached())
                mc.destroy();
            mc = null;
        }
    }
    
    /**
     * 
     * Allocate FBManagedConnection with phisical connection to database if needs.
     * 
     * @return
     * @throws SQLException
     */
    protected synchronized FBManagedConnection getManagedConnection(FBConnectionRequestInfo cxRequestInfo) throws SQLException {
        if (mc != null)
            return mc;
            
        if (mcf.getDatabaseName() == null || mcf.getDatabaseName().trim().isEmpty())
            throw new SQLException(
                "Database was not specified. Cannot provide connections.");

        mc = (FBManagedConnection)mcf.createManagedConnection(cxRequestInfo);
        mc.setManagedEnvironment(false);

        return mc;
    }
    
    // JDBC 4.0
    
    public boolean isWrapperFor(Class iface) throws SQLException {
    	return false;
    }
    
    public Object unwrap(Class iface) throws SQLException {
    	throw new FBDriverNotCapableException();
    }

    /**
     * callback for
     * when a <code>ManagedConnection</code> is closed.
     *
     * @param ce contains information about the connection that has be closed
     */
    public void connectionClosed(XcaConnectionEvent ce) {
        try {
            ((FBManagedConnection)ce.getSource()).destroy();
            connections.remove((FBManagedConnection)ce.getSource());
        }
        catch (SQLException e) {
            if (log != null) log.println("Exception closing unmanaged connection: " + e);
        }

    }

    /**
     * callback for
     * when a Local Transaction was rolled back within the context of a
     * <code>ManagedConnection</code>.
     *
     * @param ce contains information about the connection 
     */
    public void connectionErrorOccurred(XcaConnectionEvent ce) {
        try {
            ((FBManagedConnection)ce.getSource()).destroy();
            connections.remove((FBManagedConnection)ce.getSource());
        }
        catch (SQLException e) {
            if (log != null) log.println("Exception closing unmanaged connection: " + e);
        }
    }

    //We are only supposed to be notified of local transactions that a Connection started.
    //Not much we can do with this info...
    
    /**
     * Ignored event callback
     */
    public void localTransactionStarted(ConnectionEvent event) {}

    /**
     * Ignored event callback
     */
    public void localTransactionCommitted(ConnectionEvent event) {}

    /**
     * Ignored event callback
     */
    public void localTransactionRolledback(ConnectionEvent event) {}

    public void setSoTimeout(int soTimeout) {
        mcf.setSoTimeout(soTimeout);
    }

    @Override
    public int getConnectTimeout() {
        return mcf.getConnectTimeout();
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        mcf.setConnectTimeout(connectTimeout);
    }

    @Override
    public boolean isUseFirebirdAutocommit() {
        return mcf.isUseFirebirdAutocommit();
    }

    @Override
    public void setUseFirebirdAutocommit(boolean useFirebirdAutocommit) {
        mcf.setUseFirebirdAutocommit(useFirebirdAutocommit);
    }

    @Override
    public boolean isGSSAuthentication() {
        return mcf.isGSSAuthentication();
    }

    @Override
    public void setGSSAuthentication(boolean useGSSAuth) {
        mcf.setGSSAuthentication(useGSSAuth);
    }

    @Override
    public String getWireCrypt() {
        return mcf.getWireCrypt();
    }

    @Override
    public void setWireCrypt(String wireCrypt) {
        mcf.setWireCrypt(wireCrypt);
    }

    @Override
    public String getDbCryptConfig() {
        return mcf.getDbCryptConfig();
    }

    @Override
    public void setDbCryptConfig(String dbCryptConfig) {
        mcf.setDbCryptConfig(dbCryptConfig);
    }

    @Override
    public String getAuthPlugins() {
        return mcf.getAuthPlugins();
    }

    @Override
    public void setAuthPlugins(String authPlugins) {
        mcf.setAuthPlugins(authPlugins);
    }

    @Override
    public String getGeneratedKeysEnabled() {
        return mcf.getGeneratedKeysEnabled();
    }

    @Override
    public void setGeneratedKeysEnabled(String generatedKeysEnabled) {
        mcf.setGeneratedKeysEnabled(generatedKeysEnabled);
    }

    @Override
    public String getDataTypeBind() {
        return mcf.getDataTypeBind();
    }

    @Override
    public void setDataTypeBind(String dataTypeBind) {
        mcf.setDataTypeBind(dataTypeBind);
    }

    @Override
    public String getSessionTimeZone() {
        return mcf.getSessionTimeZone();
    }

    @Override
    public void setSessionTimeZone(String sessionTimeZone) {
        mcf.setSessionTimeZone(sessionTimeZone);
    }

    @Override
    public boolean isIgnoreProcedureType() {
        return mcf.isIgnoreProcedureType();
    }

    @Override
    public void setIgnoreProcedureType(boolean ignoreProcedureType) {
        mcf.setIgnoreProcedureType(ignoreProcedureType);
    }

    @Override
    public boolean isWireCompression() {
        return mcf.isWireCompression();
    }

    @Override
    public void setWireCompression(boolean wireCompression) {
        mcf.setWireCompression(wireCompression);
    }

    @Override
    public String getCertificate() {
        return mcf.getCertificate();
    }

    @Override
    public void setCertificate(String certificate) {
        mcf.setCertificate(certificate);
    }

    @Override
    public String getRepositoryPin() {
        return mcf.getRepositoryPin();
    }

    @Override
    public void setRepositoryPin(String pin) {
        mcf.setRepositoryPin(pin);
    }

    @Override
    public boolean isVerifyServerCertificate() {
        return mcf.isVerifyServerCertificate();
    }

    @Override
    public void setVerifyServerCertificate(boolean verify) {
        mcf.setVerifyServerCertificate(verify);
    }

    public int getSoTimeout() {
        return mcf.getSoTimeout();
    }

    @Override
    public String getProperty(String name) {
        return mcf.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value) {
        mcf.setProperty(name, value);
    }

    @Override
    public Integer getIntProperty(String name) {
        return mcf.getIntProperty(name);
    }

    @Override
    public void setIntProperty(String name, Integer value) {
        mcf.setIntProperty(name, value);
    }

    @Override
    public Boolean getBooleanProperty(String name) {
        return mcf.getBooleanProperty(name);
    }

    @Override
    public void setBooleanProperty(String name, Boolean value) {
        mcf.setBooleanProperty(name, value);
    }

    @Override
    public Map<ConnectionProperty, Object> connectionPropertyValues() {
        return mcf.connectionPropertyValues();
    }
}
