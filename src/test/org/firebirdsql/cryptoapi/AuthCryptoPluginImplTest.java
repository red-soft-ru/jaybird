package org.firebirdsql.cryptoapi;

import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoException;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.gds.impl.wire.auth.AuthPrivateKeyContext;
import org.firebirdsql.gds.impl.wire.auth.GDSAuthException;
import org.firebirdsql.jdbc.FBDriver;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AuthCryptoPluginImplTest {

    static {
        // Needed for supporting tests that don't reference DriverManager
        try {
            Class.forName(FBDriver.class.getName());
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("No suitable driver.");
        }
        try {
            AuthCryptoPlugin.register(new AuthCryptoPluginImpl());
        } catch (CryptoException e) {
            throw new ExceptionInInitializerError("Cannot register crypto plugin");
        }
    }

    private class HashThread extends Thread {

        private Object hash;
        private final String threadName;

        public HashThread(final int n) {
            this.threadName = "Thread " + n;
        }

        public void run(){
            this.setName(threadName);
            System.out.println(threadName + " is running");
            AuthCryptoPlugin plugin = null;
            try {
                    plugin = AuthCryptoPlugin.getPlugin();
                    hash = plugin.createHash(testbuf);
                    System.out.println(threadName + ": hash created");
                    assertNotNull(hash);
                    boolean res = plugin.destroyHash(hash);
                    System.out.println(threadName + ": hash destroyed");
                    assertTrue(res);
                    System.out.println(threadName + " is complete");
            } catch (SQLException | AuthCryptoException e) {
                System.out.println("Exception in " + threadName + ": " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

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

    @Test
    void testAuthCryptoPlugin_multipleThreads() throws Exception {
        final int size = 200;
        final Thread hashThreads[] = new Thread[size];
        for (int i = 0; i < size; i++) {
            hashThreads[i] = new Thread(new HashThread(i));
            hashThreads[i].setName("Thread " + i);
            hashThreads[i].start();
        }
        for (int j = 0; j < size; j++) {
            hashThreads[j].join();
        }
    }

    @Test
    void testAuthCryptoPlugin_register() throws Exception {
        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());
    }

    @Test
    void testAuthCryptoPluginImpl_createHash() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        hash = plugin.createHash(testbuf);
        assertNotNull(hash);
    }

    @Test
    void testAuthCryptoPluginImpl_destroyHash() throws Exception {
        if (hash == null)
            testAuthCryptoPluginImpl_createHash();
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        boolean res = plugin.destroyHash(hash);
        assertTrue(res);
    }

    @Test
    void testAuthCryptoPluginImpl_hashData() throws Exception {
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
    void testAuthCryptoPluginImpl_generateRandom() throws Exception {
        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        byte[] bytes = plugin.generateRandom(null, 32);
        assertNotNull(bytes);
    }

    @Test
    void testAuthCryptoPluginImpl_getUserKey() throws Exception {
        String cert = loadCertFromFile("/tmp/testuser.cer");

        AuthCryptoPlugin plugin = AuthCryptoPlugin.getPlugin();
        AuthPrivateKeyContext userKey = plugin.getUserKey(cert);
        assertNotNull(userKey);
    }
}
