package org.firebirdsql.gds.impl.wire.auth;

import java.sql.SQLException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 15:52
 */
public class AuthMethods {
  public static byte[] symmetricEncrypt(AuthSspi sspi,
      final byte[] data, final byte[] serverKeyData, final byte[] sessionKeyData, final byte[] sessionKeyIVdata,
      final String certBase64
  ) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    final AuthPrivateKeyContext userKey;
    try {
      userKey = p.getUserKey(certBase64);
    } catch (AuthCryptoException e) {
      throw new SQLException("No private key found for certificate: " + e.getMessage(), e);
    }
    final Object sessionKeyHandle;
    try {
      sessionKeyHandle = p.getSessionPublicKey(sessionKeyData, serverKeyData, userKey);
      sspi.setSessionKey(sessionKeyHandle);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error accessing session key: " + e.getMessage(), e);
    } finally {
      userKey.free(p);
    }

    final byte[] res;
    try {
      p.setIV(sessionKeyHandle, sessionKeyIVdata);
      res = p.encrypt(sessionKeyHandle, data);
      p.setIV(sessionKeyHandle, sessionKeyIVdata); // restore IV for following use (Copy from native driver. No need to do this???)
    } catch (AuthCryptoException e) {
      throw new SQLException("Error encrypting data: " + e.getMessage(), e);
    }
    return res;
  }

  public static byte[] symmetricEncrypt(Object sessionKeyHandle, byte[] data) throws SQLException {
    try {
      return AuthCryptoPlugin.getPlugin().encrypt(sessionKeyHandle, data);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error encrypting data: " + e.getMessage(), e);
    }
  }

  public static byte[] symmetricDecrypt(Object sessionKeyHandle, byte[] data) throws SQLException {
    try {
      return AuthCryptoPlugin.getPlugin().decrypt(sessionKeyHandle, data);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error decrypting data: " + e.getMessage(), e);
    }
  }

  public static byte[] symmetricEncrypt(Object sessionKeyHandle, byte[] data, final boolean isFinal) throws SQLException {
    try {
      return AuthCryptoPlugin.getPlugin().encrypt(sessionKeyHandle, data, isFinal);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error encrypting data: " + e.getMessage(), e);
    }
  }

  public static byte[] symmetricDecrypt(Object sessionKeyHandle, byte[] data, final boolean isFinal) throws SQLException {
    try {
      return AuthCryptoPlugin.getPlugin().decrypt(sessionKeyHandle, data, isFinal);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error decrypting data: " + e.getMessage(), e);
    }
  }

  public static void createSessionKey(AuthSspi sspi, final String data) throws SQLException {
    createSessionKey(sspi, data.getBytes());
  }

  public static void createSessionKey(AuthSspi sspi, byte[] data) throws SQLException {
    final Object sessionKey = createSessionKey(data);
    if (sessionKey != null)
      sspi.setSessionKey(sessionKey);
  }

  public static Object createSessionKey(String data) throws SQLException {
    return createSessionKey(data.getBytes());
  }

  public static Object createSessionKey(byte[] data) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      final Object hash = p.createHash(data);
      try {
        return p.deriveKey(hash, true);
      } finally {
        p.destroyHash(hash);
      }
    } catch (AuthCryptoException e) {
      throw new SQLException("Error creating session key: " + e.getMessage(), e);
    }
  }

  public static void freeKey(Object keyHandle) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    p.freeKeyHandle(keyHandle);
  }

  public static byte[] getIV(final Object sessionKey) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      return p.getIV(sessionKey);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error getting initialization vector: " + e.getMessage(), e);
    }
  }

  public static void setIV(final Object sessionKey, final byte[] dataIV) throws SQLException {
    try {
      AuthCryptoPlugin.getPlugin().setIV(sessionKey, dataIV);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error setting initialization vector: " + e.getMessage(), e);
    }
  }

  public static byte[] decrypt(final byte[] cryptData, final Object sessionKey, final byte[] ivData)
      throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      if (ivData != null)
        p.setIV(sessionKey, ivData);

      final byte[] data = p.decrypt(sessionKey, cryptData);
      if (ivData != null)
        p.setIV(sessionKey, ivData);

      return data;
    } catch (AuthCryptoException e) {
      throw new SQLException("Error setting initialization vector: " + e.getMessage(), e);
    }
  }

  public static byte[] hashData(final byte[] data, final int hashingCount, int hashMethod) throws SQLException {
    try {
      return AuthCryptoPlugin.getPlugin().hashData(data, hashingCount, hashMethod);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error hashing data: " + e.getMessage(), e);
    }
  }

  public static byte[] ccfiEncrypt(final byte[] data) throws SQLException {
    try {
      return AuthCryptoPlugin.getPlugin().ccfiEncrypt(data);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error encrypting data: " + e.getMessage(), e);
    }
  }

  public static byte[] ccfiDecrypt(final AuthPrivateKeyContext userKey, final byte[] data, String certBase64) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    final byte[] res;
    try {
      res = p.ccfiDecrypt(userKey, data, certBase64);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error decrypting data: " + e.getMessage(), e);
    }
    return res;
  }

  public static byte[] ccfiSign(final AuthPrivateKeyContext userKey, final byte[] data, String certBase64, int keySpec) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    final byte[] res;
    try {
      res = p.ccfiSign(userKey, data, certBase64, keySpec);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error signing data: " + e.getMessage(), e);
    }
    return res;
  }

  public static byte[] generateRandom(Object provHandle, int size) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    final byte[] res;
    try {
      res = p.generateRandom(provHandle, size);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error generating random number: " + e.getMessage(), e);
    }
    return res;
  }

  public static boolean verifySign(final byte[] data, final byte[] serverPublicCert, final byte[] signedNumber) throws SQLException {
    final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
    try {
      return p.verifySign(data, serverPublicCert, signedNumber);
    } catch (AuthCryptoException e) {
      throw new SQLException("Error verifying signed message: " + e.getMessage(), e);
    }
  }
}
