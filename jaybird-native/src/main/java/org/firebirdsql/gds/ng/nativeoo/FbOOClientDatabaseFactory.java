package org.firebirdsql.gds.ng.nativeoo;

import com.sun.jna.Native;
import org.firebirdsql.gds.JaybirdSystemProperties;
import org.firebirdsql.gds.ng.jna.NativeLibraryLoadException;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.nativeoo.AbstractNativeOODatabaseFactory} to connect with native
 * client library via OO API.
 *
 * @since 4.0
 */
public class FbOOClientDatabaseFactory extends AbstractNativeOODatabaseFactory {
    private static final System.Logger log = System.getLogger(FbOOClientDatabaseFactory.class.getName());
    private static final FbOOClientDatabaseFactory INSTANCE = new FbOOClientDatabaseFactory();
    static final String LIBRARY_NAME_FBCLIENT = "fbclient";
    static final String LIBRARY_NAME_RDBCLIENT = "rdbclient";

    private FbOOClientDatabaseFactory() {
        // only through getInstance()
    }

    public static FbOOClientDatabaseFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected FbClientLibrary getClientLibrary() {
        final List<Throwable> throwables = new ArrayList<>();
        final List<String> librariesToTry = JaybirdSystemProperties.getNativeLibraryFbclient() != null ?
                List.of(JaybirdSystemProperties.getNativeLibraryFbclient()) : (List<String>) defaultLibraryNames();
        for (String libraryName : librariesToTry) {
            try {
                return Native.load(libraryName, FbInterface.class);
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
