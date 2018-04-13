package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.FbDatabase;
import org.firebirdsql.gds.ng.FbMessageMetadata;
import org.firebirdsql.gds.ng.FbMetadataBuilder;
import org.firebirdsql.jna.fbclient.FbInterface.*;

import static org.firebirdsql.gds.ISCConstants.*;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IMetadataBuilderImpl implements FbMetadataBuilder {

    private IDatabaseImpl database;
    private IMaster master;
    private IStatus status;
    private int fieldCount;
    private IMetadataBuilder metadataBuilder;
    private IMessageMetadata messageMetadata;

    public IMetadataBuilderImpl(FbDatabase database, int fieldCount) throws FbException {
        this.database = (IDatabaseImpl)database;
        this.master = this.database.getMaster();
        this.status = this.database.getStatus();
        this.fieldCount = fieldCount;
        this.metadataBuilder = master.getMetadataBuilder(status, fieldCount);
    }

    @Override
    public FbMessageMetadata getMessageMetadata() throws FbException {

        messageMetadata = metadataBuilder.getMetadata(status);
        IMessageMetadataImpl metadata = new IMessageMetadataImpl(this);

        return metadata;
    }

    public IMetadataBuilder getMetadataBuilder() {
        return this.metadataBuilder;
    }

    public FbDatabase getDatabase() {
        return this.database;
    }

    public int addField() throws FbException {
        return this.metadataBuilder.addField(status);
    }

    @Override
    public void addSmallint(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_SHORT);
        metadataBuilder.setLength(status, index, Short.BYTES);
        metadataBuilder.setScale(status, index, 0);
    }

    @Override
    public void addInteger(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_LONG);
        metadataBuilder.setLength(status, index, Integer.BYTES);
        metadataBuilder.setScale(status, index, 0);
    }

    @Override
    public void addBigint(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_INT64);
        metadataBuilder.setLength(status, index, Long.BYTES);
        metadataBuilder.setScale(status, index, 0);
    }

    @Override
    public void addFloat(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_FLOAT);
        metadataBuilder.setLength(status, index, Float.BYTES);
    }

    @Override
    public void addDouble(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_DOUBLE);
        metadataBuilder.setLength(status, index, Double.BYTES);
    }

    @Override
    public void addDecfloat16(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_DEC16);
        metadataBuilder.setLength(status, index, IDecFloat16.STRING_SIZE);
    }

    @Override
    public void addDecfloat34(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_DEC34);
        metadataBuilder.setLength(status, index, IDecFloat34.STRING_SIZE);
    }

    @Override
    public void addBlob(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_BLOB);
        metadataBuilder.setLength(status, index, Integer.BYTES * 2);
    }

    @Override
    public void addBoolean(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_BOOLEAN);
        metadataBuilder.setLength(status, index, Short.BYTES);
    }

    @Override
    public void addDate(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_DATE);
        metadataBuilder.setLength(status, index, Long.BYTES);
    }

    @Override
    public void addTime(int index) throws FbException {
        metadataBuilder.setType(status, index, /*SQL_TIME*/13);
        metadataBuilder.setLength(status, index, Long.BYTES);
    }

    @Override
    public void addTimestamp(int index) throws FbException {
        metadataBuilder.setType(status, index, SQL_TIMESTAMP);
        metadataBuilder.setLength(status, index, Long.BYTES);
    }

    @Override
    public void addChar(int index, int length) throws FbException {
        metadataBuilder.setType(status, index, SQL_TEXT);
        metadataBuilder.setLength(status, index, length);
    }

    @Override
    public void addVarchar(int index, int length) throws FbException {
        metadataBuilder.setType(status, index, SQL_VARYING);
        metadataBuilder.setLength(status, index, length);
    }

    @Override
    public void addChar(int index, int length, int charSet) throws FbException {
        metadataBuilder.setType(status, index, SQL_TEXT);
        metadataBuilder.setLength(status, index, length);
        metadataBuilder.setCharSet(status, index, charSet);
    }

    @Override
    public void addVarchar(int index, int length, int charSet) throws FbException {
        metadataBuilder.setType(status, index, SQL_VARYING);
        metadataBuilder.setLength(status, index, length);
        metadataBuilder.setCharSet(status, index, charSet);
    }
}
