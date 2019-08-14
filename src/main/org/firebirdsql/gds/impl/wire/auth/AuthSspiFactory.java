package org.firebirdsql.gds.impl.wire.auth;

/**
 * Creates the Sspi {@link AuthSspi} object to provide multifactor authentication
 * depending on the type of connection used.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class AuthSspiFactory {

    public enum Type {
        TYPE3, // rdb 2.6 and 3.0
        TYPE4 // rdb 4
    }

    public static AuthSspi createAuthSspi(Type type) {
        switch (type) {
        case TYPE3:
            return new AuthSspi3();
        case TYPE4:
        default:
            return new AuthSspi();
        }
    }
}
