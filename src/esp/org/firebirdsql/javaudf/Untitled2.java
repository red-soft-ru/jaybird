package org.firebirdsql.javaudf;
import java.sql.*;
import java.io.FileOutputStream;
import java.io.PrintStream;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Untitled2 {
//  public static String user = "SYSDBA",ruser = "REPDBA";
//  public static String URL =
//      "jdbc:firebirdsql:native:192.168.1.2:/db/db.gdb";
//      "jdbc:firebirdsql:native:localhost/3050:s:/db/db1.gdb";
//  "jdbc:firebirdsql:native:localhost/3050:s:/db/db2.gdb";
  /**
   * Пароль на подключение к БД
   */
  static String password = "masterkey";
  static String driverName = "org.firebirdsql.jdbc.FBDriver";
  static String characterSet = "Cp1251";

  /**
   * Драйвер БД
   */
  static java.sql.Driver d = null;
//  static org.firebirdsql.jdbc.FBConnection c = null;
  public static FileOutputStream out=null;
  public static PrintStream ps=null;
  static{
   try{
    out=new FileOutputStream("d:/tets.sql.fbj");
    ps=new PrintStream(out);
//    try{
//      ps.println("start");
//      Class.forName(driverName);
//      ps.println("driver");
//      d = java.sql.DriverManager.getDriver(URL);
//      java.util.Properties connectionProperties = new java.util.Properties();
//      connectionProperties.put("user", user);
//      connectionProperties.put("password", password);
//      connectionProperties.put("charSet", characterSet);
//      connectionProperties.put("lc_ctype", "WIN1251");
//      c = (org.firebirdsql.jdbc.FBConnection)d.connect(URL, connectionProperties);
//      c.setAutoCommit(false);
//      c.setTransactionIsolation(c.TRANSACTION_READ_COMMITTED);
//    }catch(Exception e){e.printStackTrace(ps);}
    ps.println("ок");
   }catch(Exception e){e.printStackTrace(ps);}
  }
  public static String getV()
  {
    int r=-1;
   try{
//     ps.print("c.getIscDBHandle()==");
//     ps.println(((org.firebirdsql.ngds.isc_db_handle_impl)c.getIscDBHandle()).getRdb_id());
//     ((org.firebirdsql.ngds.isc_db_handle_impl)c.getIscDBHandle()).setRdb_id(org.firebirdsql.ngds.GDS_Impl.db_hanlde);
//     ps.print("org.firebirdsql.ngds.GDS_Impl.db_hanlde==");
//     ps.println(org.firebirdsql.ngds.GDS_Impl.db_hanlde);
//     ps.print("org.firebirdsql.ngds.GDS_Impl.tr_handle==");
//     ps.println(org.firebirdsql.ngds.GDS_Impl.tr_handle);
//    PreparedStatement ps=c.prepareStatement("select count(*) from vtree where howold=?");
//    ps.setInt(1,0);
//    ResultSet rs=ps.executeQuery();
//
//    if(rs.next())
//      r=rs.getInt(1);
   }catch(Exception e){e.printStackTrace(ps);}

   return Integer.toString(r)+'\0';
  }
  public Untitled2() {
  }
  public static void main(String[] args) {
//    System.out.println(getV());
      System.out.println(Integer.TYPE.toString());
  }

}