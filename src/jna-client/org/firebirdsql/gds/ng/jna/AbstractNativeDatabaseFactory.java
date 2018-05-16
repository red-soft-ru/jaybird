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

import org.firebirdsql.gds.ng.*;
import org.firebirdsql.gds.ng.jna.interfaces.IDatabaseConnectionImpl;
import org.firebirdsql.jna.fbclient.FbClientLibrary;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common implementation for client library and embedded database factory.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public abstract class AbstractNativeDatabaseFactory implements FbDatabaseFactory {

    @Override
    public FbDatabase connect(IConnectionProperties connectionProperties) throws SQLException {

        // TODO check the correctness of the required interface
        FbClientLibrary clientLibrary = getClientLibrary();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        clientLibrary.isc_get_client_version(byteBuffer);
        String clientVersion = new String(byteBuffer.array()).trim();
        Pattern p = Pattern.compile("\\s([\\d]+[.][\\d]+)\\b");
        Matcher m = p.matcher(clientVersion);
        m.find();
        String version = m.group(1);
        int majorVersion = version.charAt(0) - '0';
        if (majorVersion >= 3) {
            final IDatabaseConnectionImpl databaseConnection = new IDatabaseConnectionImpl(clientLibrary,
                    filterProperties(connectionProperties));
            return databaseConnection.identify();
        } else {
            final JnaDatabaseConnection jnaDatabaseConnection = new JnaDatabaseConnection(clientLibrary,
                    filterProperties(connectionProperties));
            return jnaDatabaseConnection.identify();
        }
    }

    @Override
    public JnaService serviceConnect(IServiceProperties serviceProperties) throws SQLException {
        final JnaServiceConnection jnaServiceConnection = new JnaServiceConnection(getClientLibrary(),
                filterProperties(serviceProperties));
        return jnaServiceConnection.identify();
    }

    protected abstract FbClientLibrary getClientLibrary();

    /**
     * Allows the database factory to perform modification of the attach properties before use.
     * <p>
     * Implementations should be prepared to handle immutable attach properties. Implementations are strongly
     * advised to copy the attach properties before modification and return this copy.
     * </p>
     *
     * @param attachProperties Attach properties
     * @param <T> Type of attach properties
     * @return Filtered properties
     */
    protected <T extends IAttachProperties<T>> T filterProperties(T attachProperties) {
        return attachProperties;
    }
}
