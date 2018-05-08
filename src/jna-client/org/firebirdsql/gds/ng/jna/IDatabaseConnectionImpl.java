package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.encodings.EncodingFactory;
import org.firebirdsql.encodings.IEncodingFactory;
import org.firebirdsql.gds.ng.IConnectionProperties;
import org.firebirdsql.jna.fbclient.FbClientLibrary;

import java.sql.SQLException;

public class IDatabaseConnectionImpl extends JnaConnection<IConnectionProperties, IDatabaseImpl> {

    /**
     * Creates a IDatabaseConnectionImpl (without establishing a connection to the server).
     *
     * @param clientLibrary
     *         Client library to use
     * @param connectionProperties
     *         Connection properties
     */
    public IDatabaseConnectionImpl(FbClientLibrary clientLibrary, IConnectionProperties connectionProperties)
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
    protected IDatabaseConnectionImpl(FbClientLibrary clientLibrary, IConnectionProperties attachProperties, IEncodingFactory encodingFactory) throws SQLException {
        super(clientLibrary, attachProperties, encodingFactory);
    }

    @Override
    public IDatabaseImpl identify() throws SQLException {
        return new IDatabaseImpl(this);
    }
}
