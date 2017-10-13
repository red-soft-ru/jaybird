package org.firebirdsql.cryptoapi;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.gds.impl.wire.auth.AuthPrivateKeyContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestAuthCryptoPluginImpl extends TestCase {

    private byte[] testbuf = "xxThis is a test of the crypto plugin".getBytes();
    private Object hash = null;

    public TestAuthCryptoPluginImpl(String name) {
        super(name);
    }

    public static void initLogger() {
        BasicConfigurator.configure();
    }

    public String loadCertFromFile(String filePath) throws GDSException {
        final byte buf[] = new byte[4096];
        final StringBuilder res = new StringBuilder();
        try {
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
        } catch (IOException e) {
            throw new GDSException("Error reading certificate from file " + filePath + ": " + e.getMessage());
        }
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

    public void testAuthCryptoPluginImpl_generateRandom() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        byte[] bytes = plugin.generateRandom(null, 32);
        assertNotNull(bytes);
    }

    public void testAuthCryptoPluginImpl_getUserKey() throws Exception {
        String cert = loadCertFromFile("testuser.cer");

        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        AuthPrivateKeyContext userKey = plugin.getUserKey(cert);
        assertNotNull(userKey);
    }
}
