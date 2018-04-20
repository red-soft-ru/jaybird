package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.encodings.EncodingFactory;
import org.firebirdsql.gds.ng.FbMessageBuilder;
import org.firebirdsql.gds.ng.FbMessageMetadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import static org.firebirdsql.gds.ISCConstants.SQL_INT64;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public abstract class AbstractFbMessageBuilder<E extends FbMessageMetadata> implements FbMessageBuilder {

    private FbMessageMetadata metadata;
    private ByteBuffer buffer = null;
    private final LittleEndianDatatypeCoder datatypeCoder = new LittleEndianDatatypeCoder(EncodingFactory.createInstance(StandardCharsets.UTF_8));
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private int messageAlign;
    private int messageLength;

    protected AbstractFbMessageBuilder(E messageMetadata) throws FbException {
        this.metadata = messageMetadata;
        this.messageLength = this.metadata.getMessageLength();
        buffer = ByteBuffer.allocate(this.messageLength);
        this.messageAlign = this.metadata.getAlignedLength();

    }

    static int align(int target, int alignment) {
        return (((target) + alignment - 1) & ~(alignment - 1));
    }

    @Override
    public void addSmallint(int index, short value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeShort(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addInteger(int index, int value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeInt(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addBigint(int index, long value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeLong(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addFloat(int index, float value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeFloat(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addDouble(int index, double value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);
        int type = metadata.getType(index);
        byte[] bytes = null;
        if (type == SQL_INT64) {
            BigDecimal decimal = BigDecimal.valueOf(value);
            BigInteger integer = decimal.unscaledValue();
            bytes = datatypeCoder.encodeLong(integer.longValue());
        }
        else
            bytes = datatypeCoder.encodeDouble(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addDecfloat16(int index, BigDecimal value) throws FbException {
//        int nullOffset = metadata.getNullOffset(index);
//        int offset = metadata.getOffset(index);
//
//        byte[] bytes = datatypeCoder.encodeDecimal64(value);
//        byte[] nullShort = datatypeCoder.encodeShort(0);
//
//        buffer.position(offset);
//        buffer.put(bytes);
//        buffer.position(nullOffset);
//        buffer.put(nullShort);
    }

    @Override
    public void addDecfloat34(int index, BigDecimal value) throws FbException {
//        int nullOffset = metadata.getNullOffset(index);
//        int offset = metadata.getOffset(index);
//
//        byte[] bytes = datatypeCoder.encodeDecimal128(value);
//        byte[] nullShort = datatypeCoder.encodeShort(0);
//
//        buffer.position(offset);
//        buffer.put(bytes);
//        buffer.position(nullOffset);
//        buffer.put(nullShort);
    }

    @Override
    public void addBlob(int index, long blobId) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeLong(blobId);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addBoolean(int index, boolean value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeBoolean(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addDate(int index, Date value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeDate(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addTime(int index, Time value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeTime(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addTimestamp(int index, Timestamp value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeTimestamp(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addChar(int index, String value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeString(value);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public void addVarchar(int index, String value) throws FbException {
        int nullOffset = metadata.getNullOffset(index);
        int offset = metadata.getOffset(index);

        byte[] bytes = datatypeCoder.encodeString(value);
        byte[] encodeShort = datatypeCoder.encodeShort(bytes.length);
        byte[] nullShort = datatypeCoder.encodeShort(0);

        buffer.position(offset);
        buffer.put(encodeShort);
        offset += encodeShort.length;
        buffer.position(offset);
        buffer.put(bytes);
        buffer.position(nullOffset);
        buffer.put(nullShort);
    }

    @Override
    public byte[] getData() throws FbException {
        return buffer.array();
    }

    @Override
    public void clear() throws FbException {
        buffer.clear();
    }

    @Override
    public void addStreamData(byte[] data) throws IOException {

        stream.write(data);

        int align = align(messageLength, messageAlign);
        byte[] shift = ByteBuffer.allocate(Math.abs(data.length - align)).array();
        stream.write(shift);
    }

    @Override
    public void clearStream() throws FbException {
        stream.reset();
    }

    @Override
    public byte[] getStreamData() throws FbException {
       return stream.toByteArray();
    }
}
