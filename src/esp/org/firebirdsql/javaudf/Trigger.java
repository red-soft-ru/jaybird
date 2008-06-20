package org.firebirdsql.javaudf;
import java.sql.SQLException;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.IscDbHandle;
import org.firebirdsql.jdbc.FBSQLException;
import org.firebirdsql.gds.impl.jni.InternalGDSImpl;
import org.firebirdsql.gds.impl.jni.isc_db_handle_impl;

/**
 * <p>Title: Support trigger information </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class Trigger extends InternalGDSImpl {
  public final static int INSERT=1;
  public final static int UPDATE=2;
  public final static int DELETE=3;
  public final static int CONNECT=4;
  public final static int DISCONNECT=5;
  public final static int TRANSACTION_START=6;
  public final static int TRANSACTION_COMMIT=7;
  public final static int TRANSACTION_ROLLBACK=8;

    IscDbHandle db_handle;

  public Trigger() throws FBSQLException {
      super();
      db_handle = new isc_db_handle_impl();
      try {
          native_isc_get_curret_attachment_and_transactional(null, db_handle);
      } catch (GDSException e) {
          e.printStackTrace();
      }
  }

  public static java.sql.Connection getCurrentConnection() throws java.sql.SQLException {
      return java.sql.DriverManager.getConnection("jdbc:default:connection:");
  }

  public String getTable() throws SQLException {
    try {
      return native_isc_get_trigger_table_name();
    }
    catch (GDSException e) {
      throw new FBSQLException(e);
    }
  }

 public int getTriggerAction()throws SQLException
 {
   try {
     return native_isc_get_trigger_action();
   }
   catch (GDSException e) {
     throw new FBSQLException(e);
   }
 }

 public String getString_New(String name)throws SQLException
 {
  return (String)getObject_New(name);
 }

 public Object getObject_New(String name)throws SQLException
 {
   try {
     Object o = native_isc_get_trigger_field(name, 1, db_handle);
     return o;
   }catch(GDSException e)
   {
     throw new SQLException(e.getMessage());
   }
 }
    
 public Object getObject_Old(String name)throws SQLException
 {
   try{
     Object o = native_isc_get_trigger_field(name, 0, db_handle);
     return o;
   }catch(GDSException e)
   {
     throw new SQLException(e);
   }
 }

 public void setNewValue(String name, Object newValue) throws SQLException {
   try{
     native_isc_set_trigger_field(name, 1, newValue, db_handle);
   }catch(GDSException e)
   {
     throw new SQLException(e);
   }
 }

}
