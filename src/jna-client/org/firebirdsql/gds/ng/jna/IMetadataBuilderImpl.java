package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.FbDatabase;
import org.firebirdsql.gds.ng.FbMessageMetadata;
import org.firebirdsql.gds.ng.FbMetadataBuilder;
import org.firebirdsql.jna.fbclient.FbInterface.*;

import static org.firebirdsql.gds.ISCConstants.SQL_VARYING;

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

    }

    @Override
    public void addInteger(int index) throws FbException {

    }

    @Override
    public void addBigint(int index) throws FbException {

    }

    @Override
    public void addFloat(int index) throws FbException {

    }

    @Override
    public void addDouble(int index) throws FbException {

    }

    @Override
    public void addDecfloat16(int index) throws FbException {

    }

    @Override
    public void addDecfloat34(int index) throws FbException {

    }

    @Override
    public void addBlob(int index) throws FbException {

    }

    @Override
    public void addBoolean(int index) throws FbException {

    }

    @Override
    public void addDate(int index) throws FbException {

    }

    @Override
    public void addTime(int index) throws FbException {

    }

    @Override
    public void addTimestamp(int index) throws FbException {

    }

    @Override
    public void addChar(int index, int length) throws FbException {

    }

    @Override
    public void addVarchar(int index, int length) throws FbException {
        metadataBuilder.setType(status, index, SQL_VARYING);
        metadataBuilder.setLength(status, index, length);
    }

    @Override
    public void addChar(int index, int length, int charSet) throws FbException {

    }

    @Override
    public void addVarchar(int index, int length, int charSet) throws FbException {
        metadataBuilder.setType(status, index, SQL_VARYING);
        metadataBuilder.setCharSet(status, index, charSet);
        metadataBuilder.setLength(status, index, length);
    }
}
