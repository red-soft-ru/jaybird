package org.firebirdsql.nativeoo.gds.ng;

import org.firebirdsql.encodings.EncodingFactory;
import org.firebirdsql.encodings.IEncodingFactory;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;
import org.firebirdsql.jna.fbclient.FbClientLibrary;

import java.sql.SQLException;

/**
 * Class handling the initial setup of the native OO API database connection.
 *
 * @since 4.0
 */
public class NativeDatabaseConnection extends AbstractNativeConnection<FirebirdConnectionProperties, IDatabaseImpl> {

    /**
     * Creates a IDatabaseConnectionImpl (without establishing a connection to the server).
     *
     * @param clientLibrary
     *         Client library to use
     * @param connectionProperties
     *         Connection properties
     */
    public NativeDatabaseConnection(FbClientLibrary clientLibrary, FirebirdConnectionProperties connectionProperties)
            throws SQLException {
        this(clientLibrary, connectionProperties, EncodingFactory.getPlatformDefault());
    }

    /**
     * Creates a IDatabaseConnectionImpl (without establishing a connection to the server).
     *
     * @param clientLibrary    Client library to use
     * @param attachProperties Attach properties
     * @param encodingFactory
     */
    protected NativeDatabaseConnection(FbClientLibrary clientLibrary, FirebirdConnectionProperties attachProperties,
                                       IEncodingFactory encodingFactory) throws SQLException {
        super(clientLibrary, attachProperties, encodingFactory);
    }

    @Override
    public IDatabaseImpl identify() throws SQLException {
        return new IDatabaseImpl(this);
    }

    @Override
    public final String getAttachObjectName() {
        return getAttachProperties().getDatabase();
    }
}
