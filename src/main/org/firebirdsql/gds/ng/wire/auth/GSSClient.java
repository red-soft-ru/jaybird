package org.firebirdsql.gds.ng.wire.auth;

import org.ietf.jgss.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

    public byte[] getToken() {
        // Be sure to set the javax.security.auth.useSubjectCredsOnly
        // system property value to false if you want the underlying
        // mechanism to obtain credentials, rather than your application
        // or a wrapper program performing authentication using JAAS.
        System.setProperty( "javax.security.auth.useSubjectCredsOnly", "false");

        String response = new String(gssData);
        response = response.trim();
        String[] arr = response.split("\n");

        serverName = arr[1].trim();

        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(arr[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        hostName = addr.getHostName();

        principalName = serverName + "@" + hostName;

        GSSManager manager = GSSManager.getInstance();

        GSSName gssServerName = null;

        try {
            gssServerName = manager.createName(principalName, GSSName.NT_HOSTBASED_SERVICE);
        } catch (GSSException e) {
            e.printStackTrace();
        }

        // Get the context for authentication
        GSSContext context = null;
        byte[] token = new byte[0];
        try {
            context = manager.createContext(gssServerName, null, null,
                    GSSContext.INDEFINITE_LIFETIME);
            context.requestMutualAuth(true); // Request mutual authentication
            token = context.initSecContext(token, 0, token.length);
        } catch (GSSException e) {
            e.printStackTrace();
        }

        return token;
    }
}
