package org.firebirdsql.gds.ng.wire.auth;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link GSSClient}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class TestGSSClient {
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
}
