package org.firebirdsql.gds.ng.jna;

import org.firebirdsql.gds.ng.FbBatch;
import java.sql.SQLException;

/**
 * Implementation of {@link org.firebirdsql.gds.ng.jna.AbstractFbMessageBuilder}
 * to build messages for a native connection using OO API.
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IMessageBuilderImpl extends AbstractFbMessageBuilder {

    public IMessageBuilderImpl(FbBatch batch) throws SQLException {
        super(batch);
    }
}
