/*
 * Firebird Open Source JDBC Driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.gds.impl.argument;

import org.firebirdsql.encodings.Encoding;
import org.firebirdsql.gds.ParameterBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import java.util.Arrays;

/**
 * {@link Argument} implementation for byte arrays.
 * <p>
 * This implementation supports byte arrays up to length of the argument type.
 * </p>
 */
public final class ByteArrayArgument extends TypedArgument {

    @Serial
    private static final long serialVersionUID = -8636439991275911102L;
    
    private final byte[] value;

    /**
     * Initializes an instance of ByteArrayArgument.
     *
     * @param type
     *        Parameter type
     * @param value
     *        Byte array with a maximum length defined by {@code argumentType}.
     */
    public ByteArrayArgument(int type, ArgumentType argumentType, byte[] value) {
        super(type, argumentType);
        if (argumentType != ArgumentType.TraditionalDpb && argumentType != ArgumentType.Wide
                && argumentType != ArgumentType.StringSpb) {
            throw new IllegalArgumentException(
                    "ByteArrayArgument only works for TraditionalDpb, Wide, or StringSpb was: " + argumentType);
        }
        if (value == null) {
            throw new IllegalArgumentException("byte array value should not be null");
        }
        if (value.length > argumentType.getMaxLength()) {
            throw new IllegalArgumentException(
                    String.format("byte array value should not be longer than %d bytes, length was %d",
                            argumentType.getMaxLength(), value.length));
        }
        this.value = value;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(getType());
        argumentType.writeLength(value.length, outputStream);
        outputStream.write(value);
    }

    @Override
    public int getLength() {
        return 1 + argumentType.getLengthSize() + value.length;
    }

    @Override
    public int getValueAsInt() {
        if (value.length == 1) {
            return value[0];
        } else {
            throw new UnsupportedOperationException("This method is not supported for byte arrays with length > 1");
        }
    }

    @Override
    public void copyTo(ParameterBuffer buffer, Encoding encoding) {
        buffer.addArgument(getType(), value.clone());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof final ByteArrayArgument otherByteArrayArgument)) return false;
        return this.getType() == otherByteArrayArgument.getType()
                && Arrays.equals(this.value, otherByteArrayArgument.value);
    }

    @Override
    public int hashCode() {
        int result = 23;
        result = 41 * result + getType();
        result = 41 * result + Arrays.hashCode(value);
        return result;
    }
}
