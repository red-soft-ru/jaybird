package org.firebirdsql.jna.fbclient;

/**
 * Class with for wrapping an exception thrown by the native interface.
 *
 * @since 4.0
 */
public class FbInterfaceException {
    public static void catchException(FbInterface.IStatus status, Throwable t) {
        if (t == null)
            return;
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        t.printStackTrace(pw);
        String msg = sw.toString();
        final int msgLength = msg.getBytes().length + 1;
        try (CloseableMemory memory = new CloseableMemory(msgLength)) {
            memory.setString(0, msg);
            com.sun.jna.Pointer[] vector = new com.sun.jna.Pointer[]{
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_gds),
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_random),
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_cstring),
                    new com.sun.jna.Pointer(msgLength),
                    memory,
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_end)
            };
            status.setErrors2(vector.length, vector);
        }
    }

    public static void setVersionError(FbInterface.IStatus status, String interfaceName,
                                int currentVersion, int expectedVersion) {

        try (CloseableMemory memory = new CloseableMemory(interfaceName.length() + 1)) {
            memory.setString(0, interfaceName);
            com.sun.jna.Pointer[] vector = new com.sun.jna.Pointer[]{
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_gds),
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_interface_version_too_old),
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_number),
                    new com.sun.jna.Pointer(expectedVersion),
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_number),
                    new com.sun.jna.Pointer(currentVersion),
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_cstring),
                    new com.sun.jna.Pointer(memory.size()),
                    memory,
                    new com.sun.jna.Pointer(org.firebirdsql.gds.ISCConstants.isc_arg_end)
            };
            status.setErrors2(vector.length, vector);
        }
    }
}
