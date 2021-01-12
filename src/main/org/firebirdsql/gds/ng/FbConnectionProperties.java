/*
 * Firebird Open Source JavaEE Connector - JDBC Driver
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
 * can be obtained from a source control history command.
 *
 * All rights reserved.
 */
package org.firebirdsql.gds.ng;

import org.firebirdsql.jdbc.AbstractFBConnectionProperties;
import org.firebirdsql.jdbc.FBConnectionProperties;

/**
 * Class for backward compatibility with connection properties
 *
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 3.0
 */
public class FbConnectionProperties extends FBConnectionProperties {

    /**
     * Default constructor for FBConnectionProperties.
     */
    public FbConnectionProperties() {
        super();
    }

    /**
     * Copy constructor for FBConnectionProperties.
     * <p>
     * All properties defined in {@link org.firebirdsql.jdbc.FirebirdConnectionProperties} are
     * copied from <code>src</code> to the new instance.
     * </p>
     *
     * @param src Source to copy from
     */
    public FbConnectionProperties(AbstractFBConnectionProperties src) {
        super(src);
    }

    public String getDatabaseName() {
        return getDatabase();
    }

    public void setDatabaseName(String databaseName) {
        setDatabase(databaseName);
    }

    public String getHost() {
        return getServer();
    }

    public void setHost(String host) {
        setServer(host);
    }

    public int getPortNumber() {
        return getPort();
    }

    public void setPortNumber(int portNumber) {
        setPort(portNumber);
    }

    public String getServerName() {
        return getServer();
    }

    public void setServerName(String serverName) {
        setServer(serverName);
    }

    public String getUser() {
        return getUserName();
    }

    public void setUser(String userName) {
        setUserName(userName);
    }
}
