package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.gds.impl.GDSType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class EmbeddedServicesAPITest extends AbstractServicesAPITest {

    @RegisterExtension
    static final GdsTypeExtension testType = GdsTypeExtension.supports(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);

    @BeforeEach
    @Override
    void setUp() throws Exception {
        gdsType = GDSType.getType(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);
        super.setUp();
    }

    @Override
    void connectToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:firebirdsql:embedded:" + mAbsoluteDatabasePath + "?encoding=NONE", "SYSDBA", "masterkey");
        connection.close();
    }
}
