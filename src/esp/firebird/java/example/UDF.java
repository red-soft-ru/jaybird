package firebird.java.example;

/**
 * Utility class that provides JDBC connection for the current execution context.
 *
 * @author Eugeney Putilin, Copyright (c) 2005
 * @version 1.0
 */
public class UDF {

  /**
  * Obtain JDBC connection for the current context. This is an entry point
  * if Java code needs to perform some operations in context of current
  * transaction.
  *
  * @return Connection instance of {@link java.sql.Connection}
  *
  * @throws java.sql.SQLException if connection cannot be obtained.
  */
  public static java.sql.Connection getCurrentConnection() throws java.sql.SQLException {
    try {
      Class.forName("org.firebirdsql.jdbc.FBDriver");
    } catch (ClassNotFoundException ex) {
      throw new java.sql.SQLException(ex.getMessage());
    }
    return java.sql.DriverManager.getConnection("jdbc:default:connection:");
    }

    public static java.sql.Connection getCurrentNewTrConnection() throws java.sql.SQLException {
      try {
        Class.forName("org.firebirdsql.jdbc.FBDriver");
      }
      catch (ClassNotFoundException ex) {
        throw new java.sql.SQLException(ex.getMessage());
      }
      return java.sql.DriverManager.getConnection("jdbc:new:connection:");
    }
}
