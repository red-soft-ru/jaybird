package org.firebirdsql.gds.ng.nativeoo;

import com.sun.jna.Native;
import org.firebirdsql.gds.JaybirdSystemProperties;
import org.firebirdsql.gds.ng.IAttachProperties;
import org.firebirdsql.gds.ng.jna.NativeLibraryLoadException;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.nativeoo.AbstractNativeOODatabaseFactory} to connect with embedded
 * client library via OO API.
 *
 * @since 4.0
 */
public class FbOOEmbeddedDatabaseFactory extends AbstractNativeOODatabaseFactory {

    private static final System.Logger log = System.getLogger(FbOOEmbeddedDatabaseFactory.class.getName());
    // Note Firebird 3+ embedded is fbclient + engineNN (e.g. engine12 for Firebird 3.0 / ODS 12)
    private static final List<String> LIBRARIES_TO_TRY =
            List.of("fbembed",
                    FbOOClientDatabaseFactory.LIBRARY_NAME_FBCLIENT,
                    FbOOClientDatabaseFactory.LIBRARY_NAME_RDBCLIENT);
    private static final FbOOEmbeddedDatabaseFactory INSTANCE = new FbOOEmbeddedDatabaseFactory();

    private FbOOEmbeddedDatabaseFactory() {
        // only through getInstance()
    }

    public static FbOOEmbeddedDatabaseFactory getInstance() {
        return INSTANCE;
    }

    @Override
    protected <T extends IAttachProperties<T>> T filterProperties(T attachProperties) {
        T attachPropertiesCopy = attachProperties.asNewMutable();
        // Clear server name
        attachPropertiesCopy.setServerName(null);
        return attachPropertiesCopy;
    }

    @Override
    protected Collection<String> defaultLibraryNames() {
        return LIBRARIES_TO_TRY;
    }

    @Override
    protected FbClientLibrary getClientLibrary() {
        final List<Throwable> throwables = new ArrayList<>();
        final List<String> librariesToTry = findLibrariesToTry();
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

    private static List<String> findLibrariesToTry() {
        final String libraryPath = JaybirdSystemProperties.getNativeLibraryFbclient();
        if (libraryPath != null) {
            return Collections.singletonList(libraryPath);
        }

        return LIBRARIES_TO_TRY;
    }
}
