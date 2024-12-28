package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.impl.BaseGDSFactoryPlugin;
import org.firebirdsql.gds.ng.nativeoo.FbOOClientDatabaseFactory;

import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;

/**
 * GDS factory plugin implementation for native OO API
 *
 * @since 4.0
 */
public final class FbOONativeGDSFactoryPlugin extends BaseGDSFactoryPlugin {

    public static final String NATIVE_TYPE_NAME = "FBOONATIVE";
    private static final List<String> TYPE_ALIASES = List.of("TYPE2", "LOCAL");
    private static final String DEFAULT_PROTOCOL = "jdbc:firebirdsql:fboo:native:";
    private static final List<String> JDBC_PROTOCOLS = List.of(DEFAULT_PROTOCOL, "jdbc:firebird:fboo:native:");

    @Override
    public String getPluginName() {
        return "JNA-based GDS implementation via OO API.";
    }

    @Override
    public String getTypeName() {
        return NATIVE_TYPE_NAME;
    }

    @Override
    public List<String> getTypeAliasList() {
        return TYPE_ALIASES;
    }

    @Override
    public List<String> getSupportedProtocolList() {
        return JDBC_PROTOCOLS;
    }

    @Override
    public String getDefaultProtocol() {
        return DEFAULT_PROTOCOL;
    }

    @Override
    public String getDatabasePath(String server, Integer port, String path) throws SQLException {
        requirePath(path);
        if (server == null) {
            return path;
        }

        var sb = new StringBuilder();
        sb.append(server);
        if (port != null) {
            sb.append('/').append(port.intValue());
        }
        sb.append(':').append(path);

        return sb.toString();
    }

    @Override
    public FbOOClientDatabaseFactory getDatabaseFactory() {
        return FbOOClientDatabaseFactory.getInstance();
    }
}
