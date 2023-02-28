package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;

import java.sql.SQLException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 19:33
 */
public abstract class AuthFactor {
  public static final int TYPE_NONE = 0;
  public static final int TYPE_PASSWORD = 1;
  public static final int TYPE_WINDOWS_NTLM = 2;
  public static final int TYPE_CERT_X509 = 4;
  public static final int TYPE_FINGERPRINT = 8;
  public static final int TYPE_EYE = 16;
  public static final int TYPE_SERVER_CERT = 32;
  public static final int TYPE_FORCED_DWORD = 0xFFFFFFFF;

  public static Stage STAGE_OVER = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      return false;
    }

    @Override
    public Stage nextStage() {
      return null;
    }
  };

  private int type;
  private Stage stage;
  protected AuthSspi sspi;

  protected AuthFactor(final int type, final AuthSspi sspi) {
    this.type = type;
    this.sspi = sspi;
  }

  protected void setStage(final Stage stage) {
    this.stage = stage;
  }

  protected AuthFactor(final int type) {
    this.type = type;
  }

  public final boolean request(final ByteBuffer data) throws SQLException {
    if (stage == null)
      throw new SQLException("Error processing factor " + getFactorName() + ".");
    try {
      final boolean result = stage.stage(data);
      stage = stage.nextStage();
      return result;
    } catch (SQLException e) {
      stage = STAGE_OVER;
      try {
        sspi.releaseFailSessionKey();
      } catch (Exception ignored){}
      throw e;
    }
  }

  public int getType() {
    return type;
  }

  public String getFactorName() {
    switch(getType()) {
      case TYPE_CERT_X509:
        return "Certificate";
      case TYPE_PASSWORD:
        return "Password";
      case TYPE_EYE:
        return "Eye";
      case TYPE_FINGERPRINT:
        return "Fingerprint";
      case TYPE_FORCED_DWORD:
        return "Forced dword";
      case TYPE_WINDOWS_NTLM:
        return "Windows NTLM";
    }
    return "Unknown";
  }
}
