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
            String certificateBase64 = clientAuthBlock.getCertificateBase64();

            if (certificate == null && certificateBase64 == null)
                return  AuthStatus.AUTH_CONTINUE;

            if ((certificate != null && !certificate.isEmpty()) ||
                    (certificateBase64 != null && !certificateBase64.isEmpty())) {
                AuthFactorCertificate authFactorCertificate = new AuthFactorCertificate(authSspi);
                authFactorCertificate.setSdRandomNumber(1);
                try {
                    if (certificate != null && !certificate.isEmpty())
                        authFactorCertificate.loadFromFile(certificate);
                    else
                        authFactorCertificate.setCertBase64(certificateBase64);
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

        if (authSspi.getWireKeyData() != null)
            clientAuthBlock.saveSessionKey();

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
        return true;
    }

    @Override
    public byte[] getSessionKey() throws SQLException {
        if (this.authSspi != null && authSspi.getWireKeyData() != null)
            return authSspi.getWireKeyData();
        try {
            return AuthMethods.generateRandom(null, 20);
        } catch (GDSAuthException e) {
            throw new SQLException("Can't generate session key", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getName();
    }
}
