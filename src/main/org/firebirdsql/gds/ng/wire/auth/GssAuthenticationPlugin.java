package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.sql.SQLException;

/**
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class GssAuthenticationPlugin implements AuthenticationPlugin {

  private static final Logger log = LoggerFactory.getLogger(LegacyAuthenticationPlugin.class);

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
      log.debug("Gss phase 1");
      firstStage = false;
      return AuthStatus.AUTH_MORE_DATA;
    }
    log.debug("Gss phase 2");
    gssClient = new GSSClient(serverData);
    clientData = gssClient.getToken();

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
  public String toString() {
    return getClass().getSimpleName() + " : " + getName();
  }
}
