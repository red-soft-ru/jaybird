package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.ISCConstants;
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
    private boolean firstKey = true;

    @Override
    public String getName() {
        return MULTIFACTOR_AUTH_NAME;
    }

    @Override
    public AuthStatus authenticate(ClientAuthBlock clientAuthBlock) throws SQLException {
        if (authSspi == null) {
            log.debug("Multifactor phase 1");
            authSspi = AuthSspiFactory.createAuthSspi(AuthSspiFactory.Type.TYPE3);

            String repositoryPin = clientAuthBlock.getRepositoryPin();

            if(repositoryPin != null && !repositoryPin.isEmpty()) {
                try {
                    authSspi.setRepositoryPin(repositoryPin);
                } catch (GDSAuthException e) {
                    throw new SQLException("Can not set the pin-code for the container", e);
                }
            }

            int providerID = clientAuthBlock.getProviderID();

            if (providerID != 0) {
                try {
                    authSspi.setProviderID(providerID);
                } catch (GDSException e) {
                    throw new SQLException(e.getMessage(), e);
                }
            }

            ByteBuffer data = new ByteBuffer(0);

            String userName = clientAuthBlock.getLogin();
            if (userName != null && !userName.isEmpty()) {
                AuthFactorGostPassword authFactorPassword = new AuthFactorGostPassword(authSspi);

                authFactorPassword.setUserName(userName);
                if (clientAuthBlock.getPassword() != null && !clientAuthBlock.getPassword().isEmpty()) {
                    authFactorPassword.setPassword(clientAuthBlock.getPassword());
                    authFactorPassword.setPasswordEnc(UnixCrypt.crypt(clientAuthBlock.getPassword(), "9z").substring(2, 13));
                }
                authSspi.addFactor(authFactorPassword);
                data.add((byte) AuthFactor.TYPE_PASSWORD);
            }

            String certificate = clientAuthBlock.getCertificate();
            String certificateBase64 = clientAuthBlock.getCertificateBase64();
            if ((certificate != null && !certificate.isEmpty()) ||
                    (certificateBase64 != null && !certificateBase64.isEmpty())) {
                AuthFactorCertificate authFactorCertificate = new AuthFactorCertificate(authSspi);
                authFactorCertificate.setSdRandomNumber(ISCConstants.isc_dpb_certificate_body);
                try {
                    if (certificate != null && !certificate.isEmpty())
                        authFactorCertificate.loadFromFile(certificate);
                    else
                        authFactorCertificate.setCertBase64(certificateBase64);
                } catch (GDSException e) {
                    throw new SQLException(e.getMessage(), e);
                }
                authSspi.addFactor(authFactorCertificate);
                data.add((byte) AuthFactor.TYPE_CERT_X509);
            }

            if (clientAuthBlock.getVerifyServerCertificate()) {
                AuthFactorServerCertificate authFactorServerCertificate = new AuthFactorServerCertificate(authSspi);
                authSspi.addFactor(authFactorServerCertificate);
                data.add((byte) AuthFactor.TYPE_SERVER_CERT);
            }

            if (userName == null && certificate == null && certificateBase64 == null)
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
            throw new SQLException(e.getMessage(), e);
        }

        if (authSspi.getWireKeyData() != null) {
            if (firstKey) {
                clientAuthBlock.saveSessionKey();
                firstKey = false;
            }
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

    private static String toHex(byte[] bytes) {
        return ByteArrayHelper.toHexString(bytes);
    }
}
