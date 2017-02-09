package org.firebirdsql.gds.ng.wire;

import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

/**
 * Created by vasiliy on 09.02.17.
 */
public final class CryptResponse implements Response {
    private static final Logger log = LoggerFactory.getLogger(CryptResponse.class);

    private byte[] serverData;

    CryptResponse (byte[] serverData) {
        this.serverData = serverData;
    }

    public byte[] getData() {
        return serverData;
    }
}
