package org.firebirdsql.jca;

import org.junit.Test;

import static org.firebirdsql.common.FBTestProperties.DB_DATASOURCE_URL;

public class TestFBSADataSource extends TestXABase
{
	@Test
	public void testFBSADataSource() throws Exception {

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
			java.sql.Connection c1 = dataSource.getConnection ();
			java.sql.Connection c2 = dataSource.getConnection ();
			c1.setAutoCommit(false);
			c2.setAutoCommit(false);
			java.sql.Statement stmt = c1.createStatement();
			java.sql.Statement stmt2 = c2.createStatement();
			java.sql.ResultSet rs2 = stmt2.executeQuery("SELECT MON$ATTACHMENT_ID,MON$SERVER_PID FROM MON$ATTACHMENTS");
//	        java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM MON$ATTACHMENTS");
			java.sql.ResultSet rs = stmt.executeQuery("SELECT MON$TRANSACTION_ID,MON$ATTACHMENT_ID FROM MON$TRANSACTIONS");
			while(rs.next())
				System.out.println("MON$TRANSACTION_ID = " + rs.getString(1) + ", MON$ATTACHMENT_ID = " + rs.getString(2));

			while(rs2.next())
				System.out.println("MON$ATTACHMENT_ID = " + rs2.getString(1) + ", MON$SERVER_PID = " + rs2.getString(2));
			stmt.close();

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
}
