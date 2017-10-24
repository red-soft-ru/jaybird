package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.auth.AuthFactor;
import org.firebirdsql.gds.impl.wire.auth.AuthFactorCertificate;
import org.firebirdsql.gds.impl.wire.auth.AuthSspi;
import org.firebirdsql.gds.impl.wire.auth.GDSAuthException;
import org.firebirdsql.gds.ng.IAttachProperties;
import org.firebirdsql.jdbc.FBConnectionProperties;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;
import org.firebirdsql.util.ByteArrayHelper;

import java.sql.SQLException;

/**
 * @author vasiliy
 */
public class MultifactorAuthenticationPlugin implements AuthenticationPlugin {

    private static final Logger log = LoggerFactory.getLogger(MultifactorAuthenticationPlugin.class);

    public static final String MULTIFACTOR_AUTH_NAME = "Multifactor";

    private static AuthSspi authSspi = null;
    private byte[] clientData;
    private byte[] serverData;

    @Override
    public String getName() {
        return MULTIFACTOR_AUTH_NAME;
    }

    @Override
    public AuthStatus authenticate(ClientAuthBlock clientAuthBlock) throws SQLException {
        if (authSspi == null) {
            log.debug("Multifactor phase 1");
            authSspi = new AuthSspi();
            AuthFactorCertificate authFactorCertificate = new AuthFactorCertificate(authSspi);
            try {
                authFactorCertificate.loadFromFile(clientAuthBlock.getCertificate());
            } catch (GDSException e) {
                throw new SQLException(e.getMessage(), e);
            }
            authSspi.addFactor(authFactorCertificate);
            ByteBuffer data = new ByteBuffer(0);
            data.add((byte)AuthFactor.TYPE_CERT_X509);
            clientData = data.getData();

            return AuthStatus.AUTH_MORE_DATA;
        }

        log.debug("Multifactor phase 2");
        ByteBuffer data = new ByteBuffer(0);
        data.add(serverData);
        try {
            authSspi.request(data);
        } catch (GDSAuthException e) {
            throw new SQLException(e.getMessage(), e);
        }

        clientData = data.getData();
        return AuthStatus.AUTH_MORE_DATA;
    }

    @Override
    public byte[] getClientData() {
        return clientData;
    }

    @Override
    public void setServerData(byte[] serverData) {
        this.serverData = serverData;
    }

    @Override
    public boolean hasServerData() {
        return serverData != null && serverData.length > 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getName();
    }

    private static String toHex(byte[] bytes) {
        return ByteArrayHelper.toHexString(bytes);
    }
}
