package org.firebirdsql.nativeoo.gds.ng;

import org.firebirdsql.jdbc.FirebirdConnectionProperties;
import org.firebirdsql.jna.fbclient.FbClientLibrary;

public class FbOOLocalDatabaseFactory extends AbstractNativeOODatabaseFactory {

    private static final FbOOLocalDatabaseFactory INSTANCE = new FbOOLocalDatabaseFactory();

    @Override
    protected FbClientLibrary getClientLibrary() {
        return FbOOClientDatabaseFactory.getInstance().getClientLibrary();
    }

    @Override
    protected <T extends FirebirdConnectionProperties> FirebirdConnectionProperties filterProperties(T attachProperties) {
        FirebirdConnectionProperties attachPropertiesCopy = attachProperties.asNewMutable();
        // Clear server name
        attachPropertiesCopy.setServer(null);
        return attachPropertiesCopy;
    }

    public static FbOOLocalDatabaseFactory getInstance() {
        return INSTANCE;
    }
}
