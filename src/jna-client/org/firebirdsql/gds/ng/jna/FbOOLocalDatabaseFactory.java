package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.jna.interfaces.IServiceConnectionImpl;
import org.firebirdsql.jna.fbclient.FbClientLibrary;

import java.sql.SQLException;

public class FbOOLocalDatabaseFactory extends AbstractNativeDatabaseFactory {

    private static final FbOOLocalDatabaseFactory INSTANCE = new FbOOLocalDatabaseFactory();

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
        return FbOOClientDatabaseFactory.getInstance().getClientLibrary();
    }

    @Override
    protected <T extends IAttachProperties<T>> T filterProperties(T attachProperties) {
        T attachPropertiesCopy = attachProperties.asNewMutable();
        // Clear server name
        attachPropertiesCopy.setServerName(null);
        return attachPropertiesCopy;
    }

    public static FbOOLocalDatabaseFactory getInstance() {
        return INSTANCE;
    }
}
