package org.firebirdsql.nativeoo.gds.ng;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.WinFbClientLibrary;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class FbOOClientDatabaseFactory extends AbstractNativeOODatabaseFactory {
    private static final FbOOClientDatabaseFactory INSTANCE = new FbOOClientDatabaseFactory();

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
            return (FbClientLibrary) Native.loadLibrary(System.getProperty("org.firebirdsql.jna.fbclient", "fbclient"), FbInterface.class);
        }

        private static FbClientLibrary syncWrapIfNecessary(FbClientLibrary clientLibrary) {
            if ("true".equalsIgnoreCase(getSystemPropertyPrivileged("org.firebirdsql.jna.syncWrapNativeLibrary"))) {
                return (FbClientLibrary) Native.synchronizedLibrary(clientLibrary);
            }
            return clientLibrary;
        }

        private static String getSystemPropertyPrivileged(final String propertyName) {
            return AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty(propertyName);
                }
            });
        }
    }
}
