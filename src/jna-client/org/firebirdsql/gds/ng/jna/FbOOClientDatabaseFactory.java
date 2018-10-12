package org.firebirdsql.gds.ng.jna;

import com.sun.jna.Native;
import org.firebirdsql.gds.JaybirdSystemProperties;
import org.firebirdsql.gds.ng.FbDatabase;
import org.firebirdsql.gds.ng.FbService;
import org.firebirdsql.gds.ng.IConnectionProperties;
import org.firebirdsql.gds.ng.IServiceProperties;
import org.firebirdsql.gds.ng.jna.interfaces.IServiceConnectionImpl;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface;

import java.sql.SQLException;

public class FbOOClientDatabaseFactory extends AbstractNativeDatabaseFactory {
    private static final FbOOClientDatabaseFactory INSTANCE = new FbOOClientDatabaseFactory();

    @Override
    public FbDatabase connect(IConnectionProperties connectionProperties) throws SQLException {
        final NativeDatabaseConnection databaseConnection = new NativeDatabaseConnection(getClientLibrary(),
                filterProperties(connectionProperties));
        return databaseConnection.identify();
    }

    @Override
    public FbService serviceConnect(IServiceProperties serviceProperties) throws SQLException {
        final IServiceConnectionImpl serviceConnection = new IServiceConnectionImpl(getClientLibrary(),
                filterProperties(serviceProperties));
        return serviceConnection.identify();
    }

    @Override
    protected FbClientLibrary getClientLibrary() {
        return FbOOClientDatabaseFactory.ClientHolder.clientLibrary;
    }

    public static FbOOClientDatabaseFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Initialization-on-demand depending on classloading behavior specified in JLS 12.4
     */
    private static final class ClientHolder {

        private static final FbClientLibrary clientLibrary = syncWrapIfNecessary(initClientLibrary());

        private static FbClientLibrary initClientLibrary() {
            return Native.loadLibrary("fbclient", FbInterface.class);
        }

        private static FbClientLibrary syncWrapIfNecessary(FbClientLibrary clientLibrary) {
            if (JaybirdSystemProperties.isSyncWrapNativeLibrary()) {
                return (FbClientLibrary) Native.synchronizedLibrary(clientLibrary);
            }
            return clientLibrary;
        }
    }
}
