package org.firebirdsql.gds.impl.wire.auth;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.firebirdsql.common.FBJUnit4TestBase;
import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.JdbcResourceHelper;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;
import org.firebirdsql.gds.impl.GDSServerVersion;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jca.FBSADataSource;
import org.firebirdsql.jdbc.FirebirdConnection;
import org.junit.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.firebirdsql.common.FBTestProperties.getConnectionViaDriverManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for {@link AuthSspi}
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0
 */
public class TestAuthSspi extends FBJUnit4TestBase {

    public static void initLogger() {
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
    }

    @Test
    public void testMultifactorAuthCertificateOnly() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        try (Connection connection = getConnectionViaDriverManager();
             Statement statement = connection.createStatement()) {
            GDSServerVersion serverVersion =
                    connection.unwrap(FirebirdConnection.class).getFbDatabase().getServerVersion();
            if (serverVersion.getMajorVersion() == 4) {
                statement.execute("grant policy \"DEFAULT\" to \"TEST@RED-SOFT.RU\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Statement should not fail");
        }

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "/tmp/testuser.cer");
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
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "TEST@RED-SOFT.RU");
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
            assertEquals("test@red-soft.ru", resultSet.getString(1).toLowerCase());
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

        try (Connection connection = getConnectionViaDriverManager();
             Statement statement = connection.createStatement()) {
            GDSServerVersion serverVersion =
                    connection.unwrap(FirebirdConnection.class).getFbDatabase().getServerVersion();
            if (serverVersion.getMajorVersion() == 4) {
                statement.execute("grant policy TestPolicy to \"TEST@RED-SOFT.RU\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Statement should not fail");
        }

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "TEST@RED-SOFT.RU");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "/tmp/testuser.cer");
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
            assertEquals("test@red-soft.ru", resultSet.getString(1).toLowerCase());
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
    public void testVerifyServerCertificate() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "TEST@RED-SOFT.RU");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "q3rgu7Ah");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate_base64", loadFromFile("/tmp/testuser.cer"));
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
            assertEquals("test@red-soft.ru", resultSet.getString(1).toLowerCase());
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
    public void testTrustedCertificate() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "trusted_user");
        fbDataSource.setNonStandardProperty("isc_dpb_certificate", "/tmp/testuser.cer");
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
            assertEquals("trusted_user", resultSet.getString(1).toLowerCase());
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
    public void testTrustedUser() throws Exception {
        initLogger();

        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());

        final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

        fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
        fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
        fbDataSource.setNonStandardProperty("isc_dpb_user_name", "trusted_user");
        fbDataSource.setNonStandardProperty("isc_dpb_password", "trusted");
        fbDataSource.setNonStandardProperty("isc_dpb_effective_login", "effective_user");

        Connection conn = null;
        try {
            conn = fbDataSource.getConnection();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
            resultSet.next();
            System.out.println("Current user is " + resultSet.getString(1));
            assertEquals("effective_user", resultSet.getString(1).toLowerCase());
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

    private String loadFromFile(String filePath) throws IOException {
        final byte buf[] = new byte[4096];
        final StringBuilder res = new StringBuilder();
        final InputStream is = new FileInputStream(filePath);
        try {
            int c;
            while ((c = is.read(buf)) > 0) {
                res.append(new String(buf, 0, c));
            }
            return res.toString();
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }
}
