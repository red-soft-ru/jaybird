/*
 * Firebird Open Source J2ee connector - jdbc driver
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a CVS history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.gds.impl.wire;

import java.io.IOException;
import java.io.InputStream;


/**
 * Improved version of the {@link org.firebirdsql.gds.impl.wire.XdrInputStream} that
 * can read some data directly in {@link org.firebirdsql.gds.impl.wire.isc_db_handle_impl}
 * object, this reduces garbage produced by the similar method in superclass.
 */
public class WireXdrInputStream extends XdrInputStream {

    public WireXdrInputStream() {
        super();
    }

    public WireXdrInputStream(InputStream in) {
        super(in);
    }

    public WireXdrInputStream(InputStream in, final String encoding) {
        super(in, encoding);
    }

    public int readBuffer(isc_db_handle_impl dbHandle) throws IOException {
        //HACK!: This function used only in receiveResponse, and
        //buffer length of op_response actually is USHORT, but sent as SSHORT
        //so cast it to unsigned.
        int len = readInt() & 0xFFFF;

        byte[] buffer = dbHandle.getResp_data();
        if (len > buffer.length) {
            buffer = new byte[len];
            dbHandle.setResp_data(buffer);
        }
        
        readFully(buffer,0,len);
        readFully(pad,0,(4 - len) & 3);

        dbHandle.setResp_data_len(len);
        
        return len;
    }


}
