package org.firebirdsql.jca;

import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.jdbc.FBConnection;
import org.firebirdsql.management.FBStatisticsManager;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.firebirdsql.common.DdlHelper.executeCreateTable;
import static org.firebirdsql.common.FBTestProperties.*;
import static org.junit.Assert.assertEquals;

public class TestFBSADataSource extends TestXABase {
    @Test
    public void testFBSADataSource() throws Exception {
        FBSADataSource dataSource = new FBSADataSource();
        // Set the standard properties
        dataSource.setDatabase(DB_DATASOURCE_URL);
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setPassword("masterkey");
        dataSource.setEncoding("WIN1251");
        try {
            dataSource.setLoginTimeout(10);
            Connection c1 = dataSource.getConnection();
            Connection c2 = dataSource.getConnection();
            c1.setAutoCommit(false);
            c2.setAutoCommit(false);
            Statement stmt = c1.createStatement();
            Statement stmt2 = c2.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
            ResultSet rs = stmt.executeQuery("SELECT MON$TRANSACTION_ID,MON$ATTACHMENT_ID FROM MON$TRANSACTIONS");

            while (rs.next()) {
                System.out.println(String.format("MON$TRANSACTION_ID = %s, MON$ATTACHMENT_ID = %s",
                        rs.getString(1), rs.getString(2)));
            }

            while (rs2.next()) {
                System.out.println(String.format("MON$ATTACHMENT_ID = %s, MON$SERVER_PID = %s",
                        rs2.getString(1), rs2.getString(2)));
            }

            stmt.close();

            // At this point, there is no implicit driver instance
            // registered with the driver manager!
            System.out.println("Connection established...");
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
    public void testCreationConnectionAfterReadOnly() throws Exception {
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
        dataSource.setDatabase(DB_DATASOURCE_URL);
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setPassword("masterkey");
        dataSource.setEncoding("WIN1251");
        try {
            dataSource.setLoginTimeout(10);
            FBConnection c1 = (FBConnection) dataSource.getConnection();

            TransactionParameterBuffer tpb = c1.createTransactionParameterBuffer();
            tpb.addArgument(TransactionParameterBuffer.READ_COMMITTED);
            tpb.addArgument(TransactionParameterBuffer.REC_VERSION);
            tpb.addArgument(TransactionParameterBuffer.READ);
            tpb.addArgument(TransactionParameterBuffer.NOWAIT);

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
            System.out.println("Connection established...");
            c1.close();
            c2.close();
            Connection c3 = dataSource.getConnection();
            PreparedStatement stmt3 =
                    c3.prepareStatement("SELECT MON$ATTACHMENT_ID, MON$SERVER_PID FROM MON$ATTACHMENTS");
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
    public void testEncryptedPassword() throws Exception {
        FBSADataSource dataSource = new FBSADataSource();
        // Set the standard properties
        dataSource.setDatabase(DB_DATASOURCE_URL);
        dataSource.setDescription("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setEncoding("WIN1251");

        // Legacy plugin use it
        dataSource.setNonStandardProperty("isc_dpb_password_enc", "QP3LMZ/MJh.");
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

    @Test
    public void testEncryptedPasswordService() throws Exception {
        OutputStream loggingStream = new ByteArrayOutputStream();
        FBStatisticsManager statManager = new FBStatisticsManager(getGdsType());
        if (getGdsType() == GDSType.getType("PURE_JAVA") || getGdsType() == GDSType.getType("NATIVE")) {
            statManager.setServer(DB_SERVER_URL);
            statManager.setPort(DB_SERVER_PORT);
        }
        statManager.setUserName("SYSDBA");
        statManager.setNonStandardProperty("isc_dpb_password_enc", "QP3LMZ/MJh.");
//        statManager.setNonStandardProperty("isc_dpb_password", "masterkey");
        statManager.setDatabase(getDatabasePath());
        statManager.setLogger(loggingStream);

        statManager.getHeaderPage();
        String headerPage = loggingStream.toString();
        System.out.println(headerPage);

    }
}
