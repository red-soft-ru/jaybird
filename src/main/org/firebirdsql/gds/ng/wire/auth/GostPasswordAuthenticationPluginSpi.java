package org.firebirdsql.gds.ng.wire.auth;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class GostPasswordAuthenticationPluginSpi implements AuthenticationPluginSpi {
    @Override
    public String getPluginName() {
        return GostPasswordAuthenticationPlugin.GOST_PASSWORD_AUTH_NAME;
    }

    @Override
    public AuthenticationPlugin createPlugin() {
        return new GostPasswordAuthenticationPlugin();
    }
}
