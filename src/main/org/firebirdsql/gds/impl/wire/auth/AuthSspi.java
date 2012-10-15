package org.firebirdsql.gds.impl.wire.auth;

import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 19:33
 */
public class AuthSspi {
  private List<AuthFactor> factors = new ArrayList<AuthFactor>();
  private int currentFactor = -1;
  private Object sessionKey;
  private boolean trusted;      // field is not used yet
  private boolean multifactor;  // field is not used yet
  private boolean freezeSessionKey;

  public AuthSspi(final DatabaseParameterBuffer dpb) {
    // fill correct dpb params
    if (dpb.hasArgument(ISCConstants.isc_dpb_trusted_auth)) {
      trusted = true;
      dpb.removeArgument(ISCConstants.isc_dpb_trusted_auth);
      dpb.addArgument(ISCConstants.isc_dpb_trusted_auth, new byte[0]);
    }
    if (dpb.hasArgument(ISCConstants.isc_dpb_multi_factor_auth)) {
      multifactor = true;
      dpb.removeArgument(ISCConstants.isc_dpb_multi_factor_auth);
      dpb.addArgument(ISCConstants.isc_dpb_multi_factor_auth, new byte[0]);
    }
    // set the current factor
    currentFactor = 0;
  }

  public void addFactor(AuthFactor factor) {
    factors.add(factor);
  }

  public boolean request(ByteBuffer data) throws GDSAuthException {
    if (factors.isEmpty())
      return false;

    // Skip first call for leaving the clumplet less 256 byte
    if (currentFactor == -1) {
      currentFactor = 0;
      return true;
    }

    final int dataCount = data.getLength();
    if (dataCount != 0) {
      final int type = data.get(dataCount - 1);
      if (type == AuthFactor.TYPE_NONE) {
        if (currentFactor >= factors.size())
          throw new GDSAuthException("Error multi factor authentication");
        if (sessionKey != null)
          freezeSessionKey = true;
        // Current factor was passed. Move to the next if possible
        if (++currentFactor >= factors.size()) {
          data.clear();
          // Stop factors data exchanging
          data.add((byte)AuthFactor.TYPE_NONE);
          return true;
        }
      }
      else
        data.setLength(dataCount - 1);
    }

    final AuthFactor f = factors.get(currentFactor);
    if (!f.request(data))
      return false;

    data.add((byte)f.getType());
    return true;
  }

  public void fillFactors(final DatabaseParameterBuffer dpb) throws GDSException {
    // Password factor
    if (dpb.hasArgument(ISCConstants.isc_dpb_user_name)) {
      final AuthFactorPassword f = new AuthFactorPassword(this);
      f.setUserName(dpb.getArgumentAsString(ISCConstants.isc_dpb_user_name));
      if (dpb.hasArgument(ISCConstants.isc_dpb_password)) {
        f.setPassword(dpb.getArgumentAsString(ISCConstants.isc_dpb_password));
        dpb.removeArgument(ISCConstants.isc_dpb_password);
      }
      if (dpb.hasArgument(ISCConstants.isc_dpb_password_enc)) {
        f.setPasswordEnc(dpb.getArgumentAsString(ISCConstants.isc_dpb_password_enc));
        dpb.removeArgument(ISCConstants.isc_dpb_password_enc);
      }
      addFactor(f);
    }

    // Certificate factor
    if (dpb.hasArgument(ISCConstants.isc_dpb_certificate) || dpb.hasArgument(ISCConstants.isc_dpb_certificate_base64)) {
      final AuthFactorCertificate f = new AuthFactorCertificate(this);
      if (dpb.hasArgument(ISCConstants.isc_dpb_certificate)) {
        final String filePath = dpb.getArgumentAsString(ISCConstants.isc_dpb_certificate);
        f.loadFromFile(filePath);
        dpb.removeArgument(ISCConstants.isc_dpb_certificate);
      } else {
        final String cert = dpb.getArgumentAsString(ISCConstants.isc_dpb_certificate_base64);
        f.setCertBase64(cert);
        dpb.removeArgument(ISCConstants.isc_dpb_certificate_base64);
      }
      addFactor(f);
    }
  }

  public Object getSessionKey() {
    return sessionKey;
  }

  public void setSessionKey(final Object sessionKey) throws GDSAuthException {
    releaseSessionKey();
    this.sessionKey = sessionKey;
  }

  void releaseFailSessionKey() throws GDSAuthException {
    if (sessionKey != null && !freezeSessionKey) {
      releaseSessionKey();
    }
  }

  private void releaseSessionKey() throws GDSAuthException {
    if (sessionKey == null)
      return;
    AuthCryptoPlugin.getPlugin().freeKeyHandle(sessionKey);
    sessionKey = null;
  }

  public void free() throws GDSAuthException {
    releaseSessionKey();
  }

  @Override
  protected void finalize() throws Throwable {
    free();
    super.finalize();
  }
}
