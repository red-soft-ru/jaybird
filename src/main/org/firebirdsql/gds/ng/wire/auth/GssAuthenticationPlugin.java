package org.firebirdsql.gds.ng.wire.auth;

import org.ietf.jgss.GSSException;

import java.sql.SQLException;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class GssAuthenticationPlugin implements AuthenticationPlugin {

  private static final System.Logger log = System.getLogger(GssAuthenticationPlugin.class.getName());

  public static final String GSS_AUTH_NAME = "Gss";

  private GSSClient gssClient;
  private byte[] clientData;
  private byte[] serverData;

  private boolean firstStage = true;

  @Override
  public String getName() {
    return GSS_AUTH_NAME;
  }

  @Override
  public AuthStatus authenticate(ClientAuthBlock clientAuthBlock) throws SQLException {
    if (firstStage) {
      log.log(System.Logger.Level.DEBUG, "Gss phase 1");
      firstStage = false;
      if (clientAuthBlock.getLogin() != null || clientAuthBlock.getCertificate() != null)
        return AuthStatus.AUTH_CONTINUE;

      return AuthStatus.AUTH_MORE_DATA;
    }
    log.log(System.Logger.Level.DEBUG, "Gss phase 2");
    gssClient = new GSSClient(serverData);
    try {
      clientData = gssClient.getToken();
    } catch (GSSException e) {
      throw new SQLException(e);
    }

    return AuthStatus.AUTH_SUCCESS;
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
    throw new SQLException("GssAuthenticationPlugin cannot generate a session key");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " : " + getName();
  }
}
