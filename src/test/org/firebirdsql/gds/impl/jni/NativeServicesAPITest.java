package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.gds.impl.GDSType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class NativeServicesAPITest extends AbstractServicesAPITest {

    @RegisterExtension
    static final GdsTypeExtension testType = GdsTypeExtension.supports(NativeGDSFactoryPlugin.NATIVE_TYPE_NAME);

    @BeforeEach
    @Override
    void setUp() throws Exception {
        gdsType = GDSType.getType(NativeGDSFactoryPlugin.NATIVE_TYPE_NAME);
        port = 3050;
        super.setUp();
    }

    @Override
    void connectToDatabase() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:firebirdsql:native:" + mAbsoluteDatabasePath + "?encoding=NONE", "SYSDBA", "masterkey");
        connection.close();
    }
}
