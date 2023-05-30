package org.firebirdsql.gds.ng.nativeoo;

import com.sun.jna.Memory;

/**
 * Memory class for send and receive native messages using OO API.
 *
 * @since 4.0
 */
public class CloseableMemory extends Memory implements AutoCloseable {

    public CloseableMemory(long size) {
        super(size);
    }
}
