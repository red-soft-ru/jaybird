package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.impl.wire.Bytes;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 15:52
 */
public class AuthMethods {
  public static byte[] symmetricEncrypt(AuthSspi sspi,
      final byte[] data, final Bytes serverKeyData, final Bytes sessionKeyData, final Bytes sessionKeyIVdata,
      final String certBase64
  ) throws GDSAuthException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    final AuthPrivateKeyContext userKey;
    try {
      userKey = p.getUserKey(certBase64);
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("No private key found for certificate: " + e.getMessage());
    }
    final Object sessionKeyHandle;
    try {
      sessionKeyHandle = p.getSessionPublicKey(sessionKeyData, serverKeyData, userKey);
      sspi.setSessionKey(sessionKeyHandle);
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error accessing session key: " + e.getMessage());
    } finally {
      userKey.free(p);
    }

    final byte[] res;
    try {
      p.setIV(sessionKeyHandle, sessionKeyIVdata);
      res = p.encrypt(sessionKeyHandle, data);
      p.setIV(sessionKeyHandle, sessionKeyIVdata); // restore IV for following use (Copy from native driver. No need to do this???)
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error encrypting data: " + e.getMessage());
    }
    return res;
  }

  public static void createSessionKey(AuthSspi sspi, final String data) throws GDSAuthException {
    final Object sessionKey = createSessionKey(data);
    if (sessionKey != null)
      sspi.setSessionKey(sessionKey);
  }

  public static Object createSessionKey(final String data) throws GDSAuthException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      final Object hash = p.createHash(data.getBytes());
      try {
        return p.deriveKey(hash, true);
      } finally {
        p.destroyHash(hash);
      }
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error creating session key: " + e.getMessage());
    }
  }

  public static void freeKey(Object keyHandle) throws GDSAuthException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    p.freeKeyHandle(keyHandle);
  }

  public static byte[] getIV(final Object sessionKey) throws GDSAuthException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      return p.getIV(sessionKey);
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error getting initialization vector: " + e.getMessage());
    }
  }

  public static void setIV(final Object sessionKey, final Bytes dataIV) throws GDSAuthException {
    try {
      AuthCryptoPlugin.getPlugin().setIV(sessionKey, dataIV);
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error setting initialization vector: " + e.getMessage());
    }
  }

  public static byte[] decrypt(final Bytes cryptData, final Object sessionKey, final Bytes ivData)
      throws GDSAuthException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      if (ivData != null)
        p.setIV(sessionKey, ivData);

      final byte[] data = p.decrypt(sessionKey, cryptData.bytes());
      if (ivData != null)
        p.setIV(sessionKey, ivData);

      return data;
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error setting initialization vector: " + e.getMessage());
    }
  }

  public static byte[] hashData(final byte[] data, final int hashingCount) throws GDSAuthException {
    try {
      return AuthCryptoPlugin.getPlugin().hashData(data, hashingCount);
    } catch (AuthCryptoException e) {
      throw new GDSAuthException("Error hashing data: " + e.getMessage());
    }
  }
}
