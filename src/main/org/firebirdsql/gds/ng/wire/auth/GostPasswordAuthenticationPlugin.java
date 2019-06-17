package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.auth.*;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class GostPasswordAuthenticationPlugin implements AuthenticationPlugin {

    private static final Logger log = LoggerFactory.getLogger(GostPasswordAuthenticationPlugin.class);

    public static final String GOST_PASSWORD_AUTH_NAME = "GostPassword";

    private AuthSspi4 authSspi = null;
    private byte[] clientData;
    private byte[] serverData;

    @Override
    public String getName() {
        return GOST_PASSWORD_AUTH_NAME;
    }

    @Override
    public AuthStatus authenticate(ClientAuthBlock clientAuthBlock) throws SQLException {
        if (authSspi == null) {
            log.debug("GostPassword phase 1");
            authSspi = new AuthSspi4();

            ByteBuffer data = new ByteBuffer(0);

            String userName = clientAuthBlock.getLogin();
            if (userName != null && !userName.isEmpty()) {
                AuthFactorPassword authFactorGostPassword = new AuthFactorPassword(authSspi);

                authFactorGostPassword.setUserName(userName);
                String password = clientAuthBlock.getPassword();
                authFactorGostPassword.setPassword(password);
                authFactorGostPassword.setPasswordEnc(password == null ? null : UnixCrypt.crypt(password, "9z").substring(2, 13));
                authSspi.addFactor(authFactorGostPassword);
                try {
                    authSspi.request(data);
                } catch (GDSAuthException e) {
                    throw new SQLException(e);
                }
            }

            if (userName == null)
                return  AuthStatus.AUTH_CONTINUE;

            clientData = Arrays.copyOf(data.getData(), data.getLength());

            return AuthStatus.AUTH_MORE_DATA;
        }

        log.debug("GostPassword phase 2");
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
        throw new SQLException("GostPasswordAuthenticationPlugin cannot generate a session key");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getName();
    }
}
