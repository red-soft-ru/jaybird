package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.ClumpletReader;
import org.firebirdsql.gds.impl.wire.ByteBuffer;
import org.firebirdsql.gds.ng.wire.auth.legacy.UnixCrypt;
import sun.misc.BASE64Encoder;

import java.sql.SQLException;
import java.util.Arrays;

import static org.firebirdsql.gds.ClumpletReader.Kind.Tagged;

/**
 * @author roman.kisluhin
 * @version 1.0
 *          Date: 12.10.12
 *          Time: 0:04
 */
public class AuthFactorPassword extends AuthFactor {
  public static final int SALT_LENGTH = 12;
  public static final byte rdSymmetricMethod = 0;
  public static final byte rdHashMethod = 1;
  public static final byte rdCryptData = 2;
  public static final byte rdRandomIV = 3;
  public static final byte rdSessionIV = 4;
  public static final byte rdPasswordEnc = 5;
  public static final byte rdSalt = 6;
  public static final int HASHING_COUNT = 200000;

  private String userName;
  private String password;
  private String passwordEnc;

  private boolean hashExpanded;

  public AuthFactorPassword(AuthSspi sspi) {
    super(TYPE_PASSWORD, sspi);
    setStage(CHALLENGE);
  }

  public void setUserName(final String userName) {
    this.userName = userName == null ? null : userName.toUpperCase();//Server always uppers login when calculating hash
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setPasswordEnc(final String passwordEnc) {
    this.passwordEnc = passwordEnc;
  }

  public void setHashExpanded(boolean expanded) {
    this.hashExpanded = expanded;
  }

  private final Stage CHALLENGE = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      if (userName == null || userName.length() == 0)
        throw new GDSAuthException("User name not specified");
      data.clear();
      data.add(userName.getBytes());
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
      if (passwordEnc == null && password != null)
        passwordEnc = UnixCrypt.crypt(password, "9z").substring(2);

      if (data.getLength() == 0 && passwordEnc != null) {
        // We need to send encrypted password for legacy password verify
        // Usage password hash as session key if session key isn't set yet
        // For more security LegacyHash parameter must be disable after
        // password setting with multifactor authentication
        data.add((byte)0);
        if (sspi.getSessionKey() == null) {
          AuthMethods.createSessionKey(sspi, passwordEnc);
          final byte[] iv = AuthMethods.getIV(sspi.getSessionKey());
          data.add(rdSessionIV);
          data.add((byte)iv.length);
          data.add(iv);
        }

        data.add(rdPasswordEnc);
        data.add((byte)passwordEnc.length());
        data.add(passwordEnc.getBytes());
        return true;
      }

      if (passwordEnc == null) {
        data.clear();
        return true;
      }

      fillMultifactorBuffer(data);

      return true;
    }

