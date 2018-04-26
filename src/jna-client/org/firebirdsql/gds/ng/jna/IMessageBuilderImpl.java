package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.FbBatch;
import java.sql.SQLException;

public class IMessageBuilderImpl extends AbstractFbMessageBuilder {

    public IMessageBuilderImpl(FbBatch batch) throws SQLException {
        super(batch);
    }
}
