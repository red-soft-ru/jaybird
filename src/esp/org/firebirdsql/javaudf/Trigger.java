package org.firebirdsql.javaudf;

import org.firebirdsql.gds.GDSException;
import org.firebirdsql.gds.IscDbHandle;
import org.firebirdsql.gds.impl.jni.InternalGDSImpl;
import org.firebirdsql.jdbc.FBConnection;
import org.firebirdsql.jdbc.FBSQLException;

import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>Title: Support trigger information </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class Trigger extends InternalGDSImpl {

  public static final int INSERT = 1;
  public static final int UPDATE = 2;
  public static final int DELETE = 3;
  public static final int CONNECT = 4;
  public static final int DISCONNECT = 5;
  public static final int TRANSACTION_START = 6;
  public static final int TRANSACTION_COMMIT = 7;
  public static final int TRANSACTION_ROLLBACK = 8;

  public static final int TEXT_FIELD = 1;
  public static final int CSTRING_FIELD = 2;
  public static final int VARCHAR_FIELD = 3;
  public static final int BYTE_FIELD = 7;
  public static final int SHORT_FIELD = 8;
  public static final int INTEGER_FIELD = 9;
  public static final int FLOAT_FIELD = 11;
  public static final int DOUBLE_FIELD = 12;
  public static final int DATE_FIELD = 14;
  public static final int TIME_FIELD = 15;
  public static final int TIMESTAMP_FIELD = 16;
  public static final int BLOB_FIELD = 17;
  public static final int BIGINT_FIELD = 19;

  public Trigger() throws FBSQLException {
  }

  private IscDbHandle getDbHandle() throws SQLException  {
    FBConnection con = (FBConnection)UDF.getCurrentConnection();
    try {
      return con.getIscDBHandle();
    } catch (GDSException e) {
      throw new SQLException(e.getMessage());
    }
  }

  public String getTable() throws SQLException {
    try {
      return native_isc_get_trigger_table_name();
    } catch (GDSException e) {
      throw new FBSQLException(e);
    }
  }

  public int getTriggerAction() throws SQLException {
    try {
      return native_isc_get_trigger_action();
    }
    catch (GDSException e) {
      throw new FBSQLException(e);
    }
  }

  public String getString_New(String name)throws SQLException {
    return (String)getObject_New(name);
  }


  public Object getObject_New(String name)throws SQLException {
    try {
      final IscDbHandle db_handle = getDbHandle();
      return native_isc_get_trigger_field(name, 1, db_handle);
    } catch(GDSException e) {
      throw new SQLException(e.getMessage());
    }
 }

  public Object getObject_Old(String name)throws SQLException {
    try{
      final IscDbHandle db_handle = getDbHandle();
      return native_isc_get_trigger_field(name, 0, db_handle);
    } catch(GDSException e) {
      throw new SQLException(e.getMessage());
    }
  }

  public int getFieldType(final String fieldName) throws SQLException {
    if (fieldName == null || fieldName.length() == 0) {
      throw new SQLException("Field name is not specified"); 
    }
    try {
      return native_isc_get_trigger_field_type(fieldName, getDbHandle());
    } catch(GDSException e) {
      throw new SQLException(e.getMessage());
    }
  }

  public void setNewValue(final String name, final Object newValue) throws SQLException {
    try{
      if (getFieldType(name) == BLOB_FIELD) {
        throw new SQLException("Feature is not supported for blob fields");
      }
      final IscDbHandle db_handle = getDbHandle();
      native_isc_set_trigger_field(name, 1, newValue, db_handle);
    } catch(GDSException e) {
      throw new SQLException(e.getMessage());
    }
  }

}
