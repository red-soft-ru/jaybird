package org.firebirdsql.javaudf;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.io.IOException;
import org.firebirdsql.jdbc.FirebirdConnection;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class Examples  extends UDF{
  /**
   * now
   * function return current date and time
   * @return Timestamp
   */
  public static Timestamp now() {
    return new Timestamp(System.currentTimeMillis());
  }

  /**
   * Function format date and time by mask
   *
   * @param format String
   * @param date Date
   * @return String
   */
  public static String formatDate(String format, java.util.Date date) {
    SimpleDateFormat df = new SimpleDateFormat(format);
    return df.format(date);
  }

  /**
   * Function format date and time by mask and locale name
   *
   * @param format String
   * @param date Date
   * @param locale String
   * @return String
   */
  public static String formatDate(String format, java.util.Date date,String locale) {
    SimpleDateFormat df = new SimpleDateFormat(format,new Locale(locale));
    return df.format(date);
  }

  /**
   * Calculate row count in table
   *
   * @param tableName String
   * @throws SQLException
   * @return int
   */
  public static int tableCount(String tableName) throws SQLException {
    Connection c = getCurrentConnection();
    try {
      PreparedStatement ps = c.prepareStatement("select counT(*) from " +
                                                tableName);
      try {
        ResultSet rs = ps.executeQuery();
        try {
          rs.next();
          return rs.getInt(1);
        }
        finally {
          rs.close();
        }
      }
      finally {
        ps.close();
      }
    }
    finally {
      c.close();
    }
  }

  /**
   * lower string
   *
   * @param s String
   * @return String
   */
  public static String lower(String s)
 {
  return s==null?null:s.toLowerCase();
 }

  /**
   * lower string with locale name
   *
   * @param s String
   * @param locale String
   * @return String
   */
  public static String lower(String s,String locale)
 {
  return s==null?null:s.toLowerCase(new Locale(locale));
 }

  /**
   * ESP demonstrated select from current DB
   *
   * @throws SQLException
   * @return ResultSet
   */
  public static ResultSet getFieldName()throws SQLException
 {
   Connection c = getCurrentConnection();
   PreparedStatement ps = c.prepareStatement(
       "select rdb$field_name,count(rdb$field_name) from rdb$relation_fields group by rdb$field_name");
   ResultSet rs = ps.executeQuery();
   return rs;
 }
 /**
  * ESP demonstrated select from external DB
  *
   *
   * @param dbUrl String    jdbc url
   * @param user String     user name
   * @param password String password
   * @throws SQLException
   * @return ResultSet
   */
  public static ResultSet getFieldName(String dbUrl, String user, String password) throws SQLException, ClassNotFoundException
  {
    Class.forName("org.firebirdsql.jdbc.FBDriver");
    Connection c = DriverManager.getConnection(dbUrl, user, password);
    PreparedStatement ps = c.prepareStatement(
        "select rdb$field_name,count(rdb$field_name) from rdb$relation_fields group by rdb$field_name");
    ResultSet rs = ps.executeQuery();
    return rs;
  }

  /**
   * getChildCount
   * Caclulate child count in tree.
   * Demonstrate recursion call UDF
   * @param id int node id
   * @throws SQLException
   * @return int
   */
  public static int getChildCount(int id)throws SQLException
  {
    Connection c = java.sql.DriverManager.getConnection("jdbc:default:connection:");
    try {
      PreparedStatement ps = c.prepareStatement("select sum(treecount(id)) from tree where id_p=?");
      try {
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();
        try {
          rs.next();
          return rs.getInt(1)+1;
        }
        finally {
          rs.close();
        }
      }
      finally {
        ps.close();
      }
    }
    finally {
      c.close();
    }
  }

  /**
   * getBlobLen
   * Return length of Blob
   * @param b FBBlob
   * @throws SQLException
   * @throws IOException
   * @return long
   */
  public static long getBlobLen(org.firebirdsql.jdbc.FBBlob b)throws SQLException,IOException
  {
//    if(b==null)
	return -1;
//    try{
//      return b.length();
//    }finally{b.close(true);}
  }

//  public static java.sql.ResultSet getActiveTransaction()throws SQLException{
//      FirebirdConnection c = (FirebirdConnection) getCurrentConnection();
//      try{
//          return new ActiveTransactionsRs(c.getActiveTransactions());
//      }finally{
//          c.close();
//      }
//  }
}
