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
package org.firebirdsql.gds.ng.wire.version10;

import org.firebirdsql.gds.ConnectionParameterBuffer;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.ParameterTagMapping;
import org.firebirdsql.gds.ng.AbstractConnection;
import org.firebirdsql.gds.ng.AbstractParameterConverter;
import org.firebirdsql.gds.ng.wire.WireDatabaseConnection;
import org.firebirdsql.gds.ng.wire.WireServiceConnection;
import org.firebirdsql.gds.ng.wire.auth.UnixCrypt;
import org.firebirdsql.jdbc.FirebirdConnectionProperties;

import java.sql.SQLException;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.ParameterConverter} for the version 10 protocol.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @since 3.0
 */
public class V10ParameterConverter extends AbstractParameterConverter<WireDatabaseConnection, WireServiceConnection> {

    private static final String LEGACY_PASSWORD_SALT = "9z";

    @Override
    protected void populateAuthenticationProperties(final AbstractConnection connection,
            final ConnectionParameterBuffer pb) throws SQLException {
        FirebirdConnectionProperties props = connection.getAttachProperties();
        ParameterTagMapping tagMapping = pb.getTagMapping();
        if (props.getUserName() != null) {
            pb.addArgument(tagMapping.getUserNameTag(), props.getUserName());
        }
        if (props.getPassword() != null && props.getNonStandardProperty("isc_dpb_password_enc") == null) {
            pb.addArgument(tagMapping.getEncryptedPasswordTag(), UnixCrypt.crypt(props.getPassword(),
                    LEGACY_PASSWORD_SALT).substring(2, 13));
        }
        if (props.getNonStandardProperty("isc_dpb_password_enc") != null) {
            pb.addArgument(tagMapping.getEncryptedPasswordTag(), props.getNonStandardProperty("isc_dpb_password_enc"));
        }
        if (props.getEffectiveLogin() != null) {
            pb.addArgument(tagMapping.getEffectiveLoginTag(), props.getEffectiveLogin());
        }
        if (props.isUseGSSAuth()) {
            pb.addArgument(tagMapping.getGSSAuthTag(), 1);
        }
        if (props.getCertificate() != null) {
            pb.addArgument(ISCConstants.isc_dpb_certificate, props.getCertificate());
        }
        if (props.getCertificateBase64() != null) {
            pb.addArgument(ISCConstants.isc_dpb_certificate_base64, props.getCertificateBase64());
        }
        if (props.getRepositoryPin() != null) {
            pb.addArgument(ISCConstants.isc_dpb_repository_pin, props.getRepositoryPin());
        }
    }

}
