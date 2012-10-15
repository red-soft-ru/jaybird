package org.firebirdsql.gds.impl.wire;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 15:45
 */
public class Bytes {
  private byte[] data;
  private int offset;
  private int length;

  public Bytes(final byte[] data, final int offset, final int length) {
    this.data = data;
    this.offset = offset;
    this.length = length;
  }

  public byte[] getData() {
    return data;
  }

  public int getOffset() {
    return offset;
  }

  public int getLength() {
    return length;
  }

  public byte[] bytes() {
    final byte[] buf = new byte[length];
    System.arraycopy(data, offset, buf, 0, length);
    return buf;
  }
}
