package org.firebirdsql.gds.ng.wire.crypt.wincrypt;

import org.firebirdsql.gds.ng.wire.WireConnection;
import org.firebirdsql.gds.ng.wire.crypt.CryptConnectionInfo;
import org.firebirdsql.gds.ng.wire.crypt.CryptSessionConfig;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionIdentifier;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionPlugin;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionPluginSpi;

/**
 * Wire_WinCrypt encryption plugin provider.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0.14
 */
public class WireWinCryptEncryptionPluginSpi implements EncryptionPluginSpi {

    static final EncryptionIdentifier WIRE_WINCRYPT_ID = new EncryptionIdentifier("Symmetric", "Wire_WinCrypt");

    @Override
    public EncryptionIdentifier encryptionIdentifier() {
        return WIRE_WINCRYPT_ID;
    }

    @Override
    public EncryptionPlugin createEncryptionPlugin(CryptSessionConfig cryptSessionConfig) {
        return new WireWinCryptEncryptionPlugin(cryptSessionConfig);
    }

    @Override
    public boolean isSupported(CryptConnectionInfo cryptConnectionInfo) {
        return true;
    }
}
