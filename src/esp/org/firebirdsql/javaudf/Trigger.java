package org.firebirdsql.javaudf;
import java.sql.SQLException;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.jdbc.FBSQLException;
import org.firebirdsql.gds.impl.jni.InternalGDSImpl;
/**
 * <p>Title: Support trigger information </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class Trigger extends UDF {
  public final static int INSERT=1;
  public final static int UPDATE=2;
  public final static int DELETE=3;
  public Trigger() {
  }

  public static String getTable() throws SQLException {
    try {
      return InternalGDSImpl.native_isc_get_trigger_table_name();
    }
    catch (GDSException e) {
      throw new FBSQLException(e);
    }
  }
 public static int getTriggerAction()throws SQLException
 {
   try {
     return InternalGDSImpl.native_isc_get_trigger_action();
   }
   catch (GDSException e) {
     throw new FBSQLException(e);
   }
 }
 public static String getString_New(String name)throws SQLException
 {
  return (String)getObject_New(name);
 }
 protected static Object getObject_New(String name)throws SQLException
 {
   try{
   Object o = org.firebirdsql.gds.impl.jni.InternalGDSImpl.native_isc_get_trigger_field(
       name, 1);
    return o;
   }catch(GDSException e)
   {
     throw new SQLException(e.getMessage());
   }
 }
 protected static Object getObject_Old(String name)throws SQLException
 {
   try{
   Object o = org.firebirdsql.gds.impl.jni.InternalGDSImpl.native_isc_get_trigger_field(
       name, 0);
    return o;
   }catch(GDSException e)
   {
     throw new FBSQLException(e);
   }
 }
}
