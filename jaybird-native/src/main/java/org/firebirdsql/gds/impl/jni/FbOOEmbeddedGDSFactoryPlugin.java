package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.impl.BaseGDSFactoryPlugin;
import org.firebirdsql.gds.ng.nativeoo.FbOOEmbeddedDatabaseFactory;

import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;

/**
 * GDS factory plugin implementation for embedded OO API
 *
 * @since 4.0
 */
public class FbOOEmbeddedGDSFactoryPlugin extends BaseGDSFactoryPlugin {

    public static final String EMBEDDED_TYPE_NAME = "FBOOEMBEDDED";
    private static final String DEFAULT_PROTOCOL = "jdbc:firebirdsql:fboo:embedded:";
    private static final List<String> JDBC_PROTOCOLS = List.of(DEFAULT_PROTOCOL, "jdbc:firebird:fboo:embedded:");

    public String getPluginName() {
        return "GDS implementation for embedded server via OO API.";
    }

    public String getTypeName() {
        return EMBEDDED_TYPE_NAME;
    }

    @Override
    public List<String> getTypeAliasList() {
        return List.of();
    }

    @Override
    public List<String> getSupportedProtocolList() {
        return JDBC_PROTOCOLS;
    }

    @Override
    public String getDefaultProtocol() {
        return DEFAULT_PROTOCOL;
    }

    public String getDatabasePath(String server, Integer port, String path) throws SQLException {
        requirePath(path);
        return path;
    }

    @Override
    public FbOOEmbeddedDatabaseFactory getDatabaseFactory() {
        return FbOOEmbeddedDatabaseFactory.getInstance();
    }
}
