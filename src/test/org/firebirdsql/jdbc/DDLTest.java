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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.firebirdsql.common.DdlHelper.executeDropTable;
import static org.firebirdsql.common.FBTestProperties.getConnectionViaDriverManager;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * This test case checks if DDL statements are executed correctly.
 *
 * @author Roman Rokytskyy
 */
class DDLTest {

    private static final String CREATE_MAIN_TABLE = ""
            + "CREATE TABLE main_table ("
            + "  id INTEGER NOT NULL PRIMARY KEY"
            + ")";

    private static final String CREATE_DETAIL_TABLE = ""
            + "CREATE TABLE detail_table("
            + "  main_id INTEGER NOT NULL, "
            + "  some_data VARCHAR(20)"
            + ")";

    private static final String ADD_FOREIGN_KEY = ""
            + "ALTER TABLE detail_table ADD FOREIGN KEY(main_id) "
            + "REFERENCES main_table(id) ON DELETE CASCADE";

    private static final String DROP_DETAIL_TABLE =
            "DROP TABLE detail_table";

    private static final String DROP_MAIN_TABLE =
            "DROP TABLE main_table";

    @RegisterExtension
    final UsesDatabaseExtension.UsesDatabaseForEach usesDatabase = UsesDatabaseExtension.usesDatabase();

    private static void executeUpdate(Connection connection, String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    @Test
    void testFKWithAutoCommit() throws Exception {
        try (Connection connection = getConnectionViaDriverManager()) {
            executeDropTable(connection, DROP_DETAIL_TABLE);
            executeDropTable(connection, DROP_MAIN_TABLE);

            executeUpdate(connection, CREATE_MAIN_TABLE);
            executeUpdate(connection, CREATE_DETAIL_TABLE);

            assertDoesNotThrow(() -> executeUpdate(connection, ADD_FOREIGN_KEY), "Should add foreign key constraint");

            executeDropTable(connection, DROP_DETAIL_TABLE);
            executeDropTable(connection, DROP_MAIN_TABLE);
        }
    }

    @Test
    void testFKWithTx() throws Exception {
        try (Connection connection = getConnectionViaDriverManager()) {
            connection.setAutoCommit(false);

            executeDropTable(connection, DROP_DETAIL_TABLE);
            connection.commit();

            executeDropTable(connection, DROP_MAIN_TABLE);
            connection.commit();

            executeUpdate(connection, CREATE_MAIN_TABLE);
            connection.commit();

            executeUpdate(connection, CREATE_DETAIL_TABLE);
            connection.commit();

            assertDoesNotThrow(() -> {
                executeUpdate(connection, ADD_FOREIGN_KEY);
                connection.commit();
            }, "Should add foreign key constraint");

            connection.setAutoCommit(true);

            executeDropTable(connection, DROP_DETAIL_TABLE);
            executeDropTable(connection, DROP_MAIN_TABLE);
        }
    }

    @Test
    void testFKMixed() throws Exception {
        try (Connection connection = getConnectionViaDriverManager()) {
            executeDropTable(connection, DROP_DETAIL_TABLE);
            executeDropTable(connection, DROP_MAIN_TABLE);

            executeUpdate(connection, CREATE_MAIN_TABLE);
            executeUpdate(connection, CREATE_DETAIL_TABLE);

            assertDoesNotThrow(() -> executeUpdate(connection, ADD_FOREIGN_KEY), "Should add foreign key constraint");

            connection.setAutoCommit(false);

            executeDropTable(connection, DROP_DETAIL_TABLE);

            try {
                // TODO: Doesn't fail (FB3), so what is the meaning/intention?
                // Here it will fail, but should not,
                // everything is correct from the programmers point of view
                connection.commit();
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                connection.setAutoCommit(true);
                executeDropTable(connection, DROP_DETAIL_TABLE);
            }

            connection.setAutoCommit(false);

            executeDropTable(connection, DROP_MAIN_TABLE);

            try {
                // TODO: Doesn't fail (FB3), so what is the meaning/intention?
                // Here it will fail, but should not,
                // everything is correct from the programmers point of view
                connection.commit();
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                connection.setAutoCommit(true);
                executeDropTable(connection, DROP_MAIN_TABLE);
            }
        }
    }
}
