package org.firebirdsql.gds.impl.wire.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.firebirdsql.gds.GDSException;
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

  private final Stage TRANSFER = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      final TaggedClumpletReader serverData = new TaggedClumpletReader(data.getData(), data.getLength());
      if (!serverData.find(sdServerPublicKey))
        throw new GDSAuthException("No server public key found in server data");
      final Bytes publicKeyData = serverData.getBytes();

      if (!serverData.find(sdEncryptedSessionKey))
        throw new GDSAuthException("No server session key found in server data");
      final Bytes sessionKeyData = serverData.getBytes();

      if (!serverData.find(sdSessionKeyInitVector))
        throw new GDSAuthException("No server session key IV found in server data");
      final Bytes sessionKeyIVdata = serverData.getBytes();

      data.clear();
      data.add(AuthMethods.symmetricEncrypt(sspi, successWord.getBytes(), publicKeyData, sessionKeyData, sessionKeyIVdata, certBase64));
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
