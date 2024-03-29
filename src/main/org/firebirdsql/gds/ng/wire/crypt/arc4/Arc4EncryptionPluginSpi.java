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
package org.firebirdsql.gds.ng.wire.crypt.arc4;

import org.firebirdsql.gds.ng.wire.crypt.CryptConnectionInfo;
import org.firebirdsql.gds.ng.wire.crypt.CryptSessionConfig;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionIdentifier;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionPlugin;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionPluginSpi;

/**
 * ARC4 encryption plugin provider.
 *
 * @author Mark Rotteveel
 * @since 4.0
 */
public final class Arc4EncryptionPluginSpi implements EncryptionPluginSpi {

    static final EncryptionIdentifier ARC4_ID = new EncryptionIdentifier("Symmetric", "Arc4");

    @Override
    public EncryptionIdentifier encryptionIdentifier() {
        return ARC4_ID;
    }

    @Override
    public EncryptionPlugin createEncryptionPlugin(CryptSessionConfig cryptSessionConfig) {
        return new Arc4EncryptionPlugin(cryptSessionConfig);
    }

    @Override
    public boolean isSupported(CryptConnectionInfo cryptConnectionInfo) {
        // TODO Maybe check if ARC4 requirements are allowed by the security config?
        return true;
    }

}
