/*
 * Firebird Open Source JDBC Driver
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
package org.firebirdsql.jdbc;

import org.firebirdsql.common.extension.UsesDatabaseExtension;
import org.firebirdsql.encodings.EncodingFactory;
import org.firebirdsql.gds.ng.DefaultDatatypeCoder;
import org.firebirdsql.gds.ng.fields.RowDescriptorBuilder;
import org.firebirdsql.jaybird.props.PropertyNames;
import org.firebirdsql.util.FirebirdSupportInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;
import java.util.Properties;

import static org.firebirdsql.common.FBTestProperties.*;
import static org.firebirdsql.common.FbAssumptions.assumeFeature;
import static org.firebirdsql.gds.ISCConstants.SQL_DOUBLE;
import static org.firebirdsql.gds.ISCConstants.SQL_FLOAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * This method tests correctness of {@link FBResultSetMetaData} class.
 *
 * @author Roman Rokytskyy
 * @author Mark Rotteveel
 */
class FBResultSetMetaDataTest {

    private static final String CREATE_TABLE = """
            CREATE TABLE test_rs_metadata (
              id INTEGER NOT NULL PRIMARY KEY,
              simple_field VARCHAR(60) CHARACTER SET WIN1250,
              two_byte_field VARCHAR(60) CHARACTER SET BIG_5,
              three_byte_field VARCHAR(60) CHARACTER SET UNICODE_FSS,
              long_field NUMERIC(15, 2),
              int_field NUMERIC(8, 2),
              short_field NUMERIC(3, 2),
              char_octets_field CHAR(10) CHARACTER SET OCTETS,
              varchar_octets_field VARCHAR(15) CHARACTER SET OCTETS,
              calculated_field computed by (int_field + short_field)
            )""";
        
    private static final String TEST_QUERY = """
            SELECT
             simple_field, two_byte_field, three_byte_field,
             long_field, int_field, short_field
            FROM test_rs_metadata""";

    private static final String RECREATE_AUTO_INC_TABLE_TEMPLATE = """
            recreate table TEST_AUTO_INC_RS_METADATA (
              ID %s generated always as identity primary key
            )
            """;

    private static final String TEST_QUERY_AUTO_INC = "select ID from TEST_AUTO_INC_RS_METADATA";
    
    private static final String TEST_QUERY2 = "SELECT * from RDB$DATABASE";

    @RegisterExtension
    static final UsesDatabaseExtension.UsesDatabaseForAll usesDatabase = UsesDatabaseExtension.usesDatabaseForAll(
            CREATE_TABLE);

    @Test
    void testResultSetMetaData() throws Exception {
        Properties props = getDefaultPropertiesForConnection();
        props.put("lc_ctype", "UNICODE_FSS");

        try (Connection connection = DriverManager.getConnection(getUrl(), props);
             Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(TEST_QUERY);
            ResultSetMetaData metaData = rs.getMetaData();

            assertEquals(60, metaData.getPrecision(1), "simple_field must have size 60");
            assertEquals(60, metaData.getPrecision(2), "two_byte_field must have size 60");
            assertEquals(60, metaData.getPrecision(3), "three_byte_field must have size 60");
            assertEquals(15, metaData.getPrecision(4), "long_field must have precision 15");
            assertEquals(8, metaData.getPrecision(5), "int_field must have precision 8");
            assertEquals(3, metaData.getPrecision(6), "short_field must have precision 4");

            for (int idx = 1; idx <= 6; idx++) {
                assertFalse(metaData.isAutoIncrement(idx),
                        "Expected autoIncrement is false for " + metaData.getColumnName(idx));
            }
        }
    }

