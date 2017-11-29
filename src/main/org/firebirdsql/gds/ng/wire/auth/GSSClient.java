package org.firebirdsql.gds.ng.wire.auth;

import org.ietf.jgss.*;

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

    public byte[] getToken() throws GSSException {
        // Need to get a ticket from the ticket cache
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

        int val = ((gssData[1] & 0xff) << 8) | (gssData[0] & 0xff);
        hostName = new String(Arrays.copyOfRange(gssData, 2, val + 2));
        int val2 = ((gssData[3 + val] & 0xff) << 8) | (gssData[2 + val] & 0xff);
        serverName = new String(Arrays.copyOfRange(gssData, 4 + val, 4 + val + val2));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(serverName);
        stringBuilder.append('@');
        stringBuilder.append(hostName);
        principalName = stringBuilder.toString();
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

        return token;
    }
}
