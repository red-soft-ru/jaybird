package org.firebirdsql.util;

import sun.misc.BASE64Encoder;

/**
 * Java 7 implementation of {@link Base64Encoder}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class Base64EncoderImpl implements Base64Encoder {

    @Override
    public byte[] encode(byte[] data) {
        return new BASE64Encoder().encode(data).getBytes();
    }

}
