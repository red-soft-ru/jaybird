package org.firebirdsql.jca;

// NS: This test is broken!
public class TestFBSADataSource
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		org.firebirdsql.jca.FBSADataSource dataSource = 
	        new org.firebirdsql.jca.FBSADataSource();

	    // Set the standard properties
	    dataSource.setDatabase ("localhost/3050:D:/work/db/db_jdbc.fdb");
	    dataSource.setDescription ("An example database of employees");
	    dataSource.setUserName("sysdba");
	    dataSource.setPassword("masterkey");
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
	        dataSource.close();
//	        rs = stmt3.executeQuery();
	      }
	      catch (Exception e) {
	  		e.printStackTrace();
	        System.out.println ("sql exception: " + e.getMessage ());
	      }

	}

}
