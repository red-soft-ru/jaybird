package org.firebirdsql.gds.ng.wire.auth;

/**
 * @author vasiliy
 */
public class MultifactorAuthenticationPluginSpi implements AuthenticationPluginSpi {
    @Override
    public String getPluginName() {
        return MultifactorAuthenticationPlugin.MULTIFACTOR_AUTH_NAME;
    }

    @Override
    public AuthenticationPlugin createPlugin() {
        return new MultifactorAuthenticationPlugin();
    }
}
