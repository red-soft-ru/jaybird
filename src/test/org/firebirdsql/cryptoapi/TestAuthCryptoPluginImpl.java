package org.firebirdsql.cryptoapi;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;

public class TestAuthCryptoPluginImpl extends TestCase {
    public TestAuthCryptoPluginImpl(String name) {
        super(name);
    }

    public void testAuthCryptoPlugin_register() throws Exception {
        initLogger();
        AuthCryptoPlugin.register(new AuthCryptoPluginImpl());
    }

    public static void initLogger() {
        BasicConfigurator.configure();
    }

}
