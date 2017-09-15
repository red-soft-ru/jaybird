package org.firebirdsql.gds.impl.wire.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.Bytes;
import org.firebirdsql.gds.impl.wire.TaggedClumpletReader;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 22:56
 */
public class AuthFactorCertificate extends AuthFactor {
  public static final int sdServerPublicKey = 0;
  public static final int sdEncryptedSessionKey = 1;
  public static final int sdSessionKeyInitVector = 2;
  public static final int sdServerCertificate = ISCConstants.isc_spb_skip_data;
  public static final int sdRandomNumber = ISCConstants.isc_dpb_certificate_body;
  private static final String successWord = "success\0";

  private String certBase64;

  private final Stage CHALLENGE = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) {
      data.clear();
      data.add(certBase64.getBytes());
      return true;
    }

    @Override
    public Stage nextStage() {
      return TRANSFER;
    }
  };

  public Stage RESULT = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      if (data.getLength() != 1)
        throw new GDSAuthException("Error processing " + getFactorName() + " factor");
      if (data.get(0) == 0)
        throw new GDSAuthException(ISCConstants.isc_login, "Bad " + getFactorName() + " factor");
      return true;
    }

    @Override
    public Stage nextStage() {
      return null;
    }
  };

  private final Stage TRANSFER = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      final TaggedClumpletReader serverData = new TaggedClumpletReader(data.getData(), data.getLength());
      if (!serverData.find(sdRandomNumber))
        throw new GDSAuthException("No random number found in server data");

      byte[] number = AuthMethods.ccfiDecrypt(serverData.getBytes().bytes(), certBase64);
      byte[] signData = AuthMethods.ccfiSign(number, certBase64);
      byte[] b = new byte[2];
      b[0] = (byte)(signData.length & 0xff);
      b[1] = (byte)((signData.length >> 8) & 0xff);
      data.clear();
      data.add(b);
      data.add(signData);

      return true;
    }

    @Override
    public Stage nextStage() {
      return RESULT;
    }
  };

  protected AuthFactorCertificate(AuthSspi sspi) {
    super(AuthFactor.TYPE_CERT_X509, sspi);
    setStage(CHALLENGE);
  }

  public void setCertBase64(final String certBase64) {
    this.certBase64 = certBase64;
  }

  public void loadFromFile(String filePath) throws GDSException {
    final byte buf[] = new byte[4096];
    final StringBuilder res = new StringBuilder();
    try {
      final InputStream is = new FileInputStream(filePath);
      try {
        int c;
        while ((c = is.read(buf)) > 0) {
          res.append(new String(buf, 0, c));
        }
        setCertBase64(res.toString());
      } finally {
        try {
          is.close();
        } catch (IOException ignored) {
        }
      }
    } catch (IOException e) {
      throw new GDSException("Error reading certificate from file " + filePath + ": " + e.getMessage());
    }
  }
}
