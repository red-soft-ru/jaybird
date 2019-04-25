package org.firebirdsql.cryptoapi.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 18.03.2011
 *          Time: 20:29:34
 */
public class Win32Api {

  public static int getLastError() {
    return Native.getLastError();
  }

  public static byte[] convert(byte[] b, int ofs, int len) {
    if (ofs == 0 && len == b.length)
      return b;
    else {
      final byte[] t = new byte[len];
      System.arraycopy(b, ofs, t, 0, len);
      return t;
    }
  }

  public static byte[] getActualData(byte[] data, int length) {
    return convert(data, 0, length);
  }

  public static int byteArrayToInt(final byte[] bytes) {
    final int f = 0xFF;
    return ((bytes[3] & f) << 24) + ((bytes[2] & f) << 16) + ((bytes[1] & f) << 8) + (bytes[0] & f);
  }

  public static String getMessage(int errorCode) {
    final PointerByReference buffer = new PointerByReference();
    Kernel32.INSTANCE.FormatMessage(WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER
        | WinBase.FORMAT_MESSAGE_FROM_SYSTEM
        | WinBase.FORMAT_MESSAGE_IGNORE_INSERTS, null, errorCode, 0/*Win32Api.makeLangId(Win32Api.LANG_NEUTRAL, Win32Api.SUBLANG_DEFAULT)*/, buffer, 0, null);
    try {
      final Pointer value = buffer.getValue();
      return value == null ? null : (!Boolean.getBoolean("w32.ascii") ? value.getWideString(0) : value.getString(0));
    } finally {
      if (buffer.getValue() != null)
        Kernel32.INSTANCE.LocalFree(buffer.getValue());
    }
  }
}
