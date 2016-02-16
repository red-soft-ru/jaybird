/*
 * $Id: AbstractEventManager.java,v 1.1 2015/11/14 17:27:00 roman.kislukhin Exp $
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

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.impl.GDSHelper;
import org.firebirdsql.jca.FBManagedConnection;
import org.firebirdsql.jdbc.FBSQLException;

/**
 * A managed connection based {@link EventManager} implementation to listen for database events.
 *
 * @author <a href="mailto:rkisluhin@users.sourceforge.net">Roman Kislukhin</a>
 */
public class FBMCEventManager extends AbstractEventManager {
    private final GDSHelper gdsHelper;

    public FBMCEventManager(FBManagedConnection mc) throws GDSException {
        gdsHelper = mc.getGDSHelper();
    }

    @Override
    protected GDSHandle attachDatabase() throws FBSQLException {
        return new GDSHandle(gdsHelper.getInternalAPIHandler(), gdsHelper.getCurrentDbHandle());
    }

    @Override
    protected void detachDatabase() throws FBSQLException {
        // nothing
    }

    @Override
    public void setUser(String user) {
        // nothing
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public void setPassword(String password) {
        // nothing
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setDatabase(String database) {
        // nothing
    }

    @Override
    public String getDatabase() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public void setHost(String host) {
        // nothing
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void setPort(int port) {
        // nothing
    }
}
