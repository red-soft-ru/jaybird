package org.firebirdsql.management;

import org.firebirdsql.common.FBTestProperties;
import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.common.extension.UsesDatabaseExtension;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.gds.impl.jni.EmbeddedGDSFactoryPlugin;
import org.firebirdsql.gds.impl.nativeoo.FbOOEmbeddedGDSFactoryPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.firebirdsql.common.FBTestProperties.DB_PASSWORD;
import static org.firebirdsql.common.FBTestProperties.DB_SERVER_PORT;
import static org.firebirdsql.common.FBTestProperties.DB_SERVER_URL;
import static org.firebirdsql.common.FBTestProperties.DB_USER;
import static org.firebirdsql.common.FBTestProperties.getConnectionViaDriverManager;
import static org.firebirdsql.common.FBTestProperties.getDatabasePath;
import static org.firebirdsql.common.FBTestProperties.getGdsType;

/**
 * Tests for {@link org.firebirdsql.management.FBTraceManager}.
 *
 * @author <a href="mailto:vasiliy.yashkov@red-soft.ru">Vasiliy Yashkov</a>
 * @since 5.0
 */
public class FBTraceManagerTest {

    @RegisterExtension
    @Order(1)
    static final GdsTypeExtension gdsType = GdsTypeExtension.excludes(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME,
            FbOOEmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);

    @RegisterExtension
    final UsesDatabaseExtension.UsesDatabaseForEach usesDatabase = UsesDatabaseExtension.usesDatabase();

    private FBTraceManager traceManager;
    private OutputStream loggingStream;

    private static final String DEFAULT_TABLE = ""
            + "CREATE TABLE TEST ("
            + "     TESTVAL INTEGER NOT NULL"
            + ")";

    private static final String INSERT_DEFAULT_TABLE = "INSERT INTO "
            + " TEST (TESTVAL) VALUES (?)";

    @BeforeEach
    void setUp() {
        loggingStream = new ByteArrayOutputStream();

        traceManager = new FBTraceManager();
        if (getGdsType() == GDSType.getType("PURE_JAVA") || getGdsType() == GDSType.getType("NATIVE")
                || getGdsType() == GDSType.getType("FBOONATIVE")) {
            traceManager.setServerName(DB_SERVER_URL);
            traceManager.setPortNumber(DB_SERVER_PORT);
        }
        traceManager.setUser(DB_USER);
        traceManager.setPassword(DB_PASSWORD);
        traceManager.setDatabase(getDatabasePath());
        traceManager.setLogger(loggingStream);
    }

    private void createTestTable() throws SQLException {
        try (Connection conn = getConnectionViaDriverManager()) {
            Statement stmt = conn.createStatement();
            stmt.execute(DEFAULT_TABLE);
        }
    }

    private void insertTestTable() throws SQLException {
        try (Connection conn = getConnectionViaDriverManager()) {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(INSERT_DEFAULT_TABLE);
            preparedStatement.setInt(1, 1);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.commit();
        }
    }

    @Test
    void testStatementStart() throws Exception {
        final String defaultConfiguration = "database=%[\\\\/]" + FBTestProperties.DB_NAME + "\n" +
                "{\n" +
                "\n" +
                "\tenabled = true\n" +
                "\n" +
                "\tformat = 0\n" +
                "\n" +
                "\tlog_initfini = false\n" +
                "\n" +
                "\tlog_statement_start = true\n" +
                "\n" +
                "}";

        createTestTable();
        final String traceSessionName = "testStatementStart";
        traceManager.startTraceSession(traceSessionName, defaultConfiguration);
        Thread.sleep(2000);

        insertTestTable();

        final Integer sessionId = traceManager.getSessionId(traceSessionName);
        assert sessionId != null;
        traceManager.stopTraceSession(sessionId);
        Thread.sleep(2000);

        final String trace = loggingStream.toString();
        assertThat(trace)
                .describedAs(String.format("The trace log must include 'Trace session ID %d started'", sessionId))
                .contains(String.format("Trace session ID %d started", sessionId))
                .describedAs("The trace log must include 'EXECUTE_STATEMENT_START'")
                .contains("EXECUTE_STATEMENT_START")
                .describedAs("The trace log must include 'INSERT INTO  TEST (TESTVAL) VALUES (?)'")
                .contains("INSERT INTO  TEST (TESTVAL) VALUES (?)")
                .describedAs(String.format("The trace log must include 'Trace session ID %d stopped'", sessionId))
                .contains(String.format("Trace session ID %d stopped", sessionId));
    }
}
