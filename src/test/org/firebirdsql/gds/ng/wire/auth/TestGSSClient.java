package org.firebirdsql.gds.ng.wire.auth;

import org.firebirdsql.common.FBJUnit4TestBase;
import org.firebirdsql.jdbc.FBConnection;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.firebirdsql.common.FBTestProperties.getUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link GSSClient}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class TestGSSClient extends FBJUnit4TestBase {

    @Test
    public void testGetToken() throws Exception {
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("rdb_server"); // from ci/test.sh
        stringBuilder.append('@');
        stringBuilder.append("localhost"); // from ci/test.sh
        String principalName = stringBuilder.toString();
        GSSManager manager = GSSManager.getInstance();
        GSSName gssServerName;
        gssServerName = manager.createName(principalName, GSSName.NT_HOSTBASED_SERVICE);
        // Get the context for authentication
        GSSContext context = null;
        byte[] token = new byte[0];
        context = manager.createContext(gssServerName, null, null,
                GSSContext.DEFAULT_LIFETIME);
        context.requestMutualAuth(true); // Request mutual authentication
        token = context.initSecContext(token, 0, token.length);

        assertNotNull(token);
    }

    @Test
    public void testGssAuthentication() throws Exception {
        Properties props = new Properties();
        props.put("lc_ctype", "WIN1251");
        props.put("useGSSAuth", "true");
        FBConnection connection = null;
        Statement statement = null;
        try {
            connection = (FBConnection) DriverManager.getConnection(getUrl(), props);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select current_user from rdb$database");
            resultSet.next();
            String currentUser = resultSet.getString(1);
            System.out.println("GSS auth. Current database user: " + currentUser);
            assertEquals("RDB_SERVER/LOCALHOST", currentUser);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            statement.close();
            connection.close();
        }
    }
}
