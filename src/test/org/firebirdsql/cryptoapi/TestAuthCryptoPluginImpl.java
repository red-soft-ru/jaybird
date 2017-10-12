package org.firebirdsql.cryptoapi;

import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.common.FBJUnit4TestBase;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class TestAuthCryptoPluginImpl extends FBJUnit4TestBase {

    private byte[] testbuf = "xxThis is a test of the crypto plugin".getBytes();
    private Object hash = null;

    public static void initLogger() {
        BasicConfigurator.configure();
    }

    @Test
    public void testAuthCryptoPlugin_register() throws Exception {
        initLogger();
        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());
    }

    @Test
    public void testAuthCryptoPluginImpl_createHash() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        hash = plugin.createHash(testbuf);
        assertNotNull(hash);
    }

    @Test
    public void testAuthCryptoPluginImpl_destroyHash() throws Exception {
        if (hash == null)
            testAuthCryptoPluginImpl_createHash();
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        boolean res = plugin.destroyHash(hash);
        assertTrue(res);
    }
}
