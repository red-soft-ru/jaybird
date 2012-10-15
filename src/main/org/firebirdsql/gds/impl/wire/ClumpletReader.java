package org.firebirdsql.gds.impl.wire;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.wire.auth.GDSAuthException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 14:33
 */
public abstract class ClumpletReader {
  public static final int TraditionalDpb = 0;
  public static final int SingleTpb = 1;
  public static final int StringSpb = 2;
  public static final int IntSpb = 3;
  public static final int ByteSpb = 4;
  public static final int Wide = 5;

  protected final byte[] data;
  private final int length;
  private final int offset;
  private int curOffset;

  public ClumpletReader(final byte[] data, final int length) {
    this(data, 0, length);
  }

  public ClumpletReader(
      final byte[] data,
      final int offset,
      final int length
  ) {
    this.offset = offset;
    this.data = data;
    this.length = length;
    curOffset = offset;
  }

  public void rewind() {
    curOffset = offset;
  }

  public int getCurOffset() {
    return curOffset;
  }

  public void setCurOffset(final int curOffset) {
    this.curOffset = curOffset;
  }

  public abstract void moveNext() throws GDSException;

  public boolean isEof() {
    return curOffset > getBufferEnd();
  }

  public int getBufferStart() {
    return offset;
  }

  public int getBufferEnd() {
    return length + offset - 1;
  }

  public int getClumpletSize(boolean wTag, boolean wLength, boolean wData) throws GDSAuthException {
    if (isEof())
      throw new GDSAuthException("End of clumplet data");

    final int cType = getClumpletType(data[curOffset]);

    int rc = wTag ? 1 : 0;
    int lengthSize = 0;
    int dataSize = 0;

    switch (cType) {
      // This form allows clumplets of virtually any size
      case Wide:
        // Check did we receive length component for clumplet
        if (getBufferEnd() - curOffset < 5)
          throw new GDSAuthException("buffer end before end of clumplet - no length component");
        lengthSize = 4;
        dataSize = data[curOffset + 4] & 0xff;
        dataSize <<= 8;
        dataSize += data[curOffset + 3] & 0xff;
        dataSize <<= 8;
        dataSize += data[curOffset + 2] & 0xff;
        dataSize <<= 8;
        dataSize += data[curOffset + 1] & 0xff;
        break;

      // This is the most widely used form
      case TraditionalDpb:
        // Check did we receive length component for clumplet
        if (getBufferEnd() - curOffset < 2)
          throw new GDSAuthException("buffer end before end of clumplet - no length component");
        lengthSize = 1;
        dataSize = data[curOffset + 1] & 0xff;
        break;

      // Almost all TPB parameters are single bytes
      case SingleTpb:
        break;

      // Used in SPB for long strings
      case StringSpb:
        // Check did we receive length component for clumplet
        if (getBufferEnd() - curOffset < 3)
          throw new GDSAuthException("buffer end before end of clumplet - no length component");
        lengthSize = 2;
        dataSize = data[curOffset + 2] & 0xff;
        dataSize <<= 8;
        dataSize += data[curOffset + 1] & 0xff;
        break;

      // Used in SPB for 4-byte integers
      case IntSpb:
        dataSize = 4;
        break;

      // Used in SPB for single byte
      case ByteSpb:
        dataSize = 1;
        break;
    }

    final int total = 1 + lengthSize + dataSize;
    if (curOffset + total > getBufferEnd() + 1)
      throw new GDSAuthException("buffer end before end of clumplet - no length component");
//      final int delta = total - (getBufferEnd() - curOffset);
//      if (delta > dataSize)
//        dataSize = 0;
//      else
//        dataSize -= delta;

    if (wLength) {
      rc += lengthSize;
    }
    if (wData) {
      rc += dataSize;
    }

    return rc;
  }

  public int getClumpLength() throws GDSAuthException {
    return getClumpletSize(false, false, true);
  }

  public Bytes getBytes() throws GDSAuthException {
    return new Bytes(data, curOffset + getClumpletSize(true, true, false), getClumpLength());
  }

  protected abstract int getClumpletType(final byte b);
}
