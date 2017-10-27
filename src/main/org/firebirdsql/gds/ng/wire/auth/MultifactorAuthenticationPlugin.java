package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.auth.*;
import org.firebirdsql.gds.ng.IAttachProperties;
import org.firebirdsql.jdbc.FBConnectionProperties;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;
import org.firebirdsql.util.ByteArrayHelper;

import java.sql.SQLException;
import java.util.Arrays;

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

            ByteBuffer data = new ByteBuffer(0);

            String userName = clientAuthBlock.getLogin();
            if (userName != null && !userName.isEmpty()) {
                AuthFactorPassword authFactorPassword = new AuthFactorPassword(authSspi);

                authFactorPassword.setUserName(userName);
                authFactorPassword.setPassword(clientAuthBlock.getPassword());
                authFactorPassword.setPasswordEnc(UnixCrypt.crypt(clientAuthBlock.getPassword(), "9z").substring(2, 13));
                authSspi.addFactor(authFactorPassword);
                data.add((byte) AuthFactor.TYPE_PASSWORD);
            }

            String certificate = clientAuthBlock.getCertificate();
            if (certificate != null && !certificate.isEmpty()) {
                AuthFactorCertificate authFactorCertificate = new AuthFactorCertificate(authSspi);
                try {
                    authFactorCertificate.loadFromFile(certificate);
                } catch (GDSException e) {
                    throw new SQLException(e.getMessage(), e);
                }
                authSspi.addFactor(authFactorCertificate);
                data.add((byte) AuthFactor.TYPE_CERT_X509);
            }
            clientData = Arrays.copyOf(data.getData(), data.getLength());

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

        clientData = Arrays.copyOf(data.getData(), data.getLength());
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
