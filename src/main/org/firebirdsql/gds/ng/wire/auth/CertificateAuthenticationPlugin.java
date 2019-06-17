package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.auth.*;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class CertificateAuthenticationPlugin implements AuthenticationPlugin {

    private static final Logger log = LoggerFactory.getLogger(CertificateAuthenticationPlugin.class);

    public static final String CERTIFICATE_AUTH_NAME = "Certificate";

    private AuthSspi4 authSspi = null;
    private byte[] clientData;
    private byte[] serverData;

    @Override
    public String getName() {
        return CERTIFICATE_AUTH_NAME;
    }

    @Override
    public AuthStatus authenticate(ClientAuthBlock clientAuthBlock) throws SQLException {
        if (authSspi == null) {
            log.debug("Certificate phase 1");
            authSspi = new AuthSspi4();

            String repositoryPin = clientAuthBlock.getRepositoryPin();

            if(repositoryPin != null && !repositoryPin.isEmpty()) {
                try {
                    authSspi.setRepositoryPin(repositoryPin);
                } catch (GDSAuthException e) {
                    throw new SQLException("Can not set the pin-code for the container", e);
                }
            }

            ByteBuffer data = new ByteBuffer(0);

            String certificate = clientAuthBlock.getCertificate();
            if (certificate != null && !certificate.isEmpty()) {
                AuthFactorCertificate authFactorCertificate = new AuthFactorCertificate(authSspi);
                authFactorCertificate.setSdRandomNumber(1);
                try {
                    authFactorCertificate.loadFromFile(certificate);
                    authSspi.addFactor(authFactorCertificate);
                    authSspi.request(data);
                } catch (GDSException e) {
                    throw new SQLException(e);
                }
            }

            if (clientAuthBlock.getVerifyServerCertificate()) {
                AuthFactorServerCertificate authFactorServerCertificate = new AuthFactorServerCertificate(authSspi);
                authSspi.addFactor(authFactorServerCertificate);
                data.add((byte) AuthFactor.TYPE_SERVER_CERT);
            }

            if (certificate == null)
                return  AuthStatus.AUTH_CONTINUE;

            clientData = Arrays.copyOf(data.getData(), data.getLength());

            return AuthStatus.AUTH_MORE_DATA;
        }

        log.debug("Certificate phase 2");
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
        throw new SQLException("CertificateAuthenticationPlugin cannot generate a session key");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getName();
    }
}
