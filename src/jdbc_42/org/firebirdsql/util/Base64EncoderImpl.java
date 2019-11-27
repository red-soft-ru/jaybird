package org.firebirdsql.util;

import java.util.Base64;

/**
 * Java 8 and higher implementation of {@link Base64Encoder}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class Base64EncoderImpl implements Base64Encoder {

    @SuppressWarnings("Since15")
    @Override
    public byte[] encode(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

}
