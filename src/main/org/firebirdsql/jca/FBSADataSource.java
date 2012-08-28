package org.firebirdsql.jca;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.sql.DataSource;

import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.AbstractGDS;
import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jca.FBConnectionRequestInfo;
import org.firebirdsql.jca.FBManagedConnection;
import org.firebirdsql.jca.FBManagedConnectionFactory;
import org.firebirdsql.jdbc.FBDriverNotCapableException;
import org.firebirdsql.jdbc.FBSQLException;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;

public class FBSADataSource implements DataSource, Serializable, Referenceable, FirebirdConnectionProperties, ConnectionEventListener {
    
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
        this(((AbstractGDS)GDSFactory.getDefaultGDS()).getType());
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
        return new Integer(mcf.getBlobBufferSize());
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
     * 
     * @deprecated use {@link #getDatabase} instead for the sake of naming
     * compatibility.
     */
    public String getDatabaseName() {
        return getDatabase();
    }
    
    /**
     * Set database name.
     * 
     * @param name connection URL without <code>"jdbc:firebirdsql:"</code>
     * prefix (<code>"//localhost:3050/c:/database/employee.gdb"</code>) for
     * example).
     * 
     * @deprecated use {@link #setDatabase(String)} instead for the sake of 
     * naming compatibility.
     */
    public void setDatabaseName(String name) {
        setDatabase(name);
    }

    /**
     * Get name of the database. 
     * 
     * @return database name, value is equal to the part of full JDBC URL without
     * the <code>jdbc:firebirdsql:</code> part.
     */
    public String getDatabase() {
        return mcf.getDatabase();
    }

    /**
     * Set database name.
     * 
     * @param name connection URL without <code>"jdbc:firebirdsql:"</code>
     * prefix (<code>"//localhost:3050/c:/database/employee.gdb"</code>) for
     * example).
     */
    public void setDatabase(String name) {
        mcf.setDatabase(name);
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
     * Get password used in {@link #getConnection()} method.
     *
     * @return password corresponding to the user name returned by
     * {@link #getUserName()}.
     */
    public String getPasswordSha() {
        return mcf.getPasswordSha();
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
     * Set password that will be used in the {@link #getConnection()} method.
     *
     * @param password password corresponding to the user name set in
     * {@link #setUserName(String)}.
     */
    public void setPasswordSha(String password) {
        mcf.setPasswordSha(password);
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

    public int getBuffersNumber() {
        return mcf.getBuffersNumber();
    }

    public String getCharSet() {
        return mcf.getCharSet();
    }

    public DatabaseParameterBuffer getDatabaseParameterBuffer() throws SQLException {
        return mcf.getDatabaseParameterBuffer();
    }

    public String getDefaultIsolation() {
        return mcf.getDefaultIsolation();
    }

    public int getDefaultTransactionIsolation() {
        return mcf.getDefaultTransactionIsolation();
    }

    public String getNonStandardProperty(String key) {
        return mcf.getNonStandardProperty(key);
    }

    public String getRoleName() {
        return mcf.getRoleName();
    }

    public int getSocketBufferSize() {
        return mcf.getSocketBufferSize();
    }

    public String getSqlDialect() {
        return mcf.getSqlDialect();
    }

    public TransactionParameterBuffer getTransactionParameters(int isolation) {
        return mcf.getTransactionParameters(isolation);
    }

    public String getType() {
        return mcf.getType();
    }

    public String getUseTranslation() {
        return mcf.getUseTranslation();
    }

    public boolean isTimestampUsesLocalTimezone() {
        return mcf.isTimestampUsesLocalTimezone();
    }

    public boolean isUseStandardUdf() {
        return mcf.isUseStandardUdf();
    }

    public boolean isUseStreamBlobs() {
        return mcf.isUseStreamBlobs();
    }

    public void setBlobBufferSize(int bufferSize) {
        mcf.setBlobBufferSize(bufferSize);
    }

    public void setBuffersNumber(int buffersNumber) {
        mcf.setBuffersNumber(buffersNumber);
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

    public void setNonStandardProperty(String key, String value) {
        mcf.setNonStandardProperty(key, value);
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

    public void setSqlDialect(String sqlDialect) {
        mcf.setSqlDialect(sqlDialect);
    }

    public void setTimestampUsesLocalTimezone(boolean timestampUsesLocalTimezone) {
        mcf.setTimestampUsesLocalTimezone(timestampUsesLocalTimezone);
    }

    public void setTransactionParameters(int isolation, TransactionParameterBuffer tpb) {
        mcf.setTransactionParameters(isolation, tpb);
    }

    public void setType(String type) {
        mcf.setType(type);
    }

    public void setUseStandardUdf(boolean useStandardUdf) {
        mcf.setUseStandardUdf(useStandardUdf);
    }

    public void setUseStreamBlobs(boolean useStreamBlobs) {
        mcf.setUseStreamBlobs(useStreamBlobs);
    }

    public void setUseTranslation(String translationPath) {
        mcf.setUseTranslation(translationPath);
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
      try {
        FBConnectionRequestInfo subjectCri = mcf.getDefaultConnectionRequestInfo();
        String shaPassword = mcf.getPasswordSha();
        if (shaPassword != null)
          subjectCri.setPasswordSha(shaPassword);
        FBManagedConnection mc = getManagedConnection(subjectCri).forkManagedConnection();
        mc.setManagedEnvironment(false);
        mc.setConnectionSharing(false);
        mc.addConnectionEventListener(this);
        Connection con = (Connection) mc.getConnection(null, subjectCri);
        connections.add(mc);
        return con;
      }
      catch (ResourceException ex) {
        throw new FBSQLException(ex);
      }
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
     * @throws ResourceException
     */
    public synchronized void close()throws ResourceException 
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
            
        if (mcf.getDatabase() == null || "".equals(mcf.getDatabase().trim()))
            throw new SQLException(
                "Database was not specified. Cannot provide connections.");
                
        try {
        	mc = (FBManagedConnection)mcf.createManagedConnection(null, cxRequestInfo);
			mc.setManagedEnvironment(false);
			mc.setConnectionSharing(false);

			return mc;
        } catch(ResourceException rex) {
            throw new FBSQLException(rex);
        }
    }
    
    // JDBC 4.0
    
    public boolean isWrapperFor(Class iface) throws SQLException {
    	return false;
    }
    
    public Object unwrap(Class iface) throws SQLException {
    	throw new FBDriverNotCapableException();
    }

    //javax.resource.spi.ConnectionEventListener implementation

    /**
     * <code>javax.resource.spi.ConnectionEventListener</code> callback for 
     * when a <code>ManagedConnection</code> is closed.
     *
     * @param ce contains information about the connection that has be closed
     */
    public void connectionClosed(ConnectionEvent ce) {
        PrintWriter externalLog = ((FBManagedConnection)ce.getSource()).getLogWriter();
        try {
            ((FBManagedConnection)ce.getSource()).destroy();
            connections.remove((FBManagedConnection)ce.getSource());
        }
        catch (ResourceException e) {
            if (externalLog != null) externalLog.println("Exception closing unmanaged connection: " + e);
        }

    }

    /**
     * <code>javax.resource.spi.ConnectionEventListener</code> callback for 
     * when a Local Transaction was rolled back within the context of a
     * <code>ManagedConnection</code>.
     *
     * @param ce contains information about the connection 
     */
    public void connectionErrorOccurred(ConnectionEvent ce) {
        PrintWriter externalLog = ((FBManagedConnection)ce.getSource()).getLogWriter();
        try {
            ((FBManagedConnection)ce.getSource()).destroy();
            connections.remove((FBManagedConnection)ce.getSource());
        }
        catch (ResourceException e) {
            if (externalLog != null) externalLog.println("Exception closing unmanaged connection: " + e);
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
    
    public int getSoTimeout() {
        return mcf.getSoTimeout();
    }
}
