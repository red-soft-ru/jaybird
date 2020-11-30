package org.firebirdsql.jaybird.xca;

import org.firebirdsql.jaybird.xca.FBSADataSource;
import org.firebirdsql.jaybird.xca.TestXABase;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.firebirdsql.common.FBTestProperties.*;

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
}
