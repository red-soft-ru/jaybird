/*
 * $Id$
 * 
 * Firebird Open Source J2EE Connector - JDBC Driver
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
package org.firebirdsql.ds;

import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.gds.impl.GDSServerVersion;
import org.firebirdsql.jdbc.FirebirdConnection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.PooledConnection;

import static org.firebirdsql.common.FBTestProperties.getDefaultSupportInfo;
import static org.firebirdsql.common.matchers.GdsTypeMatchers.isPureJavaType;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

/**
 * Tests for {@link FBConnectionPoolDataSource}
 * 
 * @author <a href="mailto:mrotteveel@users.sourceforge.net">Mark Rotteveel</a>
 */
public class TestFBConnectionPoolDataSource extends FBConnectionPoolTestBase {

    /**
     * Tests if the ConnectionPoolDataSource can create a PooledConnection
     * 
     * @throws SQLException
     */
    @Test
    public void testDataSource_start() throws SQLException {
        getPooledConnection();
    }

    /**
     * Tests if the connection obtained from the PooledConnection can be used
     * and has expected defaults.
     * 
     * @throws SQLException
     */
    @Test
    public void testConnection() throws SQLException {
        PooledConnection pc = getPooledConnection();

        Connection con = pc.getConnection();

        assertTrue("Autocommit should be true", con.getAutoCommit());
        assertFalse("Read-only should be false", con.isReadOnly());
        assertEquals("Tx isolation level should be read committed.",
                Connection.TRANSACTION_READ_COMMITTED, con.getTransactionIsolation());

        Statement stmt = con.createStatement();

        try {
            ResultSet rs = stmt.executeQuery("SELECT cast(1 AS INTEGER) FROM rdb$database");

            assertTrue("Should select one row", rs.next());
            assertEquals("Selected value should be 1.", 1, rs.getInt(1));
        } finally {
            stmt.close();
        }
        con.close();
        assertTrue("Connection should report as being closed.", con.isClosed());
    }
    
    /**
     * Test if a property stored with {@link FBConnectionPoolDataSource#setNonStandardProperty(String)} is retrievable.
     */
    @Test
    public void testSetNonStandardProperty_singleParam() {
        ds.setNonStandardProperty("someProperty=someValue");
        
        assertEquals("someValue", ds.getProperty("someProperty"));
    }
    
    /**
     * Test if a property stored with {@link FBConnectionPoolDataSource#setNonStandardProperty(String, String)} is retrievable.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSetNonStandardProperty_twoParam() {
        ds.setNonStandardProperty("someProperty", "someValue");
        
        assertEquals("someValue", ds.getProperty("someProperty"));
    }

    @Test
    public void enableWireCompression() throws Exception {
        assumeThat("Test only works with pure java connections", FBTestProperties.GDS_TYPE, isPureJavaType());
        assumeTrue("Test requires wire compression", getDefaultSupportInfo().supportsWireCompression());
        ds.setWireCompression(true);

        PooledConnection pooledConnection = ds.getPooledConnection();
        try (Connection connection = pooledConnection.getConnection()){
            assertTrue(connection.isValid(0));
            GDSServerVersion serverVersion =
                    connection.unwrap(FirebirdConnection.class).getFbDatabase().getServerVersion();
            assertTrue("expected wire compression in use", serverVersion.isWireCompressionUsed());
        } finally {
            pooledConnection.close();
        }
    }
}
