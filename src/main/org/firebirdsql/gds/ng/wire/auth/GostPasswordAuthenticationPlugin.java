package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.auth.*;
import org.firebirdsql.gds.ng.wire.auth.legacy.LegacyHash;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class GostPasswordAuthenticationPlugin implements AuthenticationPlugin {

    private static final System.Logger log = System.getLogger(GostPasswordAuthenticationPlugin.class.getName());

    public static final String GOST_PASSWORD_AUTH_NAME = "GostPassword";

    private AuthSspi authSspi = null;
    private byte[] clientData;
    private byte[] serverData;

    @Override
    public String getName() {
        return GOST_PASSWORD_AUTH_NAME;
    }

    @Override
    public AuthStatus authenticate(ClientAuthBlock clientAuthBlock) throws SQLException {
        if (authSspi == null) {
            log.log(System.Logger.Level.DEBUG, "GostPassword phase 1");
            authSspi = AuthSspiFactory.createAuthSspi(AuthSspiFactory.Type.TYPE4);

            ByteBuffer data = new ByteBuffer(0);

            String userName = clientAuthBlock.getLogin();
            String password = clientAuthBlock.getPassword();

            if ((userName == null || userName.isEmpty()) || (password == null || password.isEmpty()))
                return AuthStatus.AUTH_CONTINUE;

            AuthFactorGostPassword authFactorGostPassword = new AuthFactorGostPassword(authSspi);

            authFactorGostPassword.setUserName(userName);
            authFactorGostPassword.setPassword(password);
            authFactorGostPassword.setPasswordEnc(Arrays.toString(LegacyHash.fbCrypt(password)));
            authSspi.addFactor(authFactorGostPassword);
            authSspi.request(data);

            clientData = Arrays.copyOf(data.getData(), data.getLength());

            return AuthStatus.AUTH_MORE_DATA;
        }

        log.log(System.Logger.Level.DEBUG, "GostPassword phase 2");
        ByteBuffer data = new ByteBuffer(0);
        if (serverData != null)
            data.add(serverData);

        authSspi.request(data);

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
        } catch (SQLException e) {
            throw new SQLException("Can't generate session key", e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getName();
    }
}
