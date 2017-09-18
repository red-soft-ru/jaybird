package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.impl.wire.TaggedClumpletReader;

import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author vasiliy
 */
public class AuthFactorServerCertificate extends AuthFactor {

  private static int numberSize = 32;
  private byte[] number;

  protected AuthFactorServerCertificate(AuthSspi sspi) {
    super(AuthFactor.TYPE_SERVER_CERT, sspi);
    setStage(CHALLENGE);
  }

  private final Stage CHALLENGE = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      data.clear();
      number = AuthMethods.generateRandom(null, numberSize);
      data.add(number);
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
      byte[] serverData = data.getData();
      final int serverPublicCertSize = ((serverData[1] & 0xff) << 8) | (serverData[0] & 0xff);
      final byte[] serverPublicCert = Arrays.copyOfRange(serverData, 2, serverPublicCertSize + 2);
      final int signedNumberSize = ((serverData[3 + serverPublicCertSize] & 0xff) << 8) | (serverData[2 + serverPublicCertSize] & 0xff);
      final byte[] signedNumber = Arrays.copyOfRange(serverData, 4 + serverPublicCertSize, 4 + serverPublicCertSize + signedNumberSize);
      return AuthMethods.verifySign(number, serverPublicCert, signedNumber);
    }

    @Override
    public Stage nextStage() {
      return null;
    }
  };

}
