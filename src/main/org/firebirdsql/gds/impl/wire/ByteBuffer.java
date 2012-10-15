package org.firebirdsql.gds.impl.wire;

import java.io.IOException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 20:09
 */
public class ByteBuffer implements Xdrable {
  private int length = 0;
  private byte[] data;
  private static final byte[] pad = new byte[8];

  public ByteBuffer(final int length) {
    this.length = length;
  }

  private void ensureCapacity(int length) {
    if (data == null) {
      data = new byte[length];
      return;
    }
    if (data.length >= length)
      return;
    final byte[] buf = new byte[length];
    System.arraycopy(data, 0, buf, 0, this.length);
    data = buf;
  }

  public void add(byte b) {
    ensureCapacity(length + 1);
    data[length++] = b;
  }

  public void add(byte[] b) {
    ensureCapacity(length + b.length);
    System.arraycopy(b, 0, data, length, b.length);
    length += b.length;
  }

  @Override
  public int getLength() {
    return length;
  }

  @Override
  public void read(final XdrInputStream in, final int length) throws IOException {
    ensureCapacity(length);
    in.readFully(data, 0, length);
    in.readFully(pad, 0, (4 - length) & 3);
    setLength(length);
  }

  @Override
  public void write(final XdrOutputStream out) throws IOException {
    out.writeInt(length);
    out.write(data, length, (4 - length) & 3);
  }

  public byte get(final int index) {
    ensureCapacity(length);
    return data[index];
  }

  public void clear() {
    length = 0;
  }

  public void setLength(final int length) {
    this.length = length;
  }

  public byte[] getData() {
    ensureCapacity(length);
    return data;
  }
}
