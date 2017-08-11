package org.firebirdsql.gds.impl.wire;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.auth.GDSAuthException;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 14:19
 */
public class TaggedClumpletReader extends ClumpletReader {
  public TaggedClumpletReader(final byte[] data, final int length) {
    super(data, length);
  }

  public TaggedClumpletReader(final byte[] data, final int offset, final int length) {
    super(data, offset, length);
  }

  @Override
  public void rewind() {
    setCurOffset(getBufferStart() + 1);
  }

  @Override
  public void moveNext() throws GDSAuthException {
    if (isEof())
      return;   // no need to raise useless exceptions
    final int cs = getClumpletSize(true, true, true);
    setCurOffset(getCurOffset() + cs);
  }

  public boolean find(int tag) throws GDSAuthException {
    final int co = getCurOffset();
    for (rewind(); !isEof(); moveNext()) {
      if (tag == getClumpTag())
        return true;
    }
    setCurOffset(co);
    return false;
  }

  public int getClumpTag() throws GDSAuthException {
    if (isEof())
      throw new GDSAuthException("ClumpletReader>> read past EOF");
    return data[getCurOffset()] & 0xff;
  }

  @Override
  protected int getClumpletType(final byte b) {
    int tag = b & 0xff;
    switch (tag)
    {
      case ISCConstants.isc_dpb_certificate_body:
      case ISCConstants.isc_spb_skip_data:
        return Wide;
    }
    return TraditionalDpb;
  }
}
