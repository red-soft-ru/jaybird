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
package org.firebirdsql.gds.ng.jna;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.WinFbClientLibrary;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbDatabaseFactory} for establishing connection using the
 * Firebird embedded library.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public class FbEmbeddedDatabaseFactory extends AbstractNativeDatabaseFactory {

    private static final Logger log = LoggerFactory.getLogger(FbEmbeddedDatabaseFactory.class);
    private static final FbEmbeddedDatabaseFactory INSTANCE = new FbEmbeddedDatabaseFactory();

    @Override
    protected FbClientLibrary getClientLibrary() {
        return ClientHolder.clientLibrary;
    }

    @Override
    protected <T extends FirebirdConnectionProperties> FirebirdConnectionProperties filterProperties(T attachProperties) {
        FirebirdConnectionProperties attachPropertiesCopy = attachProperties.asNewMutable();
        // Clear server name
        attachPropertiesCopy.setServer(null);
        return attachPropertiesCopy;
    }

    public static FbEmbeddedDatabaseFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Initialization-on-demand depending on classloading behavior specified in JLS 12.4
     */
    private static final class ClientHolder {

        // Note Firebird 3 embedded is fbclient + engine12
        private static final List<String> LIBRARIES_TO_TRY =
                Collections.unmodifiableList(Arrays.asList("fbembed", "fbclient"));

        private static final FbClientLibrary clientLibrary = syncWrapIfNecessary(initClientLibrary());

        private static FbClientLibrary initClientLibrary() {
            final List<Throwable> throwables = new ArrayList<>();
            final List<String> librariesToTry = findLibrariesToTry();
            for (String libraryName : librariesToTry) {
                try {
                    if (Platform.isWindows()) {
                        return (FbClientLibrary) Native.loadLibrary(libraryName, WinFbClientLibrary.class);
                    } else {
                        return (FbClientLibrary) Native.loadLibrary(libraryName, FbClientLibrary.class);
                    }
                } catch (UnsatisfiedLinkError e) {
                    throwables.add(e);
                    log.debug("Attempt to load " + libraryName + " failed", e);
                    // continue with next
                }
            }
            assert throwables.size() == librariesToTry.size();
            log.error("Could not load any of the libraries in " + librariesToTry + ":");
            for (int idx = 0; idx < librariesToTry.size(); idx++) {
                log.error("Loading " + librariesToTry.get(idx) + " failed", throwables.get(idx));
            }
            throw new ExceptionInInitializerError(throwables.get(0));
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

        private static List<String> findLibrariesToTry() {
            final String libraryPath = System.getProperty("org.firebirdsql.jna.fbclient");
            if (libraryPath != null) {
                return Collections.singletonList(libraryPath);
            }

            return LIBRARIES_TO_TRY;
        }
    }

}
