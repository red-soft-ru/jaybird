package org.firebirdsql.jaybird.xca;

import org.firebirdsql.common.extension.GdsTypeExtension;
import org.firebirdsql.common.extension.UsesDatabaseExtension;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.jni.EmbeddedGDSFactoryPlugin;
import org.firebirdsql.gds.impl.jni.FbOOEmbeddedGDSFactoryPlugin;
import org.firebirdsql.jaybird.fb.constants.TpbItems;
import org.firebirdsql.jdbc.FBConnection;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.firebirdsql.common.DdlHelper.executeCreateTable;
import static org.firebirdsql.common.FBTestProperties.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FBSADataSourceTest {

    @RegisterExtension
    @Order(1)
    static final GdsTypeExtension gdsType = GdsTypeExtension.excludes(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME,
            FbOOEmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);

    @RegisterExtension
    @Order(2)
    static final UsesDatabaseExtension.UsesDatabaseForAll usesDatabase = UsesDatabaseExtension.usesDatabaseForAll();

    private String blockQuery =
    // @formatter:off
            "execute block\n" +
            "returns (v_out bigint)" +
            "as\n" +
            "  declare id integer = 1;\n" +
            "begin\n" +
            "  while (id <= 1000) do\n" +
            "  begin\n" +
            "    v_out = id;\n" +
            "    suspend;\n" +
            "    id = id + 1;\n" +
            "  end\n" +
            "end";
    // @formatter:on

    private class ReadThread extends Thread {

        private Object hash;
        private final String threadName;
        private final Connection connection;

        public ReadThread(final int n, final Connection connection) {
            this.threadName = "Thread " + n;
            this.connection = connection;
        }

        public void run(){
            this.setName(threadName);
            System.out.println(threadName + " is running");
            try {
                PreparedStatement ps = connection.prepareStatement(blockQuery);
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(threadName + ", Value: " + rs.getInt(1));
                    }
                }
                ps.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testFBSADataSourceMultithread() throws Exception {

        final FBSADataSource dataSource = new FBSADataSource();

        // Set the standard properties
        dataSource.setDatabase("localhost:employee");
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setEncoding(DB_LC_CTYPE);
        try {
            final int size = 3;
            final Thread[] readThreads = new Thread[size];
            for (int i = 0; i < size; i++) {
                readThreads[i] = new Thread(new ReadThread(i + 1, dataSource.getConnection()));
                readThreads[i].setName("Thread " + i + 1);
                readThreads[i].start();
            }
            for (int j = 0; j < size; j++) {
                readThreads[j].join();
            }

        } finally {
            dataSource.close();
        }
    }

    @Test
    void testFBSADataSource() throws Exception {

        final FBSADataSource dataSource = new FBSADataSource();

        // Set the standard properties
        dataSource.setDatabaseName(getdbpath(DB_NAME));
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setEncoding(DB_LC_CTYPE);
        try {
            dataSource.setLoginTimeout(10);
            final Connection c1 = dataSource.getConnection();
            final Connection c2 = dataSource.getConnection();
            c1.setAutoCommit(false);
            c2.setAutoCommit(false);
            final Statement stmt = c1.createStatement();
            final Statement stmt2 = c2.createStatement();
            final ResultSet rs2 = stmt2.executeQuery("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
            final ResultSet rs = stmt.executeQuery("SELECT MON$TRANSACTION_ID,MON$ATTACHMENT_ID FROM MON$TRANSACTIONS");
            final List<Integer> attIDs = new ArrayList<>();

            while (rs.next()) {
                attIDs.add(rs.getInt(2));
                System.out.println(String.format("MON$TRANSACTION_ID = %s, MON$ATTACHMENT_ID = %s",
                        rs.getString(1), rs.getString(2)));
            }

            rs.close();

            assert attIDs.get(0).equals(attIDs.get(1));

            while (rs2.next()) {
                attIDs.add(rs2.getInt(1));
                System.out.println("MON$ATTACHMENT_ID = " + rs2.getString(1) + ", MON$SERVER_PID = " + rs2.getString(2));
            }

            rs2.close();

            assert attIDs.get(0).equals(attIDs.get(2));

            stmt.close();
            stmt2.close();

            // At this point, there is no implicit driver instance
            // registered with the driver manager!
            System.out.println("got connection");
            c1.close();
            c2.close();
            final Connection c3 = dataSource.getConnection();
            final PreparedStatement stmt3 =
                    c3.prepareStatement("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
            final ResultSet rs3 = stmt3.executeQuery();
            while (rs3.next()) {
                System.out.println(rs3.getInt(1));
            }
            rs3.close();
            c3.close();
        } finally {
            dataSource.close();
        }
    }

    @Test
    void testCreationConnectionAfterReadOnly() throws Exception {
        final String CREATE_TABLE =
                "CREATE TABLE test (" +
                        "  col1 INTEGER" +
                        ")";

        final String INSERT_DATA = "INSERT INTO test(col1) VALUES(?)";

        Connection connection = getConnectionViaDriverManager();
        executeCreateTable(connection, CREATE_TABLE);

        connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(INSERT_DATA);
        ps.setInt(1, 1);
        ps.execute();
        ps.close();

        connection.commit();
        connection.close();

        FBSADataSource dataSource = new FBSADataSource();

        // Set the standard properties
        dataSource.setDatabaseName(getdbpath(DB_NAME));
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setPassword("masterkey");
        dataSource.setEncoding("WIN1251");
        try {
            dataSource.setLoginTimeout(10);
            FBConnection c1 = (FBConnection) dataSource.getConnection();


            TransactionParameterBuffer tpb = c1.createTransactionParameterBuffer();
            tpb.addArgument(TpbItems.isc_tpb_read_committed);
            tpb.addArgument(TpbItems.isc_tpb_rec_version);
            tpb.addArgument(TpbItems.isc_tpb_read);
            tpb.addArgument(TpbItems.isc_tpb_nowait);

            c1.setTransactionParameters(tpb);

            FBConnection c2 = (FBConnection) dataSource.getConnection();

            c1.setAutoCommit(false);
            c2.setAutoCommit(false);
            Statement stmt = c1.createStatement();
            Statement stmt2 = c2.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MON$TRANSACTION_ID,MON$ATTACHMENT_ID FROM MON$TRANSACTIONS");
            int update = stmt2.executeUpdate("UPDATE test SET col1 = 2");

            assertEquals(1, update);

            while (rs.next()) {
                System.out.println(String.format("MON$TRANSACTION_ID = %s, MON$ATTACHMENT_ID = %s",
                        rs.getString(1), rs.getString(2)));
            }

            stmt.close();
            stmt2.close();

            // At this point, there is no implicit driver instance
            // registered with the driver manager!
            System.out.println("got connection");
            c1.close();
            c2.close();
            Connection c3 = dataSource.getConnection();
            PreparedStatement stmt3 = c3.prepareStatement("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
            rs = stmt3.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
            rs.close();
            c3.close();
        } finally {
            dataSource.close();
        }
    }

    @Test
    void testEncryptedPassword() throws Exception {
        FBSADataSource dataSource = new FBSADataSource();
        // Set the standard properties
        dataSource.setDatabaseName(getdbpath(DB_NAME));
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setEncoding("WIN1251");

        // Legacy plugin use it
        dataSource.setProperty("isc_dpb_password_enc", "QP3LMZ/MJh.");
        dataSource.setProperty("authPlugins", "Legacy_Auth");
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "select rdb$get_context('SYSTEM', 'ENGINE_VERSION') from rdb$database");
            ResultSet rs = ps.executeQuery();
            rs.next();
            System.out.println(String.format("Engine version: %s", rs.getString(1)));
            rs.close();
            ps.close();
        } finally {
            dataSource.close();
        }
    }
}
