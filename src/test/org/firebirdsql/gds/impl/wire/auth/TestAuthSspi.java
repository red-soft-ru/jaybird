package org.firebirdsql.gds.impl.wire.auth;

import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.common.FBJUnit4TestBase;
import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.JdbcResourceHelper;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jca.FBSADataSource;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.fail;

/**
 * Tests for {@link AuthSspi}
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0
 */
public class TestAuthSspi extends FBJUnit4TestBase {

    public static void initLogger() {
        BasicConfigurator.configure();
    }

    @Test
    public void testMultifactorAuthCertificateOnly() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "testuser.cer");
        fbDataSource.setNonStandardProperty("isc_dpb_repository_pin", "12345678");

        Connection conn = null;
        try {
            conn = fbDataSource.getConnection();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
            resultSet.next();
            System.out.println("Current user is " + resultSet.getString(1));
            JdbcResourceHelper.closeQuietly(resultSet);
            JdbcResourceHelper.closeQuietly(statement);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Statement should not fail");
        } finally {
            JdbcResourceHelper.closeQuietly(conn);
            fbDataSource.close();
        }
    }

    @Test
    public void testMultifactorAuthPasswordOnly() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "artyom.smirnov@red-soft.ru");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");

        Connection conn = null;
        try {
            conn = fbDataSource.getConnection();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
            resultSet.next();
            System.out.println("Current user is " + resultSet.getString(1));
            JdbcResourceHelper.closeQuietly(resultSet);
            JdbcResourceHelper.closeQuietly(statement);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Statement should not fail");
        } finally {
            JdbcResourceHelper.closeQuietly(conn);
            fbDataSource.close();
        }
    }

    @Test
    public void testMultifactorAuthPasswordAndCertificate() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "artyom.smirnov@red-soft.ru");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "testuser.cer");
        fbDataSource.setNonStandardProperty("isc_dpb_repository_pin", "12345678");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");

        Connection conn = null;
        try {
            conn = fbDataSource.getConnection();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
            resultSet.next();
            System.out.println("Current user is " + resultSet.getString(1));
            JdbcResourceHelper.closeQuietly(resultSet);
            JdbcResourceHelper.closeQuietly(statement);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Statement should not fail");
        } finally {
            JdbcResourceHelper.closeQuietly(conn);
            fbDataSource.close();
        }
    }

    @Test
    @Ignore
    // TODO enable test after fix error with open server certificate #25043
    public void testVerifyServerCertificate() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "artyom.smirnov@red-soft.ru");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "testuser.cer");
        fbDataSource.setNonStandardProperty("isc_dpb_repository_pin", "12345678");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_verify_server", "1");

        Connection conn = null;
        try {
            conn = fbDataSource.getConnection();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
            resultSet.next();
            System.out.println("Current user is " + resultSet.getString(1));
            JdbcResourceHelper.closeQuietly(resultSet);
            JdbcResourceHelper.closeQuietly(statement);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Statement should not fail");
        } finally {
            JdbcResourceHelper.closeQuietly(conn);
            fbDataSource.close();
        }
    }
}
