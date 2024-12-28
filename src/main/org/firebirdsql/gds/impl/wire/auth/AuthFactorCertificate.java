package org.firebirdsql.gds.impl.wire.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.firebirdsql.gds.ClumpletReader;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.jaybird.fb.constants.DpbItems;

import static org.firebirdsql.gds.ClumpletReader.Kind.WideTagged;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 11.10.12
 *          Time: 22:56
 */
public class AuthFactorCertificate extends AuthFactor {
  private int sdRandomNumber = 1;
  private int sdWireKey = 2;
  private String certBase64;
  private ClumpletReader.Kind clumpletReaderType;

  public static int ksExchange = 1;
  public static int ksSignature = 2;

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
    public boolean stage(final ByteBuffer data) throws SQLException {
      if (data.getLength() != 1)
        throw new SQLException("Error processing " + getFactorName() + " factor");
      if (data.get(0) == 0)
        throw FbExceptionBuilder.forException(ISCConstants.isc_login).cause(
              new Throwable("Bad " + getFactorName() + " factor")).toSQLException();
      return true;
    }

    @Override
    public Stage nextStage() {
      return null;
    }
  };

  private final Stage TRANSFER = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws SQLException {
      if (sspi instanceof AuthSspi3) {
        sdRandomNumber = DpbItems.isc_dpb_certificate_body;
        sdWireKey = 1;
      }
      final ClumpletReader serverData = new ClumpletReader(clumpletReaderType, data.getData());
      if (!serverData.find(sdRandomNumber))
        throw new SQLException("No random number found in server data");
      final byte[] encryptNumber;
      encryptNumber = serverData.getBytes();

      final AuthCryptoPlugin p = AuthCryptoPlugin.getPlugin();
      final AuthPrivateKeyContext userKey; // cache the user key context to avoid password dialog appearing 2 times
      try {
        userKey = p.getUserKey(certBase64);
      } catch (AuthCryptoException e) {
        throw new SQLException("No private key found for certificate: " + e.getMessage(), e);
      }
      final byte[] signData;
      final byte[] wireKeyData;
      try {
        final byte[] number = AuthMethods.ccfiDecrypt(userKey, encryptNumber, certBase64);
        signData = AuthMethods.ccfiSign(userKey, number, certBase64, ksExchange);
        if (!sspi.isSkipWireKeyTag()) { // skip sdWireKey for old protocols
          if (serverData.find(sdWireKey)) {
            wireKeyData = serverData.getBytes();
            sspi.setWireKeyData(AuthMethods.ccfiDecrypt(userKey, wireKeyData, certBase64));
          }
        }
      } finally {
        userKey.free(p);
      }
      final byte[] b = new byte[2];
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

  public AuthFactorCertificate(AuthSspi sspi) {
    super(AuthFactor.TYPE_CERT_X509, sspi);
    setStage(CHALLENGE);
    this.clumpletReaderType = sspi.getClumpletReaderType();
  }

  public void setCertBase64(final String certBase64) {
    this.certBase64 = certBase64;
  }

  public void loadFromFile(String filePath) throws SQLException {
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
      throw new SQLException("Error reading certificate from file " + filePath + ": " + e.getMessage());
    }
  }

  public void setSdRandomNumber(int sdRandomNumber) {
    this.sdRandomNumber = sdRandomNumber;
  }
}
