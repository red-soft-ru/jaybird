package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.impl.nativeoo.FbOOEmbeddedGDSFactoryPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class FbOOEmbeddedServicesAPITest extends AbstractServicesAPITest {

    @RegisterExtension
    static final GdsTypeExtension testType = GdsTypeExtension.supports(FbOOEmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);

    @BeforeEach
    @Override
    void setUp() throws Exception {
        gdsType = GDSType.getType(FbOOEmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);
        super.setUp();
    }

    @Override
    void connectToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:firebirdsql:fboo:embedded:" + mAbsoluteDatabasePath + "?encoding=NONE", "SYSDBA", "masterkey");
        connection.close();
    }
}