    @Test
    void testResultSetMetaData2() throws Exception {
        Properties props = getDefaultPropertiesForConnection();
        props.put("lc_ctype", "UNICODE_FSS");

        try (Connection connection = DriverManager.getConnection(getUrl(), props);
             Statement stmt = connection.createStatement()) {
            DatabaseMetaData dmd = connection.getMetaData();
            int firebirdVersion = dmd.getDatabaseMajorVersion();

            ResultSet rs = stmt.executeQuery(TEST_QUERY2);
            ResultSetMetaData metaData = rs.getMetaData();

            int columnDisplaySize = metaData.getColumnDisplaySize(3);
            int columnDisplaySize2 = metaData.getColumnDisplaySize(4);

            if (firebirdVersion == 1) {
                assertEquals(10, columnDisplaySize, "RDB$SECURITY_CLASS must have display size 10");
                assertEquals(10, columnDisplaySize2, "RDB$CHARACTER_SET_NAME must have display size 10");
            } else if (firebirdVersion >= 2 && firebirdVersion < 4) {
                assertEquals(31, columnDisplaySize, "RDB$SECURITY_CLASS must have display size 31");
                assertEquals(31, columnDisplaySize2, "RDB$CHARACTER_SET_NAME must have display size 31");
            } else if (firebirdVersion >= 4) {
                assertEquals(63, columnDisplaySize, "RDB$SECURITY_CLASS must have display size 63");
                assertEquals(63, columnDisplaySize2, "RDB$CHARACTER_SET_NAME must have display size 63");
            } else {
                fail("Unknown Firebird version, not clear what to compare.");
            }
        }
    }

    @Test
    void testColumnTypeName() throws Exception {
        Properties props = getDefaultPropertiesForConnection();
		props.put("lc_ctype", "UNICODE_FSS");

        try (Connection connection = DriverManager.getConnection(getUrl(), props);
             Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(TEST_QUERY);
            ResultSetMetaData metaData = rs.getMetaData();

            assertEquals("VARCHAR", metaData.getColumnTypeName(1), "simple_field must be of type VARCHAR");
            assertEquals("VARCHAR", metaData.getColumnTypeName(2), "two_byte_field must be of type VARCHAR");
            assertEquals("VARCHAR", metaData.getColumnTypeName(3), "three_byte_field must be of type VARCHAR");
            assertEquals("NUMERIC", metaData.getColumnTypeName(4), "long_field must be of type NUMERIC");
            assertEquals("NUMERIC", metaData.getColumnTypeName(5), "int_field must be of type NUMERIC");
            assertEquals("NUMERIC", metaData.getColumnTypeName(6), "short_field must be of type NUMERIC");
        }
	}

