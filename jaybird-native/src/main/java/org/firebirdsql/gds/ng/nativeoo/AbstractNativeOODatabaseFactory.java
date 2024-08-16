package org.firebirdsql.gds.ng.nativeoo;

import org.firebirdsql.gds.JaybirdErrorCodes;
import org.firebirdsql.gds.ng.FbDatabaseFactory;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.IAttachProperties;
import org.firebirdsql.gds.ng.IConnectionProperties;
import org.firebirdsql.gds.ng.IServiceProperties;
import org.firebirdsql.gds.ng.jna.NativeLibraryLoadException;
import org.firebirdsql.jna.fbclient.FbClientLibrary;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.System.Logger.Level.TRACE;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.FbDatabaseFactory} for the Native OO protocol implementation.
 *
 * @since 4.0
 */
public abstract class AbstractNativeOODatabaseFactory implements FbDatabaseFactory {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Override
    public IDatabaseImpl connect(IConnectionProperties connectionProperties) throws SQLException {
        try {
            final NativeDatabaseConnection databaseConnection = new NativeDatabaseConnection(getClientLibrary(),
                    filterProperties(connectionProperties));
            return databaseConnection.identify();
        } catch (NativeLibraryLoadException e) {
            throw FbExceptionBuilder.forNonTransientConnectionException(JaybirdErrorCodes.jb_failedToLoadNativeLibrary)
                    .cause(e)
                    .toSQLException();
        }
    }

    @Override
    public IServiceImpl serviceConnect(IServiceProperties serviceProperties) throws SQLException {
        try {
            final IServiceConnectionImpl serviceConnection = new IServiceConnectionImpl(getClientLibrary(),
                    filterProperties(serviceProperties));
            return serviceConnection.identify();
        } catch (NativeLibraryLoadException e) {
            throw FbExceptionBuilder.forNonTransientConnectionException(JaybirdErrorCodes.jb_failedToLoadNativeLibrary)
                    .cause(e)
                    .toSQLException();
        }
    }

    protected abstract FbClientLibrary getClientLibrary();

    /**
     * Allows the database factory to perform modification of the attach properties before use.
     * <p>
     * Implementations should be prepared to handle immutable attach properties. Implementations are strongly
     * advised to copy the attach properties before modification and return this copy.
     * </p>
     *
     * @param attachProperties
     *         Attach properties
     * @param <T>
     *         Type of attach properties
     * @return Filtered properties
     */
    protected <T extends IAttachProperties<T>> T filterProperties(T attachProperties) {
        return attachProperties;
    }

    /**
     * @return the default library names loaded by this factory
     */
    protected abstract Collection<String> defaultLibraryNames();
}
