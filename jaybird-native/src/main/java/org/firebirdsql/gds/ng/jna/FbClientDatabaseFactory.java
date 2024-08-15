/*
 * Firebird Open Source JDBC Driver
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
import org.firebirdsql.gds.JaybirdSystemProperties;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.WinFbClientLibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbDatabaseFactory} for establishing connection using the
 * Firebird native client library.
 * <p>
 * A separate factory is used for embedded: {@link FbEmbeddedDatabaseFactory}.
 * </p>
 *
 * @author Mark Rotteveel
 * @since 3.0
 */
public final class FbClientDatabaseFactory extends AbstractNativeDatabaseFactory {

    private static final System.Logger log = System.getLogger(FbClientDatabaseFactory.class.getName());
    private static final FbClientDatabaseFactory INSTANCE = new FbClientDatabaseFactory();
    static final String LIBRARY_NAME_FBCLIENT = "fbclient";
    static final String LIBRARY_NAME_RDBCLIENT = "rdbclient";

    private FbClientDatabaseFactory() {
        // only through getInstance()
    }

    public static FbClientDatabaseFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected FbClientLibrary createClientLibrary() {
        final List<Throwable> throwables = new ArrayList<>();
        final List<String> librariesToTry = JaybirdSystemProperties.getNativeLibraryFbclient() != null ?
                List.of(JaybirdSystemProperties.getNativeLibraryFbclient()) : (List<String>) defaultLibraryNames();
        for (String libraryName : librariesToTry) {
            try {
                if (Platform.isWindows()) {
                    return Native.load(libraryName, WinFbClientLibrary.class);
                } else {
                    return Native.load(libraryName, FbClientLibrary.class);
                }
            } catch (RuntimeException | UnsatisfiedLinkError e) {
                throwables.add(e);
                log.log(DEBUG, () -> "Attempt to load %s failed".formatted(libraryName), e);
                // continue with next
            }
        }
        assert throwables.size() == librariesToTry.size();
        if (log.isLoggable(ERROR)) {
            log.log(ERROR, "Could not load any of the libraries in {0}:", librariesToTry);
            for (int idx = 0; idx < librariesToTry.size(); idx++) {
                log.log(ERROR, "Loading %s failed".formatted(librariesToTry.get(idx)), throwables.get(idx));
            }
        }
        throw new NativeLibraryLoadException("Could not load any of " + librariesToTry + "; linking first exception",
                throwables.get(0));
    }

    @Override
    protected Collection<String> defaultLibraryNames() {
        return List.of(LIBRARY_NAME_FBCLIENT, LIBRARY_NAME_RDBCLIENT);
    }

}
