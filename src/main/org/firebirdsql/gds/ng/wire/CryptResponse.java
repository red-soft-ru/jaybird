package org.firebirdsql.gds.ng.wire;

/**
 * Created by vasiliy on 09.02.17.
 */
public final class CryptResponse implements Response {

    private byte[] serverData;

    CryptResponse (byte[] serverData) {
        this.serverData = serverData;
    }

    public byte[] getData() {
        return serverData;
    }
}
