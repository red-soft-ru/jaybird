package org.firebirdsql.cryptoapi.windows;

import com.sun.jna.Memory;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 10.09.2011
 *          Time: 12:00:38
 */
public class JnaUtils {
  public static Pointer memory(byte... buf) {
    final Pointer memory = new Memory(buf.length);
    memory.write(0, buf, 0, buf.length);
    return memory;
  }

  public static String toString(Pointer pbData, int cbData) {
    if (pbData == null)
      return null;
    final StringBuilder buf = new StringBuilder();
    byte[] data = pbData.getByteArray(0, cbData);
    for (byte b : data) {
      final int i = b & 0xff;
      buf.append(alignStringLeft(Integer.toHexString(i), 2, '0'));
    }
    return buf.toString();
  }

  public static void init() {
    final String encoding = System.getProperty("jna.encoding");
    if (encoding == null) {
      System.setProperty("jna.encoding", Platform.isWindows() ? "Cp1251" : "UTF8");
    }
  }

  public static StringBuilder alignStringLeft(String str, int length, char c) {
    final StringBuilder buf = str == null ? new StringBuilder() : new StringBuilder(str);
    for (int i = length - buf.length(); i > 0; i--) buf.insert(0, c);
    return buf;
  }
}
