package org.firebirdsql.cryptoapi.cryptopro;

import java.nio.ByteBuffer;

import com.sun.jna.Pointer;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.cryptoapi.windows.advapi.Advapi;

/**
 * @author Pavel Maslov
 * Date: 12.10.2011
 * Time: 17:21:49
 */

public class RandomUtil {
  public static ByteBuffer createRandomBuffer(int bufSize) throws CryptoException {
    final Pointer prov = CryptoProProvider.acquireContext();
    try {
      ByteBuffer buffer = Advapi.cryptGenRandom(prov, bufSize);
      if (buffer == null) {
        final int error = Advapi.getLastError();
        throw new CryptoException("Error random value generating. Error code: " + error, error);
      }
      return buffer;
    } finally {
      Advapi.cryptReleaseContext(prov);
    }
  }

  public static ByteBuffer createRandomBuffer(Pointer provider, int bufSize) throws CryptoException {
    ByteBuffer buffer = Advapi.cryptGenRandom(provider, bufSize);
    if (buffer == null) {
      final int error = Advapi.getLastError();
      throw new CryptoException("Error random value generating. Error code: " + error, error);
    }
    return buffer;
  }
}
