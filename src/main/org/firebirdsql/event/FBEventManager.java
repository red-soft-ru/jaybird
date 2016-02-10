/*
 * $Id$
 * 
 * Firebird Open Source J2EE Connector - JDBC Driver
 * 
 * Copyright (C) All Rights Reserved.
 * 
 * This file was created by members of the firebird development team.
 * All individual contributions remain the Copyright (C) of those
 * individuals.  Contributors to this file are either listed here or
 * can be obtained from a CVS history command.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  
 *   - Redistributions of source code must retain the above copyright 
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above 
 *     copyright notice, this list of conditions and the following 
 *     disclaimer in the documentation and/or other materials provided 
 *     with the distribution.
 *   - Neither the name of the firebird development team nor the names
 *     of its contributors may be used to endorse or promote products 
 *     derived from this software without specific prior written 
 *     permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF 
 * SUCH DAMAGE.
 */
package org.firebirdsql.event;

import org.firebirdsql.gds.DatabaseParameterBuffer;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.GDSFactory;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jdbc.FBSQLException;

/**
 * An {@link EventListener} implementation to listen for database events.
 *
 * @author <a href="mailto:gab_reid@users.sourceforge.net">Gabriel Reid</a>
 * @author <a href="mailto:rkisluhin@users.sourceforge.net">Roman Kislukhin</a>
 */
public class FBEventManager extends AbstractEventManager {
    private final GDSType gdsType;
    private String user = "";
    private String password = "";
    private String database = "";
    private int port = 3050;
    private String host = "localhost";

    public FBEventManager() {
        this(GDSFactory.getDefaultGDSType());
    }

    public FBEventManager(GDSType gdsType) {
        this.gdsType = gdsType;
    }

    protected GDSHandle attachDatabase() throws FBSQLException {
        final GDSHandle gdsHandle = new GDSHandle();
        gdsHandle.gds = GDSFactory.getGDSForType(gdsType);
        gdsHandle.dbHandle = gdsHandle.gds.createIscDbHandle();

        final DatabaseParameterBuffer dpb = gdsHandle.gds.createDatabaseParameterBuffer();
        dpb.addArgument(DatabaseParameterBuffer.USER, user);
        dpb.addArgument(DatabaseParameterBuffer.PASSWORD, password);
        String connString = host + "/" + port + ":" + database;
        try {
            gdsHandle.gds.iscAttachDatabase(connString, gdsHandle.dbHandle, dpb);
        } catch (GDSException e){
            throw new FBSQLException(e);
        }
        return gdsHandle;
    }

    protected void detachDatabase() throws FBSQLException {
        try {
            final GDSHandle gdsHandle = getGdsHandle();
            gdsHandle.gds.iscDetachDatabase(gdsHandle.dbHandle);
        } catch (GDSException e2) {
            throw new FBSQLException(e2);
        }
    }

    /**
     * Sets the username for the connection to the database .
     *
     * @param user the username for the connection to the database.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Returns the username for the connection to the databaes.
     *
     * @return the username for the connection to the database.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the password for the connection to the database.
     *
     * @param password for the connection to the database.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the password for the connection to the database.
     *
     * @return the password for the connection to the database.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the database path for the connection to the database.
     *
     * @param database path for the connection to the database.
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Returns the database path for the connection to the database.
     *
     * @return the database path for the connection to the database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Returns the host for the connection to the database.
     *
     * @return the host for the connection to the database.
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host for the connection to the database.
     *
     * @param host for the connection to the database.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns the port for the connection to the database.
     *
     * @return the port for the connection to the database.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port for the connection to the database.
     *
     * @param port for the connection to the database.
     */
    public void setPort(int port) {
        this.port = port;
    }
}