    /**
     * Tests the default strategy for retrieving columnNames and columnLabels, where the columnName is the original column name
     * and the label is the AS clause (if specified), or the original column name otherwise.
     * <p>
     * This is the JDBC-compliant behavior
     * </p>
     */
    @Test
    void columnNameAndLabel_Default() throws Exception {
        try (Connection con = getConnectionViaDriverManager();
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT simple_field AS column1Alias, two_byte_field, 1 + 1, 2 - 2 AS column4Alias " +
                    "FROM test_rs_metadata");
            ResultSetMetaData metaData = rs.getMetaData();

            assertEquals("SIMPLE_FIELD", metaData.getColumnName(1), "Column 1, unexpected columnName");
            assertEquals("COLUMN1ALIAS", metaData.getColumnLabel(1), "Column 1, unexpected columnLabel");

            assertEquals("TWO_BYTE_FIELD", metaData.getColumnName(2), "Column 2, unexpected columnName");
            assertEquals("TWO_BYTE_FIELD", metaData.getColumnLabel(2), "Column 2, unexpected columnLabel");

            assertEquals("ADD", metaData.getColumnName(3), "Column 3, unexpected columnName");
            assertEquals("ADD", metaData.getColumnLabel(3), "Column 3, unexpected columnLabel");

            if (getDefaultSupportInfo().isVersionEqualOrAbove(2, 5, 8)) {
                assertEquals("SUBTRACT", metaData.getColumnName(4), "Column 4, unexpected columnName");
            } else {
                assertEquals("", metaData.getColumnName(4), "Column 4, unexpected columnName");
            }
            assertEquals("COLUMN4ALIAS", metaData.getColumnLabel(4), "Column 4, unexpected columnLabel");
        }
    }

    /**
     * Tests for columnLabelForName strategy of retrieving columnNames and columnLabels,
     * where the columnName has the same value as the label.
     * <p>
     * This strategy is enabled by specifying the columnLabelForName property (value true)
     * in the connection properties
     * </p>
     */
    @Test
    void columnNameAndLabel_ColumnLabelForName() throws Exception {
        Properties props = getDefaultPropertiesForConnection();
        props.put("columnLabelForName", "true");

        try (Connection con = DriverManager.getConnection(getUrl(), props);
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT simple_field AS column1Alias, two_byte_field, 1 + 1, 2 - 2 AS column4Alias " +
                    "FROM test_rs_metadata");
            ResultSetMetaData metaData = rs.getMetaData();

            assertEquals("COLUMN1ALIAS", metaData.getColumnName(1), "Column 1, unexpected columnName");
            assertEquals("COLUMN1ALIAS", metaData.getColumnLabel(1), "Column 1, unexpected columnLabel");

            assertEquals("TWO_BYTE_FIELD", metaData.getColumnName(2), "Column 2, unexpected columnName");
            assertEquals("TWO_BYTE_FIELD", metaData.getColumnLabel(2), "Column 2, unexpected columnLabel");

            assertEquals("ADD", metaData.getColumnName(3), "Column 3, unexpected columnName");
            assertEquals("ADD", metaData.getColumnLabel(3), "Column 3, unexpected columnLabel");

            assertEquals("COLUMN4ALIAS", metaData.getColumnName(4), "Column 4, unexpected columnName");
            assertEquals("COLUMN4ALIAS", metaData.getColumnLabel(4), "Column 4, unexpected columnLabel");
        }
    }

    /**
     * Tests if the columnLabelForName strategy allows com.sun.rowset.CachedRowSetImpl to
     * access rows by their columnLabel.
     */
    @Test
    void cachedRowSetImpl_columnLabelForName() throws Exception {
        Properties props = getDefaultPropertiesForConnection();
        props.put("columnLabelForName", "true");

        try (Connection con = DriverManager.getConnection(getUrl(), props);
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "SELECT id, simple_field AS column2, int_field AS column3, 2 - 1 AS column4 FROM test_rs_metadata");

            RowSetFactory aFactory = RowSetProvider.newFactory();
            CachedRowSet rowSet = aFactory.createCachedRowSet();
            rowSet.populate(rs);

            assertEquals(1, rowSet.findColumn("id"));
            assertEquals(2, rowSet.findColumn("column2"));
            assertEquals(3, rowSet.findColumn("column3"));
            assertEquals(4, rowSet.findColumn("column4"));

            assertThrows(SQLException.class, () -> rowSet.findColumn("simple_field"),
                    "Looking up column with original column name should fail with columnLabelForName strategy");

            rowSet.close();
        }
    }

    @Test
    void octetsCharAndVarchar() throws Exception {
        try (Connection con = getConnectionViaDriverManager();
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT char_octets_field, varchar_octets_field FROM test_rs_metadata");
            ResultSetMetaData rsmd = rs.getMetaData();

            assertEquals(Types.BINARY, rsmd.getColumnType(1), "Unexpected column type, expected CHAR");
            assertEquals(10, rsmd.getPrecision(1), "Unexpected column precision");
            assertEquals(10, rsmd.getColumnDisplaySize(1), "Unexpected column display size");
            assertEquals("[B", rsmd.getColumnClassName(1), "Unexpected column class name");

            assertEquals(Types.VARBINARY, rsmd.getColumnType(2), "Unexpected column type, expected VARCHAR");
            assertEquals(15, rsmd.getPrecision(2), "Unexpected column precision");
            assertEquals(15, rsmd.getColumnDisplaySize(2), "Unexpected column display size");
            assertEquals("[B", rsmd.getColumnClassName(2), "Unexpected column class name");
        }
    }

    @Test
    void precisionOfCalculatedField() throws Exception {
        try (Connection connection = getConnectionViaDriverManager();
             Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT calculated_field FROM test_rs_metadata");
            ResultSetMetaData rsmd = rs.getMetaData();

            // For Firebird 2.5 and earlier we estimate
            assertEquals(18, rsmd.getPrecision(1), "Unexpected column precision");
            assertEquals(18 + 2, rsmd.getColumnDisplaySize(1), "Unexpected column display size");
        }
    }

    @Test
    void getPrecisionOfNumericColumnWithoutActiveTransaction() throws Exception {
        try (Connection connection = getConnectionViaDriverManager();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            ResultSet rs = stmt.executeQuery("SELECT long_field FROM test_rs_metadata");
            ResultSetMetaData rsmd = rs.getMetaData();

            connection.commit();

            assertEquals(15, rsmd.getPrecision(1));
        }
    }

    @Test
    public void returnEmptyColumnNameWithAlias() throws Exception {
        try (Connection connection = getConnectionViaDriverManager()) {
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select (select count(1) from rdb$database) as \"AName\" from rdb$database\n" +
                    "union\n" +
                    "select 'a' as \"AName\" from rdb$database");
            ResultSetMetaData rsmd = rs.getMetaData();

            connection.commit();

            assertEquals("AName", rsmd.getColumnName(1));
        }
    }

    /**
     * Test for CORE-5655
     */
    @Test
    void getTableAliasCTE() throws Exception {
        FirebirdSupportInfo supportInfo = getDefaultSupportInfo();
        assumeTrue(supportInfo.isVersionEqualOrAbove(2, 5, 8)
                && !supportInfo.isVersionEqualOrAbove(3, 0) || supportInfo.isVersionEqualOrAbove(3, 0, 3),
                "Firebird 2.5.8+, 3.0.3+, or 4 or higher");
        try (Connection connection = getConnectionViaDriverManager();
             Statement stmt = connection.createStatement()) {
            stmt.execute("\n"
                    + "create table tablea("
                    + "column1 varchar(20),"
                    + "column2 varchar(20)"
                    + "); ");

            for (String query : new String[] {
                    "select a.column1 from (select b.column1 from (select c.column1 from tablea c) b) a",
                    "with a as (SELECT column1 from TABLEA)\n"
                            + "select column1 from a",
                    "select * from (select column1 from tablea b) a"
            }) {
                try (ResultSet rs = stmt.executeQuery(query)) {
//                    System.out.println(query);
                    FirebirdResultSetMetaData rsmd = rs.getMetaData().unwrap(FirebirdResultSetMetaData.class);
                    final String columnLabel = rsmd.getColumnLabel(1);
                    final String tableAlias = rsmd.getTableAlias(1);
//                    System.out.println("'" + columnLabel + "'");
//                    System.out.println("'" + tableAlias + "'");
                    assertEquals("COLUMN1", columnLabel, "columnLabel");
                    assertEquals("A", tableAlias, "tableAlias");
                }
//                System.out.println("---------");
            }
        }
    }

    /**
     * Test for CORE-5713
     */
    @Test
    void core5713() throws Exception {
        FirebirdSupportInfo supportInfo = getDefaultSupportInfo();
        assumeTrue(supportInfo.isVersionEqualOrAbove(3, 0, 3), "Test for CORE-5713, broken in version before 3.0.3");
        try (Connection connection = getConnectionViaDriverManager();
             Statement stmt = connection.createStatement()) {
            for (String query : new String[] { """
                    select 1 a1, 2 a2
                    from rdb$database
                    union all
                    select 1 a1, coalesce(cast(null as varchar(64)), 0) a2
                    from rdb$database""",
                    """
                    select a1, a2
                    from
                      (select 1 a1, 2 a2
                      from rdb$database)
                    group by 1, 2
                    union all
                    select 1 a1, coalesce(cast(null as varchar(64)), 0) a2
                    from rdb$database"""
            }) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    FirebirdResultSetMetaData rsmd = rs.getMetaData().unwrap(FirebirdResultSetMetaData.class);
                    assertEquals("A1", rsmd.getColumnLabel(1), "columnLabel1");
                    assertEquals("A2", rsmd.getColumnLabel(2), "columnLabel2");
                }
            }
        }
    }

    /**
     * Tests for <a href="https://github.com/FirebirdSQL/jaybird/issues/730">jaybird#730</a>.
     */
    @Test
    void getPrecision_connectionLessResultSet_shouldSucceedWithoutException_730() throws Exception {
        var rowDescriptor = new RowDescriptorBuilder(2,
                DefaultDatatypeCoder.forEncodingFactory(EncodingFactory.createInstance(StandardCharsets.UTF_8)))
                .at(0).simple(SQL_FLOAT, 4, "TEST", "FLOAT").addField()
                .at(1).simple(SQL_DOUBLE, 8, "TEST", "DOUBLE").addField()
                .toRowDescriptor();
        var rs = new FBResultSet(rowDescriptor, List.of());
        ResultSetMetaData rsmd = rs.getMetaData();

        assertEquals(24, rsmd.getPrecision(1));
        assertEquals(53, rsmd.getPrecision(2));
    }

    /**
     * Test for <a href="https://github.com/FirebirdSQL/jaybird/issues/731">jaybird#731</a>.
     */
    @Test
    void extendedFieldInfo_moreThan70Columns_731() throws Exception {
        try (var connection = getConnectionViaDriverManager();
             var stmt = connection.createStatement()) {

            var createTable = new StringBuilder(28 + 71 * 22).append("create table extfieldtest (");
            var selectStmt = new StringBuilder(25 + 71 * 9).append("select ");
            for (int colIdx = 1; colIdx <= 70; colIdx++) {
                createTable.append("column").append(colIdx).append(" numeric(3,1),");
                selectStmt.append("column").append(colIdx).append(',');
            }
            createTable.append("column71 numeric(2,1))");
            selectStmt.append("column71 from extfieldtest");

            stmt.execute(createTable.toString());

            try (var rs = stmt.executeQuery(selectStmt.toString())) {
                ResultSetMetaData rsmd = rs.getMetaData();

                assertEquals(3, rsmd.getPrecision(70),
                        "expected actual precision of 3 instead of estimated precision of 4");
                assertEquals(2, rsmd.getPrecision(71),
                        "expected actual precision of 2 instead of estimated precision of 4");
            }
        }
    }

    @ParameterizedTest
    @EnumSource(value = JDBCType.class, names = { "SMALLINT", "INTEGER", "BIGINT" })
    void isAutoIncrement_identityColumn(JDBCType columnType) throws Exception {
        assumeFeature(FirebirdSupportInfo::supportsIdentityColumns, "Test requires identity column support");
        try (var connection = getConnectionViaDriverManager();
             var stmt = connection.createStatement()) {
            stmt.execute(RECREATE_AUTO_INC_TABLE_TEMPLATE.formatted(columnType.name()));

            var rs = stmt.executeQuery(TEST_QUERY_AUTO_INC);
            ResultSetMetaData rsmd = rs.getMetaData();
            assertTrue(rsmd.isAutoIncrement(1), "Expected autoIncrement true");
        }
    }

    @ParameterizedTest
    @EnumSource(value = JDBCType.class, names = { "SMALLINT", "INTEGER", "BIGINT" })
    void isAutoIncrement_extendedMetadataDisabled(JDBCType columnType) throws Exception {
        assumeFeature(FirebirdSupportInfo::supportsIdentityColumns, "Test requires identity column support");
        try (var connection = getConnectionViaDriverManager(PropertyNames.extendedMetadata, "false");
             var stmt = connection.createStatement()) {
            stmt.execute(RECREATE_AUTO_INC_TABLE_TEMPLATE.formatted(columnType.name()));

            var rs = stmt.executeQuery(TEST_QUERY_AUTO_INC);
            ResultSetMetaData rsmd = rs.getMetaData();
            assertFalse(rsmd.isAutoIncrement(1), "Expected autoIncrement false when extendedMetadata=false");
        }
    }

    @Test
    void getPrecision_extendedMetadataDisabled() throws Exception {
        try (var connection = getConnectionViaDriverManager(PropertyNames.extendedMetadata, "false");
             var stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("select long_field, int_field, short_field from test_rs_metadata");

            ResultSetMetaData rsmd = rs.getMetaData();
            assertEquals(18, rsmd.getPrecision(1),
                    "Expected estimated precision 18 for long_field when extendedMetadata = false");
            assertEquals(9, rsmd.getPrecision(2),
                    "Expected estimated precision 9 for int_field when extendedMetadata = false");
            assertEquals(4, rsmd.getPrecision(3),
                    "Expected estimated precision 4 for short_field when extendedMetadata = false");
        }
    }

}