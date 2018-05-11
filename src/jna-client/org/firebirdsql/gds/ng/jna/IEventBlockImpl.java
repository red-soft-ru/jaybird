package org.firebirdsql.gds.ng.jna;

import com.sun.jna.Callback;
import com.sun.jna.Memory;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import org.firebirdsql.encodings.Encoding;
import org.firebirdsql.gds.EventHandler;
import org.firebirdsql.gds.ng.AbstractEventHandle;
import org.firebirdsql.jna.fbclient.FbClientLibrary;
import org.firebirdsql.jna.fbclient.FbInterface.*;
import org.firebirdsql.jna.fbclient.WinFbClientLibrary;
import org.firebirdsql.logging.Logger;
import org.firebirdsql.logging.LoggerFactory;

/**
 * Event handle for the native OO API.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 4.0
 */
public class IEventBlockImpl extends AbstractEventHandle {

    private static final Logger LOG = LoggerFactory.getLogger(JnaEventHandle.class);

    private final CloseableMemory eventNameMemory;
    private int size = -1;
    private int eventId;
    private IEventBlock eventBlock;
    private IEventCallback callback;
    private int reference = 0;

    IEventBlockImpl(String eventName, EventHandler eventHandler, Encoding encoding) {
        super(eventName, eventHandler);
        // Requires null-termination
        final byte[] eventNameBytes = encoding.encodeToCharset(eventName + '\0');
        if (eventNameBytes.length > 256) {
            throw new IllegalArgumentException("Event name as bytes too long");
        }
        eventNameMemory = new CloseableMemory(eventNameBytes.length);
        eventNameMemory.write(0, eventNameBytes, 0, eventNameBytes.length);
        callback = new IEventCallback(new IEventCallbackIntf() {
            @Override
            public void eventCallbackFunction(int length, Pointer events) {
                if (length > 0)
                    onEventOccurred();
            }

            @Override
            public void addRef() {
                ++reference;
            }

            @Override
            public int release() {
                return --reference;
            }
        });
    }

    @Override
    protected void setEventCount(int eventCount) {
        super.setEventCount(eventCount);
    }

    @Override
    public int getEventId() {
        return eventId;
    }

    /**
     * @return Event memory name
     */
    Memory getEventNameMemory() {
        return eventNameMemory;
    }

    /**
     * @param size Size of the event buffers
     */
    void setSize(int size) {
        this.size = size;
    }

    /**
     * @return Size of the event buffers
     */
    int getSize() {
        return eventBlock.getLength();
    }

    /**
     * @return Event callback.
     */
    IEventCallback getCallback() {
        return callback;
    }

    public IEventBlock getEventBlock() {
        return eventBlock;
    }

    public void setEventBlock(IEventBlock eventBlock) {
        this.eventBlock = eventBlock;
    }

    public synchronized void releaseMemory() {
        if (size == -1) return;
        try {
            eventNameMemory.close();
        } finally {
            size = -1;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            releaseMemory();
        } finally {
            super.finalize();
        }
    }

    private class IEventCallbackImpl extends IEventCallback {
        public void addRef()
        {
            IReferenceCounted.VTable vTable = getVTable();
            vTable.addRef.invoke(this);
        }

        public int release()
        {
            IReferenceCounted.VTable vTable = getVTable();
            int result = vTable.release.invoke(this);
            return result;
        }

        @Override
        public void eventCallbackFunction(int length, com.sun.jna.Pointer events) {

            onEventOccurred();
        }
    }
}
