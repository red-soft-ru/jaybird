package org.firebirdsql.gds.ng.wire.auth;

import org.ietf.jgss.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by vasiliy on 09.02.17.
 */
public class GSSClient {

    private byte[] gssData;
    private String serverName;
    private String hostName;
    private String principalName;

    public GSSClient (byte[] gssData) {
        this.gssData = gssData;
    }

    public byte[] getToken() throws UnknownHostException, GSSException {
        // Be sure to set the javax.security.auth.useSubjectCredsOnly
        // system property value to false if you want the underlying
        // mechanism to obtain credentials, rather than your application
        // or a wrapper program performing authentication using JAAS.
        System.setProperty( "javax.security.auth.useSubjectCredsOnly", "false");

        int val = ((gssData[1] & 0xff) << 8) | (gssData[0] & 0xff);
        String address  = new String(Arrays.copyOfRange(gssData, 2, val+2));
        int val2 = ((gssData[3+val] & 0xff) << 8) | (gssData[2+val] & 0xff);
        InetAddress addr = null;
        serverName =  new String(Arrays.copyOfRange(gssData, 4+val, 4+val+val2));
        addr = InetAddress.getByName(address);
        hostName = addr.getHostName();
        principalName = serverName + "@" + hostName;
        GSSManager manager = GSSManager.getInstance();
        GSSName gssServerName = null;
        gssServerName = manager.createName(principalName, GSSName.NT_HOSTBASED_SERVICE);
        // Get the context for authentication
        GSSContext context = null;
        byte[] token = new byte[0];
        context = manager.createContext(gssServerName, null, null,
                GSSContext.DEFAULT_LIFETIME);
        context.requestMutualAuth(true); // Request mutual authentication
        token = context.initSecContext(token, 0, token.length);

        return token;
    }
}
