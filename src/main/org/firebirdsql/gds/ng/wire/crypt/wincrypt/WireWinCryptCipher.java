package org.firebirdsql.gds.ng.wire.crypt.wincrypt;

import org.firebirdsql.gds.impl.wire.auth.AuthMethods;

import javax.crypto.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Wire_WinCrypt encryption cipher.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0.14
 */
public final class WireWinCryptCipher extends CipherSpi {

    final private static int INIT_VECTOR_LEN = 8;

    private boolean encrypt;
    private Object sessionKey;

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("not implemented yet");
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("not implemented yet");
    }

    @Override
    protected int engineGetBlockSize() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        // Output size is equal input length always
        return inputLen;
    }

    @Override
    protected byte[] engineGetIV() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        this.encrypt = opmode == Cipher.ENCRYPT_MODE;
        try {
            this.sessionKey = AuthMethods.createSessionKey(Arrays.copyOfRange(key.getEncoded(), 0,
                    key.getEncoded().length - this.INIT_VECTOR_LEN));
            AuthMethods.setIV(this.sessionKey, Arrays.copyOfRange(key.getEncoded(),
                    key.getEncoded().length - this.INIT_VECTOR_LEN, key.getEncoded().length));
        } catch (SQLException e) {
            throw new InvalidKeyException("Can't initialize WireWinCrypt cipher", e);
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        if (input == null)
            return null;
        try {
            if (this.encrypt) {
                return AuthMethods.symmetricEncrypt(this.sessionKey, Arrays.copyOfRange(input, inputOffset, inputLen), false);
            } else {
                return AuthMethods.symmetricDecrypt(this.sessionKey, Arrays.copyOfRange(input, inputOffset, inputLen), false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Can't update data using symmetric %s", this.encrypt ? "encryption" : "decryption"), e);
        }
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        byte[] bytes = this.engineUpdate(input, inputOffset, inputLen);
        System.arraycopy(bytes, 0, output, 0, bytes.length);
        // Output buffer can be larger than actual message size,
        // so we return input length as actual output size.
        return inputLen;
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        byte[] bytes;
        try {
            bytes = this.engineUpdate(input, inputOffset, inputLen);
            AuthMethods.freeKey(this.sessionKey);
        } catch (SQLException e) {
            throw new RuntimeException("Can't destroy session key", e);
        }
        return bytes;
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytes = this.engineDoFinal(input, inputOffset, inputLen);
        if (bytes != null) {
            System.arraycopy(bytes, 0, output, 0, bytes.length);
            return inputLen;
        }
        return 0;
    }
}
