package org.firebirdsql.javaudf;

/**
 * <p>Title: Support UDF only getCurrentConnection()</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class UDF {
  /**
   * Method returns getCurrentConnection
   *
   * @throws SQLException
   * @return Connection
   */
  public static java.sql.Connection getCurrentConnection() throws java.sql.SQLException {
//      try {
//	  Class.forName("org.firebirdsql.jdbc.FBDriver");
//      } catch (ClassNotFoundException ex) {}
      return java.sql.DriverManager.getConnection("jdbc:default:connection:");
  }
}
