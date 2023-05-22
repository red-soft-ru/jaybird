package org.firebirdsql.gds.ng.wire.crypt.wincrypt;

import org.firebirdsql.gds.ng.FbExceptionBuilder;
import org.firebirdsql.gds.ng.wire.auth.ClientAuthBlock;
import org.firebirdsql.gds.ng.wire.crypt.CryptSessionConfig;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionIdentifier;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionInitInfo;
import org.firebirdsql.gds.ng.wire.crypt.EncryptionPlugin;
import org.firebirdsql.util.SQLExceptionChainBuilder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.*;
import java.sql.SQLException;
import java.util.Arrays;

import static org.firebirdsql.gds.JaybirdErrorCodes.*;

/**
 * Wire_WinCrypt encryption plugin (the wire encryption with CryptoPro provided in Red Database 3).
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0.14
 */
public class WireWinCryptEncryptionPlugin implements EncryptionPlugin {

    private final CryptSessionConfig cryptSessionConfig;

    public WireWinCryptEncryptionPlugin(CryptSessionConfig cryptSessionConfig) {
        this.cryptSessionConfig = cryptSessionConfig;
    }

    @Override
    public EncryptionIdentifier encryptionIdentifier() {
        return WireWinCryptEncryptionPluginSpi.WIRE_WINCRYPT_ID;
    }

    @Override
    public EncryptionInitInfo initializeEncryption() {
        SQLExceptionChainBuilder<SQLException> chainBuilder = new SQLExceptionChainBuilder<>();
        Cipher encryptionCipher = createEncryptionCipher(cryptSessionConfig.encryptKey(), chainBuilder);
        Cipher decryptionCipher = createDecryptionCipher(cryptSessionConfig.decryptKey(), chainBuilder);

        if (chainBuilder.hasException()) {
            return EncryptionInitInfo.failure(encryptionIdentifier(), chainBuilder.getException());
        }
        return EncryptionInitInfo.success(encryptionIdentifier(), encryptionCipher, decryptionCipher);
    }

    private Cipher createEncryptionCipher(byte[] key, SQLExceptionChainBuilder<SQLException> chainBuilder) {
        return createCipher(Cipher.ENCRYPT_MODE, key, chainBuilder);
    }

    private Cipher createDecryptionCipher(byte[] key, SQLExceptionChainBuilder<SQLException> chainBuilder) {
        return createCipher(Cipher.DECRYPT_MODE, key, chainBuilder);
    }

    private Cipher createCipher(int mode, byte[] key, SQLExceptionChainBuilder<SQLException> chainBuilder) {
        try {
            return createCipher(mode, key);
        } catch (SQLException e) {
            chainBuilder.append(e);
            return null;
        }
    }

    private Cipher createCipher(int mode, byte[] key) throws SQLException {
        try {
            // Important! Since JCE policy prohibits the use of unsigned ciphers,
            // we initialize the object through reflection
            final Constructor<Cipher>[] constructors = (Constructor<Cipher>[]) Cipher.class.getDeclaredConstructors();
            final Constructor<Cipher> constructor = Arrays.stream(constructors)
                    .filter(cipherConstructor -> cipherConstructor.getParameterCount() == 2 &&
                            javax.crypto.CipherSpi.class.isAssignableFrom(cipherConstructor.getParameterTypes()[0]) &&
                            java.lang.String.class.isAssignableFrom(cipherConstructor.getParameterTypes()[1]))
                    .findAny().get();
            constructor.setAccessible(true);
            Cipher instance = constructor.newInstance(new WireWinCryptCipher(), "WireWinCrypt");
            SecretKeySpec wireWinCryptKey = new SecretKeySpec(key, "WireWinCryptKey");
            instance.init(mode, wireWinCryptKey);
            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new FbExceptionBuilder().nonTransientException(jb_cryptAlgorithmNotAvailable)
                    .messageParameter(encryptionIdentifier().toString())
                    .cause(e).toFlatSQLException();
        } catch (InvalidKeyException | IllegalArgumentException e) {
            throw new FbExceptionBuilder().nonTransientException(jb_cryptInvalidKey)
                    .messageParameter(encryptionIdentifier().toString())
                    .cause(e).toFlatSQLException();
        }
    }
}
