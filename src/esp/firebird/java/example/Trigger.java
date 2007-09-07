package firebird.java.example;

import java.sql.SQLException;
import org.firebirdsql.gds.GDSException;
import org.firebirdsql.jdbc.FBSQLException;
import org.firebirdsql.gds.impl.jni.InternalGDSImpl;

/**
 * Helper class for Java routines invoked from trigger context.
 *
 * @author Eugeney Putilin, Copyright (c) 2005
 * @version 1.0
 */
public class Trigger extends UDF {

    /**
     * Constant telling that trigger was invoked for INSERT action.
     */
    public final static int ACTION_INSERT = 1;

    /**
     * Constant telling that trigger was invoked for UPDATE action.
     */
    public final static int ACTION_UPDATE = 2;

    /**
     * Constant telling that trigger was invoked for DELETE action.
     */
    public final static int ACTION_DELETE = 3;

    /**
     * Get name of the table trigger of which was fired.
     *
     * @return name of the table.
     *
     * @throws SQLException if name cannot be obtained.
     */
    public static String getTableName() throws SQLException {
        try {
            return InternalGDSImpl.native_isc_get_trigger_table_name();
        } catch (GDSException e) {
            throw new FBSQLException(e);
        }
    }

    /**
     * Get action that fired the trigger.
     *
     * @return one of the {@link #ACTION_INSERT}, {@link #ACTION_UPDATE} or
     * {@link #ACTION_DELETE}.
     *
     * @throws SQLException if action cannot be obtained.
     */
    public static int getTriggerAction() throws SQLException {
        try {
            return InternalGDSImpl.native_isc_get_trigger_action();
        } catch (GDSException e) {
            throw new FBSQLException(e);
        }
    }

    /**
     * Get value of the "new.<name>" variable as string. This is a convenenience
     * method that casts result of {@link #getNewValue(String)} to {@link String}.
     *
     * @param columnName name of the column, new value of which should be
     * obtained.
     *
     * @return value of the specified column.
     *
     * @throws SQLException if the value cannot be obtained.
     */
    public static String getNewAsString(String columnName) throws SQLException {
        return (String) getNewValue(columnName);
    }

    /**
     * Get value of the "new.<columnName>" variable as object.
     *
     * @param name name of the column, new value of which should be returned.
     *
     * @return value of the specified column as object (primitive types are
     * converted into corresponding objects).
     *
     * @throws SQLException if value cannot be obtained.
     */
    public static Object getNewValue(String name) throws SQLException {
        try {
            Object o = InternalGDSImpl.native_isc_get_trigger_field(name, 1);
            return o;
        } catch (GDSException e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Get value of the "old.<columnName>" variable as object.
     *
     * @param name name of the column, new value of which should be returned.
     *
     * @return value of the specified column as object (primitive types are
     * converted into corresponding objects).
     *
     * @throws SQLException if value cannot be obtained.
     */
    public static Object getOldValue(String name) throws SQLException {
        try {
            Object o = InternalGDSImpl.native_isc_get_trigger_field(name, 0);
            return o;
        } catch (GDSException e) {
            throw new FBSQLException(e);
        }
    }
}
