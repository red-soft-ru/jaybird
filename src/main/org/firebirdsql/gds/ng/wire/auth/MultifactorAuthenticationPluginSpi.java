package org.firebirdsql.gds.ng.wire.auth;

/**
 * @author vasiliy
 */
public class MultifactorAuthenticationPluginSpi implements AuthenticationPluginSpi {

    public static final String MULTIFACTOR_AUTH_NAME = "Multifactor";

    @Override
    public String getPluginName() {
        return MultifactorAuthenticationPlugin.MULTIFACTOR_AUTH_NAME;
    }

    @Override
    public AuthenticationPlugin createPlugin() {
        return new MultifactorAuthenticationPlugin();
    }
}
