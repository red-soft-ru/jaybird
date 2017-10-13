package org.firebirdsql.cryptoapi;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;

import static org.junit.Assert.assertNotNull;

public class TestAuthCryptoPluginImpl extends TestCase {

    private byte[] testbuf = "xxThis is a test of the crypto plugin".getBytes();
    private Object hash = null;

    public TestAuthCryptoPluginImpl(String name) {
        super(name);
    }

    public static void initLogger() {
        BasicConfigurator.configure();
    }

    public void testAuthCryptoPlugin_register() throws Exception {
        initLogger();
        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());
    }

    public void testAuthCryptoPluginImpl_createHash() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        hash = plugin.createHash(testbuf);
        assertNotNull(hash);
    }

    public void testAuthCryptoPluginImpl_destroyHash() throws Exception {
        if (hash == null)
            testAuthCryptoPluginImpl_createHash();
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        boolean res = plugin.destroyHash(hash);
        assertTrue(res);
    }

    public void testAuthCryptoPluginImpl_hashData() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        byte[] bytes = plugin.hashData(testbuf, 200000);
        assertNotNull(bytes);
    }
}
