package org.firebirdsql.jca;

import org.firebirdsql.cryptoapi.AuthCryptoPluginImpl;
import org.firebirdsql.cryptoapi.cryptopro.exception.CryptoException;
import org.firebirdsql.gds.TransactionParameterBuffer;
import org.firebirdsql.gds.impl.wire.auth.AuthCryptoPlugin;
import org.firebirdsql.jdbc.FBConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.firebirdsql.common.DdlHelper.executeCreateTable;
import static org.firebirdsql.common.FBTestProperties.*;
import static org.junit.Assert.assertEquals;

public class TestFBSADataSource extends TestXABase {

    @Test
    public void testFBSADataSource() throws Exception {

        final FBSADataSource dataSource = new FBSADataSource();

        // Set the standard properties
        dataSource.setDatabase(DB_DATASOURCE_URL);
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
                System.out.println("MON$TRANSACTION_ID = " + rs.getString(1) + ", MON$ATTACHMENT_ID = " + rs.getString(2));
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
            final PreparedStatement stmt3 = c3.prepareStatement("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
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

        org.firebirdsql.jca.FBSADataSource dataSource =
                new org.firebirdsql.jca.FBSADataSource();

        // Set the standard properties
        dataSource.setDatabase (DB_DATASOURCE_URL);
        dataSource.setDescription ("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setPassword("masterkey");
        dataSource.setEncoding("WIN1251");
        try {
            dataSource.setLoginTimeout (10);
            FBConnection c1 = (FBConnection) dataSource.getConnection ();


            TransactionParameterBuffer tpb = c1.createTransactionParameterBuffer();
            tpb.addArgument(TransactionParameterBuffer.READ_COMMITTED);
            tpb.addArgument(TransactionParameterBuffer.REC_VERSION);
            tpb.addArgument(TransactionParameterBuffer.READ);
            tpb.addArgument(TransactionParameterBuffer.NOWAIT);

            c1.setTransactionParameters(tpb);

            FBConnection c2 = (FBConnection) dataSource.getConnection ();

            c1.setAutoCommit(false);
            c2.setAutoCommit(false);
            java.sql.Statement stmt = c1.createStatement();
            java.sql.Statement stmt2 = c2.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT MON$TRANSACTION_ID,MON$ATTACHMENT_ID FROM MON$TRANSACTIONS");
            int update = stmt2.executeUpdate("UPDATE test SET col1 = 2");

            assertEquals(1, update);

            while(rs.next())
                System.out.println("MON$TRANSACTION_ID = " + rs.getString(1) + ", MON$ATTACHMENT_ID = " + rs.getString(2));

            stmt.close();
            stmt2.close();

            // At this point, there is no implicit driver instance
            // registered with the driver manager!
            System.out.println ("got connection");
            c1.close ();
            c2.close ();
            java.sql.Connection c3 = dataSource.getConnection ();
            java.sql.PreparedStatement stmt3 = c3.prepareStatement("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
            rs = stmt3.executeQuery();
            while (rs.next()){
                System.out.println(rs.getInt(1));
            }
            rs.close();
            c3.close();
        }
        finally {
            dataSource.close();
        }
    }

    @Test
    public void testEncryptedPassword() throws Exception {

        FBSADataSource dataSource =	new FBSADataSource();
        // Set the standard properties
        dataSource.setDatabase (DB_DATASOURCE_URL);
        dataSource.setDescription ("An example database of employees");
        dataSource.setUserName("sysdba");
        dataSource.setEncoding("WIN1251");

        // Legacy plugin use it
        dataSource.setNonStandardProperty("isc_dpb_password_enc", "QP3LMZ/MJh.");
        dataSource.setNonStandardProperty("authPlugins", "Legacy_Auth");
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "select rdb$get_context('SYSTEM', 'ENGINE_VERSION') from rdb$database");
            ResultSet rs = ps.executeQuery();
            rs.next();
            System.out.println(String.format("Engine version: %s", rs.getString(1)));
            rs.close();
            ps.close();
        }
        finally {
            dataSource.close();
        }
    }
}
