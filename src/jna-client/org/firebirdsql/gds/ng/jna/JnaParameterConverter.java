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

import org.firebirdsql.gds.ConnectionParameterBuffer;
import org.firebirdsql.gds.ParameterTagMapping;
import org.firebirdsql.gds.ng.AbstractConnection;
import org.firebirdsql.gds.ng.AbstractParameterConverter;
import org.firebirdsql.gds.ng.WireCrypt;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import java.sql.SQLException;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.ParameterConverter} for JNA.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public class JnaParameterConverter extends AbstractParameterConverter<JnaDatabaseConnection, JnaServiceConnection> {

    @Override
    protected void populateAuthenticationProperties(final AbstractConnection connection,
            final ConnectionParameterBuffer pb) throws SQLException {
        FirebirdConnectionProperties props = connection.getAttachProperties();
        ParameterTagMapping tagMapping = pb.getTagMapping();
        if (props.getUserName() != null) {
            pb.addArgument(tagMapping.getUserNameTag(), props.getUserName());
        }
        if (props.getPassword() != null) {
            pb.addArgument(tagMapping.getPasswordTag(), props.getPassword());
        }
        if (props.getEffectiveLogin() != null) {
            pb.addArgument(tagMapping.getEffectiveLoginTag(), props.getEffectiveLogin());
        }

        if (WireCrypt.fromString(props.getWireCrypt()) != WireCrypt.DEFAULT) {
            // Need to do this differently when having to add multiple configs
            String configString = "WireCrypt = " + props.getWireCrypt();
            pb.addArgument(tagMapping.getConfigTag(), configString);
        }
    }
}
