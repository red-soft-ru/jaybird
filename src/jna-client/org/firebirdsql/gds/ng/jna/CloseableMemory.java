package org.firebirdsql.gds.ng.jna;

import com.sun.jna.Memory;

public class CloseableMemory extends Memory implements AutoCloseable {

    public CloseableMemory(long size)
    {
        super(size);
    }

    @Override
    public void close() {
        dispose();
    }
}
