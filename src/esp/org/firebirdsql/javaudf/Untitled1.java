package org.firebirdsql.javaudf;
import java.sql.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Untitled1 {
  public static String test(Object a,Object b)
  {
  if(a==null)
    a="";
  if(b==null)
    b="";
  return a.toString()+b.toString();
  }
  public Untitled1() {  }

  public static java.sql.ResultSet testesp()
  {
   return new TestRS(7);
  }
  public static java.sql.ResultSet testesp(int n)
  {
   return new TestRS(n);
  }
  public static void main(String[] args) {
  /*  String s =
        CallJavaMethod.call("java.lang.System.currentTimeMillis", new Object[0]).toString();
    System.out.println(s);
      s=CallJavaMethod.call("org.firebirdsql.javaudf.Untitled1.test", new Object[]{
        "first "," last "
        }).toString();
    System.out.println(s);
  */
  }
  public static String testf()throws org.firebirdsql.gds.GDSException
  {
   Object o=org.firebirdsql.gds.impl.jni.InternalGDSImpl.native_isc_get_trigger_field("ID",1);
   /*
   if(o==null)
    return null;
   return o.toString();
  */
   return "test";
  }
  public static int tests()throws SQLException
  {
    try{
    Class.forName("org.firebirdsql.jdbc.FBDriver");
    }
    catch(Exception e){e.printStackTrace();}
    Connection c=java.sql.DriverManager.getConnection("jdbc:default:connection:");
    PreparedStatement ps=c.prepareStatement("select counT(*) from rdb$relations");
    ResultSet rs=ps.executeQuery();
    int r=-1;
    rs.next();
    r=rs.getInt(1);
    ps.close();
    return r;
  }


}