    private void fillMultifactorBuffer(final ByteBuffer data) throws GDSAuthException {
      if (password == null || password.length() == 0)
        throw new GDSAuthException("User password not specified");

      // parseSessionKeyData
      final ClumpletReader cr = new ClumpletReader(Tagged, data.getData());

      try {
        if (!cr.find(rdSymmetricMethod))
          throw new GDSAuthException("Can't find data (symmetric method) in server response");
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }
      // todo Kill the param from auth protocol. Replace by algId or OID.
//      final Bytes smBytes = cr.getBytes();
//      try {
//        final String sm = new String(smBytes.getData(), smBytes.getOffset(), smBytes.getLength(), "UTF-16");
//      } catch (UnsupportedEncodingException e) {
//        throw new GDSAuthException("Can't find data (symmetric method) in server response");
//      }

      try {
        if (!cr.find(rdHashMethod))
          throw new GDSAuthException("Can't find data (hash method) in server response");
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }
      // todo Kill the param from auth protocol. Replace by algId or OID.
//      final Bytes hmBytes = cr.getBytes();
//      try {
//        final String hm = new String(hmBytes.getData(), hmBytes.getOffset(), hmBytes.getLength(), "UTF-16");
//      } catch (UnsupportedEncodingException e) {
//        throw new GDSAuthException("Can't find data (hash method) in server response");
//      }

      try {
        if (!cr.find(rdCryptData))
          throw new GDSAuthException("Can't find data (crypt data) in server response");
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }
      final byte[] cryptData;
      try {
        cryptData = cr.getBytes();
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }

      byte[] sessionIV = null;
      try {
        if (cr.find(rdSessionIV)) {
          sessionIV = cr.getBytes();
        }
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }

      try {
        if (!cr.find(rdSalt))
          throw new GDSAuthException("Can't find data (salt) in server response");
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }

      final byte[] saltData;
      try {
        saltData = cr.getBytes();
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      }
//      byte[] bytesSalt = Arrays.copyOfRange(saltData.getData(), saltData.getOffset(), saltData.getOffset() + saltData.getLength());
//      ByteBuffer saltBuffer = new ByteBuffer(0);
//      saltBuffer.add(bytesSalt);
      final byte[] hash = hashMf(userName, password, saltData);

      final Object sessionKey = AuthMethods.createSessionKey(hash);
      final byte[] randomData;
      try {
        if (!cr.find(rdRandomIV))
          throw new GDSAuthException("Can't find data (random IV) in server response");

        final byte[] ivData = cr.getBytes();
        randomData = AuthMethods.decrypt(cryptData, sessionKey, ivData);
      } catch (SQLException e) {
        throw new GDSAuthException(e.getMessage(), e);
      } finally {
        AuthMethods.freeKey(sessionKey);
      }

      if (sessionIV != null) {
        AuthMethods.createSessionKey(sspi, randomData);
        AuthMethods.setIV(sspi.getSessionKey(), sessionIV);
      }

      // hashOfSum
      final int sumDataLen = hash.length + randomData.length;
      final byte[] sumData = new byte[sumDataLen];
      System.arraycopy(randomData, 0, sumData, 0, randomData.length);
      byte[] hashData = hash;
      System.arraycopy(hashData, 0, sumData, randomData.length, hashData.length);

      AuthMethods.hashData(sumData, 1);
      final byte[] hash2 = AuthMethods.hashData(sumData, 1);
      data.clear();

      String hex = toHexString(hash2);
      byte[] bytes = hex.getBytes();
      data.add(bytes);
    }

    private byte[] hashMf(final String userName, final String password, byte[] salt) throws GDSAuthException {
      ByteBuffer buffer = new ByteBuffer(0);
      buffer.add(salt);
      for (int i = buffer.getLength(); i < SALT_LENGTH; i++) {
        buffer.add((byte)'=');
      }

      ByteBuffer oldSalt = new ByteBuffer(0);
      oldSalt.add(buffer.getData());
      final String allData = new String(buffer.getData()) + userName + password;
      buffer.add(userName.getBytes());
      buffer.add(password.getBytes());
      byte[] data = buffer.getData();
      for (int i = 0; i < HASHING_COUNT; i++) {
        data = AuthMethods.hashData(data, 1);
      }

      if (data.length < allData.length() && hashExpanded) {
        byte[] bytes = Arrays.copyOf(data, allData.length());
        Arrays.fill(bytes, data.length, allData.length(), (byte)0xCC);
        data = bytes;
      }

      final byte[] enc64 = new BASE64Encoder().encode(data).getBytes();
      oldSalt.add(enc64);
      return oldSalt.getData();
    }

    @Override
    public Stage nextStage() {
      return RESULT;
    }
  };

  public Stage RESULT = new Stage() {
    @Override
    public boolean stage(final ByteBuffer data) throws GDSAuthException {
      if (data.getLength() != 1)
        throw new GDSAuthException("Error processing " + getFactorName() + " factor");
      return true;
    }

    @Override
    public Stage nextStage() {
      return null;
    }
  };

  private final static char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
      '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  private String toHexString(byte[] bytes) {
    final StringBuilder buf = new StringBuilder(bytes.length);
    for (byte b : bytes) {
      final int i = b & 0xff;
      buf.append(hexDigit[i >> 4]);
      buf.append(hexDigit[i & 0xf]);
    }
    return buf.toString();
  }
}
