package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.impl.wire.ByteBuffer;

public class AuthSspi4 extends AuthSspi {

    @Override
    public boolean request(ByteBuffer data) throws GDSAuthException {
        if (factors.isEmpty())
        return false;

        // Skip first call for leaving the clumplet less 256 byte
        if (currentFactor == -1) {
            currentFactor = 0;
            return true;
        }

        final AuthFactor f = factors.get(currentFactor);
        if (!f.request(data))
            return false;

        return true;
    }
}
