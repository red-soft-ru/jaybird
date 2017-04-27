package org.firebirdsql.gds.impl.wire.auth;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 22:48
 */
public class AuthPrivateKeyContext {
  private Object provider;
  private Object keyHandle;

  public AuthPrivateKeyContext(final Object provider, final Object keyHandle) {
    this.provider = provider;
    this.keyHandle = keyHandle;
  }

  public Object getProvHandle() {
    return provider;
  }

  public Object getKeyHandle() {
    return keyHandle;
  }

  public void free(AuthCryptoPlugin p) {
    p.freeKeyHandle(keyHandle);
    p.freeProviderContext(provider);
  }
}
