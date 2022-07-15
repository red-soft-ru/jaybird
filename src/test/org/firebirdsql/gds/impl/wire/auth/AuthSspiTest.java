package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.JdbcResourceHelper;
import org.firebirdsql.common.extension.UsesDatabaseExtension;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;
import org.firebirdsql.gds.impl.GDSServerVersion;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jaybird.xca.FBSADataSource;
import org.firebirdsql.jdbc.FirebirdConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.firebirdsql.common.FBTestProperties.getConnectionViaDriverManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link AuthSspi}
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0
 */
public class AuthSspiTest {

    @RegisterExtension
    static final UsesDatabaseExtension.UsesDatabaseForAll usesDatabase = UsesDatabaseExtension.usesDatabaseForAll();

    @Test
    void testMultifactorAuthCertificateOnly() throws Exception {

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

        try (AuthCryptoPluginImpl acp = new AuthCryptoPluginImpl()) {
            AuthCryptoPlugin.register(acp);

            final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

            fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
            fbDataSource.setNonStandardProperty("isc_dpb_user_name", "test@red-soft.ru");
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
    }

    @Test
    void testMultifactorAuthPasswordOnly() throws Exception {

        try (AuthCryptoPluginImpl acp = new AuthCryptoPluginImpl()) {
            AuthCryptoPlugin.register(acp);

            final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

            final String username = "UserWithGostPassword";
            final String password = "password";

            fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
            fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
            fbDataSource.setNonStandardProperty("isc_dpb_user_name", username);
            fbDataSource.setNonStandardProperty("isc_dpb_password", password);
            fbDataSource.setNonStandardProperty("isc_dpb_trusted_auth", "1");
            fbDataSource.setNonStandardProperty("isc_dpb_multi_factor_auth", "1");

            Connection conn = null;
            try {
                conn = fbDataSource.getConnection();

                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
                resultSet.next();
                System.out.println("Current user is " + resultSet.getString(1));
                assertEquals(username.toUpperCase(), resultSet.getString(1));
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

    @Test
    void testMultifactorAuthPasswordAndCertificate() throws Exception {

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

        try (AuthCryptoPluginImpl acp = new AuthCryptoPluginImpl()) {
            AuthCryptoPlugin.register(acp);

            final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

            fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
            fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
            fbDataSource.setNonStandardProperty("isc_dpb_user_name", "test@red-soft.ru");
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

    @Test
    void testTrustedCertificate() throws Exception {

        try (AuthCryptoPluginImpl acp = new AuthCryptoPluginImpl()) {
            AuthCryptoPlugin.register(acp);

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
    }

    @Test
    void testVerifyServerCertificate() throws Exception {

        try (AuthCryptoPluginImpl acp = new AuthCryptoPluginImpl()) {
            AuthCryptoPlugin.register(acp);

            final FBSADataSource fbDataSource = new FBSADataSource(GDSType.getType("PURE_JAVA"));

            fbDataSource.setDatabase(FBTestProperties.DB_DATASOURCE_URL);
            fbDataSource.setNonStandardProperty("isc_dpb_lc_ctype", "WIN1251");
            fbDataSource.setNonStandardProperty("isc_dpb_user_name", "test@red-soft.ru");
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
                assertEquals("TEST@RED-SOFT.RU", resultSet.getString(1).toUpperCase());
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

    @Test
    void testTrustedUser() throws Exception {

        try (AuthCryptoPluginImpl acp = new AuthCryptoPluginImpl()) {
            AuthCryptoPlugin.register(acp);

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
