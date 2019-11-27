package org.firebirdsql.gds.impl.wire.auth;

import org.firebirdsql.gds.ISCConstants;
import org.firebirdsql.gds.impl.wire.ByteBuffer;

/**
 * Provides compatibility with multifactor authentication plugin
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 */
public class AuthSspi3 extends AuthSspi {

    @Override
    public boolean request(ByteBuffer data) throws GDSAuthException {
        if (factors.isEmpty())
            return false;

        // Skip first call for leaving the clumplet less 256 byte
        if (currentFactor == -1) {
            currentFactor = 0;
            return true;
        }

        int dataCount = data.getLength();
        if (dataCount != 0) {
            if ((data.get(dataCount - 1) & 0xFF)  == ISCConstants.isc_dpb_session_encryption)
            {
                setSessionEncyption(true);
                data.setLength(dataCount - 1);
                dataCount = data.getLength();
            }
            if ((data.get(dataCount - 1) & 0xFF)  == ISCConstants.isc_dpb_security_authentication)
            {
                setSecurityAuthentication(true);
                data.setLength(dataCount - 1);
                dataCount = data.getLength();
            }
            int type = data.get(dataCount - 1);
            if (type == AuthFactor.TYPE_NONE) {
                if (currentFactor >= factors.size())
                    throw new GDSAuthException("Error multi factor authentication");
                if (sessionKey != null)
                    freezeSessionKey = true;
                // Current factor was passed. Move to the next if possible
                if (++currentFactor >= factors.size()) {
                    data.clear();
                    // Stop factors data exchanging
                    data.add((byte)AuthFactor.TYPE_NONE);
                    return true;
                }
            }
            else
                data.setLength(dataCount - 1);
        }

        final AuthFactor f = factors.get(currentFactor);
        if (!f.request(data))
            return false;

        data.add((byte)f.getType());
        return true;
    }
}
