package org.firebirdsql.gds.impl.wire.auth;

import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.common.SimpleFBTestBase;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;
import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.GDS;
import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.IscDbHandle;
import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.impl.wire.TransactionParameterBufferImpl;
import org.firebirdsql.jca.FBSADataSource;
import org.firebirdsql.jca.FBTpb;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author vasiliy
 */
public class TestAuthSspi extends SimpleFBTestBase {

    static final String dbName = "testdb.gdb";
    private GDS gds;
    private DatabaseParameterBuffer c;
    private FBTpb tpb;

    /**
     * @param s
     */
    public TestAuthSspi(String s) {
        super(s);
    }

    public static void initLogger() {
        BasicConfigurator.configure();
    }

    protected void setUp() {
        if (!"PURE_JAVA".equals(System.getProperty("test.gds_type")) &&
                !"TYPE4".equals(System.getProperty("test.gds_type")) &&
                System.getProperty("test.gds_type") != null)
            fail("This test cannot be run for JNI driver");

        // super.setUp(); we will create our own db's directly
        gds = GDSFactory.getDefaultGDS();

        c = gds.createDatabaseParameterBuffer();

        c.addArgument(ISCConstants.isc_dpb_num_buffers, new byte[] { 90});
        c.addArgument(ISCConstants.isc_dpb_dummy_packet_interval, new byte[] {
                120, 10, 0, 0});
        c.addArgument(ISCConstants.isc_dpb_sql_dialect,
                new byte[] { 3, 0, 0, 0});
        c.addArgument(ISCConstants.isc_dpb_user_name, DB_USER);
        c.addArgument(ISCConstants.isc_dpb_password, DB_PASSWORD);

        TransactionParameterBufferImpl tpbImpl = new TransactionParameterBufferImpl();
        tpbImpl.addArgument(ISCConstants.isc_tpb_read_committed);
        tpbImpl.addArgument(ISCConstants.isc_tpb_no_rec_version);
        tpbImpl.addArgument(ISCConstants.isc_tpb_write);
        tpbImpl.addArgument(ISCConstants.isc_tpb_wait);

        tpb = new FBTpb(tpbImpl);
    }

    protected IscDbHandle createDatabase(String name) throws Exception {

        IscDbHandle db = gds.createIscDbHandle();

        gds.iscCreateDatabase(getdbpath(name), db, c);
        return db;
    }

    protected void tearDown() throws Exception {
        File db = new File(DB_PATH + "/" + dbName);
        if (db.exists())
            db.delete();
    }// hide superclass teardown.

    public void testMultifactorAuthCertificateOnly() throws Exception {
        initLogger();

        File db = new File(DB_PATH + "/" + dbName);
        if (db.exists())
            db.delete();
        createDatabase(dbName);

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        final String databaseURL = getdbpath(dbName);
        fbDataSource.setDatabase(databaseURL);
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "testuser.cer");
        fbDataSource.setNonStandardProperty("isc_dpb_repository_pin", "12345678");

        final Connection conn = fbDataSource.getConnection();

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
        resultSet.next();
        System.out.println("Current user is " + resultSet.getString(1));
        resultSet.close();
        statement.close();
        conn.close();
    }

    public void testMultifactorAuthPasswordOnly() throws Exception {
        initLogger();

        File db = new File(DB_PATH + "/" + dbName);
        if (db.exists())
            db.delete();
        createDatabase(dbName);

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        final String databaseURL = getdbpath(dbName);
        fbDataSource.setDatabase(databaseURL);
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "artyom.smirnov@red-soft.ru");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");

        final Connection conn = fbDataSource.getConnection();

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
        resultSet.next();
        System.out.println("Current user is " + resultSet.getString(1));
        resultSet.close();
        statement.close();
        conn.close();
    }

    public void testMultifactorAuthPasswordAndCertificate() throws Exception {
        initLogger();

        File db = new File(DB_PATH + "/" + dbName);
        if (db.exists())
            db.delete();
        createDatabase(dbName);

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        final String databaseURL = getdbpath(dbName);
        fbDataSource.setDatabase(databaseURL);
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "artyom.smirnov@red-soft.ru");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "testuser.cer");
        fbDataSource.setNonStandardProperty("isc_dpb_repository_pin", "12345678");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");

        final Connection conn = fbDataSource.getConnection();

        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
        resultSet.next();
        System.out.println("Current user is " + resultSet.getString(1));
        resultSet.close();
        statement.close();
        conn.close();
    }
}
