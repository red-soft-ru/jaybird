package org.firebirdsql.gds.impl.wire.auth;

import java.util.ArrayList;
import java.util.List;

import org.firebirdsql.gds.*;
import org.firebirdsql.gds.impl.wire.ByteBuffer;

import static org.firebirdsql.gds.ClumpletReader.Kind.WideTagged;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 19:33
 */
public class AuthSspi {
  protected List<AuthFactor> factors = new ArrayList<AuthFactor>();
  protected int currentFactor = -1;
  protected Object sessionKey;
  private boolean trusted;      // field is not used yet
  private boolean multifactor;  // field is not used yet
  protected boolean freezeSessionKey;
  private boolean securityAuthentication;
  private boolean sessionEncyption;
  private ClumpletReader.Kind clumpletReaderType = WideTagged;
  private boolean skipWireKeyTag;
  private byte[] wireKeyData;

  public AuthSspi() {
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

    final AuthFactor f = factors.get(currentFactor);
    if (!f.request(data))
      return false;

    return true;
  }

  public void fillFactors(final ConnectionParameterBuffer dpb) throws GDSException {
    // Password factor
    if (dpb.hasArgument(ISCConstants.isc_dpb_password) || dpb.hasArgument(ISCConstants.isc_dpb_user_name)) {
      final AuthFactorGostPassword f = new AuthFactorGostPassword(this);
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
      f.setClumpletReaderType(this.clumpletReaderType);
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

    // Server certificate factor
    if (dpb.hasArgument(ISCConstants.isc_dpb_verify_server)) {
      final AuthFactorServerCertificate f = new AuthFactorServerCertificate(this);
      dpb.removeArgument(ISCConstants.isc_dpb_verify_server);
      addFactor(f);
    }

    if (dpb.hasArgument(ISCConstants.isc_dpb_trusted_auth)) {
      trusted = true;
//      dpb.removeArgument(ISCConstants.isc_dpb_trusted_auth);
    }
    if (dpb.hasArgument(ISCConstants.isc_dpb_multi_factor_auth)) {
      multifactor = true;
      dpb.removeArgument(ISCConstants.isc_dpb_multi_factor_auth);
      dpb.addArgument(ISCConstants.isc_dpb_multi_factor_auth, factors.size());
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

  public boolean isSecurityAuthentication() {
    return securityAuthentication;
  }

  public void setSecurityAuthentication(boolean securityAuthentication) {
    this.securityAuthentication = securityAuthentication;
  }

  public boolean isSessionEncyption() {
    return sessionEncyption;
  }

  public void setSessionEncyption(boolean sessionEncyption) {
    this.sessionEncyption = sessionEncyption;
  }

  public void setRepositoryPin(String pin) throws GDSAuthException {
    AuthCryptoPlugin.getPlugin().setRepositoryPin(pin);
  }

  public void setProviderID(int providerID) throws GDSAuthException {
    try {
      AuthCryptoPlugin.getPlugin().setProviderID(providerID);
    } catch (AuthCryptoException e) {
      throw new GDSAuthException(String.format("Can't initialize provider with provider type %s", providerID), e);
    }
  }

  public void setClumpletReaderType(ClumpletReader.Kind type) {
    this.clumpletReaderType = type;
  }

  public boolean isSkipWireKeyTag() {
    return skipWireKeyTag;
  }

  public void setSkipWireKeyTag(boolean skip) {
    this.skipWireKeyTag = skip;
  }

  public void setWireKeyData(byte[] wireKeyData) {
    this.wireKeyData = wireKeyData;
  }

  public byte[] getWireKeyData() {
    return wireKeyData;
  }
}
