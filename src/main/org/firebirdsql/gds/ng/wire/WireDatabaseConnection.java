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
package org.firebirdsql.gds.ng.wire;

import org.firebirdsql.encodings.IEncodingFactory;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import java.sql.SQLException;

/**
 * Wire connection instance for connecting to a database
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public final class WireDatabaseConnection extends WireConnection<FirebirdConnectionProperties, FbWireDatabase> {

    /**
     * Creates a WireDatabaseConnection (without establishing a connection to the server) with the default
     * protocol collection.
     *
     * @param connectionProperties
     *         Connection properties
     */
    public WireDatabaseConnection(FirebirdConnectionProperties connectionProperties) throws SQLException {
        super(connectionProperties);
    }

    /**
     * Creates a WireDatabaseConnection (without establishing a connection to the server).
     *
     * @param connectionProperties
     *         Connection properties
     * @param encodingFactory
     *         Factory for encoding definitions
     * @param protocols
     *         The collection of protocols to use for this connection.
     */
    public WireDatabaseConnection(FirebirdConnectionProperties connectionProperties, IEncodingFactory encodingFactory,
            ProtocolCollection protocols) throws SQLException {
        super(connectionProperties, encodingFactory, protocols);
    }

    @Override
    protected FbWireDatabase createConnectionHandle(ProtocolDescriptor protocolDescriptor) {
        return protocolDescriptor.createDatabase(this);
    }

    @Override
    public final String getAttachObjectName() {
        return getAttachProperties().getDatabase();
    }
}
