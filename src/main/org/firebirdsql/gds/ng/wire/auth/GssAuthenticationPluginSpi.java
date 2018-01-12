package org.firebirdsql.gds.ng.wire.auth;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class GssAuthenticationPluginSpi implements AuthenticationPluginSpi {
  @Override
  public String getPluginName() {
    return GssAuthenticationPlugin.GSS_AUTH_NAME;
  }

  @Override
  public AuthenticationPlugin createPlugin() {
    return new GssAuthenticationPlugin();
  }
}
