package org.firebirdsql.util;

/**
 * Interface for accessing Java version specific base64 encoder.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0.7
 */
public interface Base64Encoder {
    /**
     * Encodes all bytes from the specified byte array into a newly-allocated
     * byte array using the Base64 encoding scheme.
     *
     * @param data
     *         byte array for encoding
     * @return byte array after encoding
     */
    byte[] encode(byte[] data);
}
