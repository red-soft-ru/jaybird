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
package org.firebirdsql.common.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.firebirdsql.common.FBTestProperties.getConnectionViaDriverManager;

/**
 * JUnit extension to create a user and drop it again after completion of the test.
 * <p>
 * NOTE: This extension only works for Firebird versions that support user management through SQL
 * (i.e. Firebird 2.5 and higher).
 * </p>
 *
 * @author Mark Rotteveel
 * @since 5
 */
public class DatabaseUserExtension implements AfterEachCallback {

    private static final System.Logger log = System.getLogger(DatabaseUserExtension.class.getName());

    private final List<User> createdUsers = new ArrayList<>();

    private DatabaseUserExtension() {
        // No direct instantiation
    }

    public static DatabaseUserExtension withDatabaseUser() {
        return new DatabaseUserExtension();
    }

    /**
     * Create a database user.
     * <p>
     * On Firebird 3.0 or higher this uses the default user manager plugin.
     * </p>
     *
     * @param username
     *         username
     * @throws SQLException
     *         For errors creating the user.
     */
    public void createUser(String username, String password) throws SQLException {
        createUser(username, password, null);
    }

    /**
     * Create a database user with the specified username and plugin.
     * <p>
     * For non-null plugins, this method will only work on Firebird 3 or higher.
     * </p>
     *
     * @param username
     *         username
     * @param plugin
     *         The user manager plugin to use, or {@code null} for the default (or Firebird 2.5)
     * @throws SQLException
     *         For errors creating the user.
     */
    public void createUser(String username, String password, String plugin) throws SQLException {
        StringBuilder createUserSql = new StringBuilder("CREATE USER ").append(username)
                .append(" PASSWORD ").append('\'').append(password).append('\'');
        if (plugin != null) {
            createUserSql.append(" USING PLUGIN ").append(plugin);
        }

        try (Connection connection = getConnectionViaDriverManager();
             Statement statement = connection.createStatement()) {
            statement.execute(createUserSql.toString());
        }

        createdUsers.add(new User(username, plugin));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (createdUsers.isEmpty()) {
            return;
        }
        try (Connection connection = getConnectionViaDriverManager()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                for (User user : createdUsers) {
                    dropUser(statement, user);
                }
            } finally {
                connection.commit();
            }
        } catch (SQLException e) {
            log.log(System.Logger.Level.ERROR, "Can't drop users", e);
        }
    }

    private void dropUser(Statement statement, User user) {
        try {
            StringBuilder dropUserSql = new StringBuilder("DROP USER ").append(user.name);
            if (user.plugin != null) {
                dropUserSql.append(" USING PLUGIN ").append(user.plugin);
            }
            statement.execute(dropUserSql.toString());
        } catch (SQLException e) {
            log.log(System.Logger.Level.ERROR, "Unable to drop user " + user, e);
        }
    }

    private static class User {
        private final String name;
        private final String plugin;

        private User(String name, String plugin) {
            this.name = name;
            this.plugin = plugin;
        }

        @Override
        public String toString() {
            return plugin == null ? name : name + " (" + plugin + " )";
        }
    }
}
