package org.firebirdsql.gds.ng.wire.auth;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class CertificateAuthenticationPluginSpi implements AuthenticationPluginSpi {

    public static final String CERTIFICATE_AUTH_NAME = "Certificate";

    @Override
    public String getPluginName() {
        return CertificateAuthenticationPlugin.CERTIFICATE_AUTH_NAME;
    }

    @Override
    public AuthenticationPlugin createPlugin() {
        return new CertificateAuthenticationPlugin();
    }
}
