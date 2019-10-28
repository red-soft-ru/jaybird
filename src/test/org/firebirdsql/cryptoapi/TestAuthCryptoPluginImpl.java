package org.firebirdsql.cryptoapi;

import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.common.FBJUnit4TestBase;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.gds.impl.wire.auth.AuthPrivateKeyContext;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class TestAuthCryptoPluginImpl extends FBJUnit4TestBase {

    private byte[] testbuf = "xxThis is a test of the crypto plugin".getBytes();
    private Object hash = null;

    public String loadCertFromFile(String filePath) throws Exception {
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
            throw new Exception("Error reading certificate from file " + filePath + ": " + e.getMessage());
        }
    }

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

    @Test
    public void testAuthCryptoPluginImpl_hashData() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        byte[] bytes = plugin.hashData(testbuf, 200000, 32798); // GOST R 34.11/34.10-2001
        assertNotNull(bytes);
        bytes = null;
        bytes = plugin.hashData(testbuf, 200000, 32801); // GOST R 34.11-2012/34.10-2012 256 bit
        assertNotNull(bytes);
        bytes = null;
        bytes = plugin.hashData(testbuf, 200000, 32802); // GOST R 34.11-2012/34.10-2012 512 bit
        assertNotNull(bytes);
    }

    @Test
    public void testAuthCryptoPluginImpl_generateRandom() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        byte[] bytes = plugin.generateRandom(null, 32);
        assertNotNull(bytes);
    }

    @Test
    public void testAuthCryptoPluginImpl_getUserKey() throws Exception {
        String cert = loadCertFromFile("testuser.cer");

        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        AuthPrivateKeyContext userKey = plugin.getUserKey(cert);
        assertNotNull(userKey);
    }
}
