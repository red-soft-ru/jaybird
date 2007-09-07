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
package org.firebirdsql.gds.impl.jni;

import org.firebirdsql.gds.GDS;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.GDSFactoryPlugin;
import org.firebirdsql.jdbc.FBConnection;


public class InternalNewGDSFactoryPlugin implements GDSFactoryPlugin {

    private static final String[] TYPE_ALIASES = new String[0];
    private static final String[] JDBC_PROTOCOLS = new String[] {
            "jdbc:new:connection:"};

    private static InternalGDSImpl gds;
    public static final String INTERNAL_TYPE_NAME = "INTERNAL_NEW";

    public String getPluginName() {
        return "JNI-based GDS implementation using internal communication.";
    }

    public String getTypeName() {
        return INTERNAL_TYPE_NAME;
    }

    public String[] getTypeAliases() {
        return TYPE_ALIASES;
    }

    public Class getConnectionClass() {
        return FBConnection.class;
    }

    public String[] getSupportedProtocols() {
        return JDBC_PROTOCOLS;
    }

    public GDS getGDS() {

        if (gds == null)
            gds = new InternalNewGDSImpl();

        return gds;
    }

    public String getDatabasePath(String server, Integer port, String path) throws GDSException{
        return path;
    }

    public String getDatabasePath(String jdbcUrl) throws GDSException {
        String[] protocols = getSupportedProtocols();
        for (int i = 0; i < protocols.length; i++) {
            if (jdbcUrl.startsWith(protocols[i]))
                return jdbcUrl.substring(protocols[i].length());
        }

        throw new IllegalArgumentException("Incorrect JDBC protocol handling: "
                + jdbcUrl);
    }

    public String getDefaultProtocol() {
        return getSupportedProtocols()[0];
    }

    /**
     * /{@inheritDoc}
     */
    public boolean equals(Object obj) {
        return obj!=null&&getClass().getName().equals(obj.getClass().getName());
    }
}
