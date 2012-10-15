package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.impl.wire.Bytes;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 15:40
 */
public abstract class AuthCryptoPlugin {
  private static AuthCryptoPlugin _plugin;

  public static AuthCryptoPlugin getPlugin() throws GDSAuthException {
    if (_plugin == null)
      throw new GDSAuthException("Not crypto plugin installed. Please, register with AuthCryptoPlugin#register(AuthCryptoPlugin).");
    return _plugin;
  }

  public static void register(AuthCryptoPlugin plugin) {
    _plugin = plugin;
  }

  public abstract Object getSessionPublicKey(final Bytes publicKeyData, Bytes exchangeKeyData, final AuthPrivateKeyContext userKey)
      throws AuthCryptoException;

  public abstract AuthPrivateKeyContext getUserKey(final String certBase64) throws AuthCryptoException;

  public abstract void setIV(final Object keyHandle, final Bytes iVdata) throws AuthCryptoException;

  public abstract byte[] getIV(final Object keyHandle) throws AuthCryptoException;

  public abstract void freeKeyHandle(final Object keyHandle);

  public abstract void freeProviderContext(final Object provHandle);

  public abstract byte[] encrypt(final Object keyHandle, final byte[] data) throws AuthCryptoException;

  public abstract byte[] decrypt(final Object keyHandle, final byte[] data) throws AuthCryptoException;

  public abstract Object createHash(final byte[] data) throws AuthCryptoException;

  public abstract boolean destroyHash(Object hashHandle);

  public abstract byte[] hashData(final byte[] data, final int hashingCount) throws AuthCryptoException;

  public abstract Object deriveKey(final Object hashHandle, boolean exportable) throws AuthCryptoException;
}
