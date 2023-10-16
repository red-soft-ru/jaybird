package org.firebirdsql.gds.ng.nativeoo;

import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.gds.ng.BaseTestBlob;
import org.firebirdsql.gds.ng.FbConnectionProperties;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;

public class IBlobImplInputTest extends BaseTestBlob {

    @RegisterExtension
    @Order(1)
    public static final GdsTypeExtension testType = GdsTypeExtension.supportsFBOONativeOnly();

    private final AbstractNativeOODatabaseFactory factory =
            (AbstractNativeOODatabaseFactory) FBTestProperties.getFbDatabaseFactory();

    @Override
    protected IDatabaseImpl createFbDatabase(FbConnectionProperties connectionInfo) throws SQLException {
        final IDatabaseImpl db = factory.connect(connectionInfo);
        db.attach();
        return db;
    }

    @Override
    protected IDatabaseImpl createDatabaseConnection() throws SQLException {
        return (IDatabaseImpl) super.createDatabaseConnection();
    }
}
