package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.auth.*;
import org.firebirdsql.gds.ng.wire.auth.legacy.UnixCrypt;
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

    private AuthSspi authSspi = null;
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

            String repositoryPin = clientAuthBlock.getRepositoryPin();

            if(repositoryPin != null && !repositoryPin.isEmpty()) {
                try {
                    authSspi.setRepositoryPin(repositoryPin);
                } catch (GDSAuthException e) {
                    throw new SQLException("Can not set the pin-code for the container", e);
                }
            }

            ByteBuffer data = new ByteBuffer(0);

            String userName = clientAuthBlock.getLogin();
            if (userName != null && !userName.isEmpty()) {
                AuthFactorGostPassword authFactorGostPassword = new AuthFactorGostPassword(authSspi);

                authFactorGostPassword.setUserName(userName);
                authFactorGostPassword.setPassword(clientAuthBlock.getPassword());
                authFactorGostPassword.setPasswordEnc(UnixCrypt.crypt(clientAuthBlock.getPassword(), "9z").substring(2, 13));
                authSspi.addFactor(authFactorGostPassword);
                data.add((byte) AuthFactor.TYPE_PASSWORD);
            }

            String certificate = clientAuthBlock.getCertificate();
            if (certificate != null && !certificate.isEmpty()) {
                AuthFactorCertificate authFactorCertificate = new AuthFactorCertificate(authSspi);
                try {
                    authFactorCertificate.loadFromFile(certificate);
                } catch (GDSException e) {
                    throw new SQLException(e);
                }
                authSspi.addFactor(authFactorCertificate);
                data.add((byte) AuthFactor.TYPE_CERT_X509);
            }

            if (clientAuthBlock.getVerifyServerCertificate()) {
                AuthFactorServerCertificate authFactorServerCertificate = new AuthFactorServerCertificate(authSspi);
                authSspi.addFactor(authFactorServerCertificate);
                data.add((byte) AuthFactor.TYPE_SERVER_CERT);
            }

            if (userName == null && certificate == null)
                return  AuthStatus.AUTH_CONTINUE;

            clientData = Arrays.copyOf(data.getData(), data.getLength());

            return AuthStatus.AUTH_MORE_DATA;
        }

        log.debug("Multifactor phase 2");
        ByteBuffer data = new ByteBuffer(0);
        if (serverData != null)
            data.add(serverData);
        try {
            authSspi.request(data);
        } catch (GDSAuthException e) {
            throw new SQLException(e);
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
    public boolean generatesSessionKey() {
        return false;
    }

    @Override
    public byte[] getSessionKey() throws SQLException {
        throw new SQLException("MultifactorAuthenticationPlugin cannot generate a session key");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getName();
    }

    private static String toHex(byte[] bytes) {
        return ByteArrayHelper.toHexString(bytes);
    }
}
