package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.FbMessageMetadata;
import org.firebirdsql.gds.ng.FbMetadataBuilder;
import org.firebirdsql.jna.fbclient.FbInterface.*;

/**
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IMessageMetadataImpl implements FbMessageMetadata {

    private IDatabaseImpl database;
    private IMetadataBuilderImpl metadataBuilderImpl;
    private IMetadataBuilder metadataBuilder;

    public IMessageMetadataImpl(FbMetadataBuilder metadataBuilder) {

        this.metadataBuilderImpl = (IMetadataBuilderImpl)metadataBuilder;
        this.database = (IDatabaseImpl)this.metadataBuilderImpl.getDatabase();

        this.metadataBuilder = this.metadataBuilderImpl.getMetadataBuilder();
    }

    public IMessageMetadata getMetadata() throws FbException {
        return metadataBuilder.getMetadata(database.getStatus());
    }
}
