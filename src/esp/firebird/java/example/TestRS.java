package firebird.java.example;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * This class represent computable ResultSet.
 * The quantity of lines of returned result is defined in the constructor.
 *
 */

public class TestRS implements ResultSet {
  int i = 0, n;
  public TestRS(int a) {
    n = a;
    if (n < 5)
      n = 5;
  }

  public TestRS() {
    n = 5;
  }

  public boolean next() throws SQLException
  {
    return i++<n;
  }

  public Object getObject(int columnIndex) throws SQLException
  {
    String r=i+" "+columnIndex;
    return r;
  }
  /**
   * Releases this <code>ResultSet</code> object's database and JDBC resources
   * immediately instead of waiting for this to happen when it is automatically
   * closed.
   *
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void close() throws SQLException
  {
  }

  /**
   * Reports whether the last column read had a value of SQL <code>NULL</code>.
   *
   * @return <code>true</code> if the last column value read was SQL
   *   <code>NULL</code> and <code>false</code> otherwise
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean wasNull() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>String</code> in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public String getString(int columnIndex) throws SQLException
  {
    return "";
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>boolean</code> in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>false</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean getBoolean(int columnIndex) throws SQLException
  {
    return false;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>byte</code> in the Java programming
   * language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public byte getByte(int columnIndex) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>short</code> in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public short getShort(int columnIndex) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>int</code> in the Java programming
   * language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getInt(int columnIndex) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>long</code> in the Java programming
   * language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public long getLong(int columnIndex) throws SQLException
  {
    return 0L;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>float</code> in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public float getFloat(int columnIndex) throws SQLException
  {
    return 0.0F;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>double</code> in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public double getDouble(int columnIndex) throws SQLException
  {
    return 0.0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.BigDecimal</code> in the
   * Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param scale the number of digits to the right of the decimal point
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws
      SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>byte</code> array in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public byte[] getBytes(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Date</code> object in the
   * Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Date getDate(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Time</code> object in the
   * Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Time getTime(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Timestamp</code> object in
   * the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Timestamp getTimestamp(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a stream of ASCII characters.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a Java input stream that delivers the database column value as a
   *   stream of one-byte ASCII characters; if the value is SQL
   *   <code>NULL</code>, the value returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public InputStream getAsciiStream(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as as a stream of two-byte Unicode characters.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a Java input stream that delivers the database column value as a
   *   stream of two-byte Unicode characters; if the value is SQL
   *   <code>NULL</code>, the value returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public InputStream getUnicodeStream(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a binary stream of uninterpreted bytes.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a Java input stream that delivers the database column value as a
   *   stream of uninterpreted bytes; if the value is SQL <code>NULL</code>, the
   *   value returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public InputStream getBinaryStream(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>String</code> in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public String getString(String columnName) throws SQLException
  {
    return "";
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>boolean</code> in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>false</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean getBoolean(String columnName) throws SQLException
  {
    return false;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>byte</code> in the Java programming
   * language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public byte getByte(String columnName) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>short</code> in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public short getShort(String columnName) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>int</code> in the Java programming
   * language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getInt(String columnName) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>long</code> in the Java programming
   * language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public long getLong(String columnName) throws SQLException
  {
    return 0L;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>float</code> in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public float getFloat(String columnName) throws SQLException
  {
    return 0.0F;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>double</code> in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>0</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public double getDouble(String columnName) throws SQLException
  {
    return 0.0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.math.BigDecimal</code> in the
   * Java programming language.
   *
   * @param columnName the SQL name of the column
   * @param scale the number of digits to the right of the decimal point
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public BigDecimal getBigDecimal(String columnName, int scale) throws
      SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>byte</code> array in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public byte[] getBytes(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Date</code> object in the
   * Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Date getDate(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Time</code> object in the
   * Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Time getTime(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Timestamp</code> object.
   *
   * @param columnName the SQL name of the column
   * @return the column value; if the value is SQL <code>NULL</code>, the value
   *   returned is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Timestamp getTimestamp(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a stream of ASCII characters.
   *
   * @param columnName the SQL name of the column
   * @return a Java input stream that delivers the database column value as a
   *   stream of one-byte ASCII characters. If the value is SQL
   *   <code>NULL</code>, the value returned is <code>null</code>.
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public InputStream getAsciiStream(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a stream of two-byte Unicode characters.
   *
   * @param columnName the SQL name of the column
   * @return a Java input stream that delivers the database column value as a
   *   stream of two-byte Unicode characters. If the value is SQL
   *   <code>NULL</code>, the value returned is <code>null</code>.
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public InputStream getUnicodeStream(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a stream of uninterpreted
   * <code>byte</code>s.
   *
   * @param columnName the SQL name of the column
   * @return a Java input stream that delivers the database column value as a
   *   stream of uninterpreted bytes; if the value is SQL <code>NULL</code>, the
   *   result is <code>null</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public InputStream getBinaryStream(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the first warning reported by calls on this <code>ResultSet</code>
   * object.
   *
   * @return the first <code>SQLWarning</code> object reported or
   *   <code>null</code> if there are none
   * @throws SQLException if a database access error occurs or this method is
   *   called on a closed result set
   * @todo Implement this java.sql.ResultSet method
   */
  public SQLWarning getWarnings() throws SQLException
  {
    return null;
  }

  /**
   * Clears all warnings reported on this <code>ResultSet</code> object.
   *
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void clearWarnings() throws SQLException
  {
  }

  /**
   * Retrieves the name of the SQL cursor used by this <code>ResultSet</code>
   * object.
   *
   * @return the SQL name for this <code>ResultSet</code> object's cursor
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public String getCursorName() throws SQLException
  {
    return "";
  }

  /**
   * Retrieves the number, types and properties of this <code>ResultSet</code>
   * object's columns.
   *
   * @return the description of this <code>ResultSet</code> object's columns
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public ResultSetMetaData getMetaData() throws SQLException
  {
    return null;
  }

  /**
   * <p>Gets the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>Object</code> in the Java
   * programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return a <code>java.lang.Object</code> holding the column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */

  /**
   * <p>Gets the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>Object</code> in the Java
   * programming language.
   *
   * @param columnName the SQL name of the column
   * @return a <code>java.lang.Object</code> holding the column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Object getObject(String columnName) throws SQLException
  {
    return "";
  }

  /**
   * Maps the given <code>ResultSet</code> column name to its
   * <code>ResultSet</code> column index.
   *
   * @param columnName the name of the column
   * @return the column index of the given column name
   * @throws SQLException if the <code>ResultSet</code> object does not contain
   *   <code>columnName</code> or a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int findColumn(String columnName) throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.io.Reader</code> object.
   *
   * @return a <code>java.io.Reader</code> object that contains the column
   *   value; if the value is SQL <code>NULL</code>, the value returned is
   *   <code>null</code> in the Java programming language.
   * @param columnIndex the first column is 1, the second is 2, ...
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Reader getCharacterStream(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.io.Reader</code> object.
   *
   * @param columnName the name of the column
   * @return a <code>java.io.Reader</code> object that contains the column
   *   value; if the value is SQL <code>NULL</code>, the value returned is
   *   <code>null</code> in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Reader getCharacterStream(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.math.BigDecimal</code> with
   * full precision.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @return the column value (full precision); if the value is SQL
   *   <code>NULL</code>, the value returned is <code>null</code> in the Java
   *   programming language.
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.math.BigDecimal</code> with
   * full precision.
   *
   * @param columnName the column name
   * @return the column value (full precision); if the value is SQL
   *   <code>NULL</code>, the value returned is <code>null</code> in the Java
   *   programming language.
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public BigDecimal getBigDecimal(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves whether the cursor is before the first row in this
   * <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is before the first row;
   *   <code>false</code> if the cursor is at any other position or the result
   *   set contains no rows
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean isBeforeFirst() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves whether the cursor is after the last row in this
   * <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is after the last row;
   *   <code>false</code> if the cursor is at any other position or the result
   *   set contains no rows
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean isAfterLast() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves whether the cursor is on the first row of this
   * <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on the first row;
   *   <code>false</code> otherwise
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean isFirst() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves whether the cursor is on the last row of this
   * <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on the last row;
   *   <code>false</code> otherwise
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean isLast() throws SQLException
  {
    return false;
  }

  /**
   * Moves the cursor to the front of this <code>ResultSet</code> object, just
   * before the first row.
   *
   * @throws SQLException if a database access error occurs or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public void beforeFirst() throws SQLException
  {
  }

  /**
   * Moves the cursor to the end of this <code>ResultSet</code> object, just
   * after the last row.
   *
   * @throws SQLException if a database access error occurs or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public void afterLast() throws SQLException
  {
  }

  /**
   * Moves the cursor to the first row in this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   *   <code>false</code> if there are no rows in the result set
   * @throws SQLException if a database access error occurs or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean first() throws SQLException
  {
    return false;
  }

  /**
   * Moves the cursor to the last row in this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   *   <code>false</code> if there are no rows in the result set
   * @throws SQLException if a database access error occurs or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean last() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves the current row number.
   *
   * @return the current row number; <code>0</code> if there is no current row
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getRow() throws SQLException
  {
    return 0;
  }

  /**
   * Moves the cursor to the given row number in this <code>ResultSet</code>
   * object.
   *
   * @param row the number of the row to which the cursor should move. A
   *   positive number indicates the row number counting from the beginning of
   *   the result set; a negative number indicates the row number counting from
   *   the end of the result set
   * @return <code>true</code> if the cursor is on the result set;
   *   <code>false</code> otherwise
   * @throws SQLException if a database access error occurs, or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean absolute(int row) throws SQLException
  {
    return false;
  }

  /**
   * Moves the cursor a relative number of rows, either positive or negative.
   *
   * @param rows an <code>int</code> specifying the number of rows to move from
   *   the current row; a positive number moves the cursor forward; a negative
   *   number moves the cursor backward
   * @return <code>true</code> if the cursor is on a row; <code>false</code>
   *   otherwise
   * @throws SQLException if a database access error occurs, there is no current
   *   row, or the result set type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean relative(int rows) throws SQLException
  {
    return false;
  }

  /**
   * Moves the cursor to the previous row in this <code>ResultSet</code> object.
   *
   * @return <code>true</code> if the cursor is on a valid row;
   *   <code>false</code> if it is off the result set
   * @throws SQLException if a database access error occurs or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean previous() throws SQLException
  {
    return false;
  }

  /**
   * Gives a hint as to the direction in which the rows in this
   * <code>ResultSet</code> object will be processed.
   *
   * @param direction an <code>int</code> specifying the suggested fetch
   *   direction; one of <code>ResultSet.FETCH_FORWARD</code>,
   *   <code>ResultSet.FETCH_REVERSE</code>, or
   *   <code>ResultSet.FETCH_UNKNOWN</code>
   * @throws SQLException if a database access error occurs or the result set
   *   type is <code>TYPE_FORWARD_ONLY</code> and the fetch direction is not
   *   <code>FETCH_FORWARD</code>
   * @todo Implement this java.sql.ResultSet method
   */
  public void setFetchDirection(int direction) throws SQLException
  {
  }

  /**
   * Retrieves the fetch direction for this <code>ResultSet</code> object.
   *
   * @return the current fetch direction for this <code>ResultSet</code> object
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getFetchDirection() throws SQLException
  {
    return 0;
  }

  /**
   * Gives the JDBC driver a hint as to the number of rows that should be fetched
   * from the database when more rows are needed for this <code>ResultSet</code>
   * object.
   *
   * @param rows the number of rows to fetch
   * @throws SQLException if a database access error occurs or the condition
   *   <code>0 <= rows <= Statement.getMaxRows()</code> is not satisfied
   * @todo Implement this java.sql.ResultSet method
   */
  public void setFetchSize(int rows) throws SQLException
  {
  }

  /**
   * Retrieves the fetch size for this <code>ResultSet</code> object.
   *
   * @return the current fetch size for this <code>ResultSet</code> object
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getFetchSize() throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the type of this <code>ResultSet</code> object.
   *
   * @return <code>ResultSet.TYPE_FORWARD_ONLY</code>,
   *   <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
   *   <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getType() throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves the concurrency mode of this <code>ResultSet</code> object.
   *
   * @return the concurrency type, either
   *   <code>ResultSet.CONCUR_READ_ONLY</code> or
   *   <code>ResultSet.CONCUR_UPDATABLE</code>
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public int getConcurrency() throws SQLException
  {
    return 0;
  }

  /**
   * Retrieves whether the current row has been updated.
   *
   * @return <code>true</code> if both (1) the row has been visibly updated by
   *   the owner or another and (2) updates are detected
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean rowUpdated() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves whether the current row has had an insertion.
   *
   * @return <code>true</code> if a row has had an insertion and insertions are
   *   detected; <code>false</code> otherwise
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean rowInserted() throws SQLException
  {
    return false;
  }

  /**
   * Retrieves whether a row has been deleted.
   *
   * @return <code>true</code> if a row was deleted and deletions are detected;
   *   <code>false</code> otherwise
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public boolean rowDeleted() throws SQLException
  {
    return false;
  }

  /**
   * Gives a nullable column a null value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateNull(int columnIndex) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>boolean</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBoolean(int columnIndex, boolean x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>byte</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateByte(int columnIndex, byte x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>short</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateShort(int columnIndex, short x) throws SQLException
  {
  }

  /**
   * Updates the designated column with an <code>int</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateInt(int columnIndex, int x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>long</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateLong(int columnIndex, long x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>float</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateFloat(int columnIndex, float x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>double</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateDouble(int columnIndex, double x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.math.BigDecimal</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with a <code>String</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateString(int columnIndex, String x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>byte</code> array value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBytes(int columnIndex, byte[] x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Date</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateDate(int columnIndex, Date x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Time</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateTime(int columnIndex, Time x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Timestamp</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
  {
  }

  /**
   * Updates the designated column with an ascii stream value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param length the length of the stream
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with a binary stream value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param length the length of the stream
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with a character stream value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param length the length of the stream
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @param scale for <code>java.sql.Types.DECIMA</code> or
   *   <code>java.sql.Types.NUMERIC</code> types, this is the number of digits
   *   after the decimal point. For all other types this value will be ignored.
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateObject(int columnIndex, Object x, int scale) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateObject(int columnIndex, Object x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>null</code> value.
   *
   * @param columnName the name of the column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateNull(String columnName) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>boolean</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBoolean(String columnName, boolean x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>byte</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateByte(String columnName, byte x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>short</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateShort(String columnName, short x) throws SQLException
  {
  }

  /**
   * Updates the designated column with an <code>int</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateInt(String columnName, int x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>long</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateLong(String columnName, long x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>float </code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateFloat(String columnName, float x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>double</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateDouble(String columnName, double x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.BigDecimal</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBigDecimal(String columnName, BigDecimal x) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with a <code>String</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateString(String columnName, String x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a byte array value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBytes(String columnName, byte[] x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Date</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateDate(String columnName, Date x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Time</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateTime(String columnName, Time x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Timestamp</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateTimestamp(String columnName, Timestamp x) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with an ascii stream value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @param length the length of the stream
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateAsciiStream(String columnName, InputStream x, int length) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with a binary stream value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @param length the length of the stream
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBinaryStream(String columnName, InputStream x, int length) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with a character stream value.
   *
   * @param columnName the name of the column
   * @param reader the <code>java.io.Reader</code> object containing the new
   *   column value
   * @param length the length of the stream
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateCharacterStream(String columnName, Reader reader,
                                    int length) throws SQLException
  {
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @param scale for <code>java.sql.Types.DECIMAL</code> or
   *   <code>java.sql.Types.NUMERIC</code> types, this is the number of digits
   *   after the decimal point. For all other types this value will be ignored.
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateObject(String columnName, Object x, int scale) throws
      SQLException
  {
  }

  /**
   * Updates the designated column with an <code>Object</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateObject(String columnName, Object x) throws SQLException
  {
  }

  /**
   * Inserts the contents of the insert row into this <code>ResultSet</code>
   * object and into the database.
   *
   * @throws SQLException if a database access error occurs, if this method is
   *   called when the cursor is not on the insert row, or if not all of
   *   non-nullable columns in the insert row have been given a value
   * @todo Implement this java.sql.ResultSet method
   */
  public void insertRow() throws SQLException
  {
  }

  /**
   * Updates the underlying database with the new contents of the current row of
   * this <code>ResultSet</code> object.
   *
   * @throws SQLException if a database access error occurs or if this method is
   *   called when the cursor is on the insert row
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateRow() throws SQLException
  {
  }

  /**
   * Deletes the current row from this <code>ResultSet</code> object and from the
   * underlying database.
   *
   * @throws SQLException if a database access error occurs or if this method is
   *   called when the cursor is on the insert row
   * @todo Implement this java.sql.ResultSet method
   */
  public void deleteRow() throws SQLException
  {
  }

  /**
   * Refreshes the current row with its most recent value in the database.
   *
   * @throws SQLException if a database access error occurs or if this method is
   *   called when the cursor is on the insert row
   * @todo Implement this java.sql.ResultSet method
   */
  public void refreshRow() throws SQLException
  {
  }

  /**
   * Cancels the updates made to the current row in this <code>ResultSet</code>
   * object.
   *
   * @throws SQLException if a database access error occurs or if this method is
   *   called when the cursor is on the insert row
   * @todo Implement this java.sql.ResultSet method
   */
  public void cancelRowUpdates() throws SQLException
  {
  }

  /**
   * Moves the cursor to the insert row.
   *
   * @throws SQLException if a database access error occurs or the result set is
   *   not updatable
   * @todo Implement this java.sql.ResultSet method
   */
  public void moveToInsertRow() throws SQLException
  {
  }

  /**
   * Moves the cursor to the remembered cursor position, usually the current row.
   *
   * @throws SQLException if a database access error occurs or the result set is
   *   not updatable
   * @todo Implement this java.sql.ResultSet method
   */
  public void moveToCurrentRow() throws SQLException
  {
  }

  /**
   * Retrieves the <code>Statement</code> object that produced this
   * <code>ResultSet</code> object.
   *
   * @return the <code>Statment</code> object that produced this
   *   <code>ResultSet</code> object or <code>null</code> if the result set was
   *   produced some other way
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Statement getStatement() throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>Object</code> in the Java
   * programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @param map a <code>java.util.Map</code> object that contains the mapping
   *   from SQL type names to classes in the Java programming language
   * @return an <code>Object</code> in the Java programming language
   *   representing the SQL value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Object getObject(int i, Map map) throws SQLException
  {
    return "";
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>Ref</code> object in the Java
   * programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return a <code>Ref</code> object representing an SQL <code>REF</code> value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Ref getRef(int i) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>Blob</code> object in the Java
   * programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return a <code>Blob</code> object representing the SQL <code>BLOB</code>
   *   value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Blob getBlob(int i) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>Clob</code> object in the Java
   * programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return a <code>Clob</code> object representing the SQL <code>CLOB</code>
   *   value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Clob getClob(int i) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>Array</code> object in the Java
   * programming language.
   *
   * @param i the first column is 1, the second is 2, ...
   * @return an <code>Array</code> object representing the SQL
   *   <code>ARRAY</code> value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Array getArray(int i) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>Object</code> in the Java
   * programming language.
   *
   * @param colName the name of the column from which to retrieve the value
   * @param map a <code>java.util.Map</code> object that contains the mapping
   *   from SQL type names to classes in the Java programming language
   * @return an <code>Object</code> representing the SQL value in the specified
   *   column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Object getObject(String colName, Map map) throws SQLException
  {
    return "";
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>Ref</code> object in the Java
   * programming language.
   *
   * @param colName the column name
   * @return a <code>Ref</code> object representing the SQL <code>REF</code>
   *   value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Ref getRef(String colName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>Blob</code> object in the Java
   * programming language.
   *
   * @param colName the name of the column from which to retrieve the value
   * @return a <code>Blob</code> object representing the SQL <code>BLOB</code>
   *   value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Blob getBlob(String colName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>Clob</code> object in the Java
   * programming language.
   *
   * @param colName the name of the column from which to retrieve the value
   * @return a <code>Clob</code> object representing the SQL <code>CLOB</code>
   *   value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Clob getClob(String colName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as an <code>Array</code> object in the Java
   * programming language.
   *
   * @param colName the name of the column from which to retrieve the value
   * @return an <code>Array</code> object representing the SQL
   *   <code>ARRAY</code> value in the specified column
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Array getArray(String colName) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Date</code> object in the
   * Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object to use in
   *   constructing the date
   * @return the column value as a <code>java.sql.Date</code> object; if the
   *   value is SQL <code>NULL</code>, the value returned is <code>null</code>
   *   in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Date getDate(int columnIndex, Calendar cal) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Date</code> object in the
   * Java programming language.
   *
   * @param columnName the SQL name of the column from which to retrieve the
   *   value
   * @param cal the <code>java.util.Calendar</code> object to use in
   *   constructing the date
   * @return the column value as a <code>java.sql.Date</code> object; if the
   *   value is SQL <code>NULL</code>, the value returned is <code>null</code>
   *   in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Date getDate(String columnName, Calendar cal) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Time</code> object in the
   * Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object to use in
   *   constructing the time
   * @return the column value as a <code>java.sql.Time</code> object; if the
   *   value is SQL <code>NULL</code>, the value returned is <code>null</code>
   *   in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Time getTime(int columnIndex, Calendar cal) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Time</code> object in the
   * Java programming language.
   *
   * @param columnName the SQL name of the column
   * @param cal the <code>java.util.Calendar</code> object to use in
   *   constructing the time
   * @return the column value as a <code>java.sql.Time</code> object; if the
   *   value is SQL <code>NULL</code>, the value returned is <code>null</code>
   *   in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Time getTime(String columnName, Calendar cal) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Timestamp</code> object in
   * the Java programming language.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param cal the <code>java.util.Calendar</code> object to use in
   *   constructing the timestamp
   * @return the column value as a <code>java.sql.Timestamp</code> object; if
   *   the value is SQL <code>NULL</code>, the value returned is
   *   <code>null</code> in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws
      SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.sql.Timestamp</code> object in
   * the Java programming language.
   *
   * @param columnName the SQL name of the column
   * @param cal the <code>java.util.Calendar</code> object to use in
   *   constructing the date
   * @return the column value as a <code>java.sql.Timestamp</code> object; if
   *   the value is SQL <code>NULL</code>, the value returned is
   *   <code>null</code> in the Java programming language
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public Timestamp getTimestamp(String columnName, Calendar cal) throws
      SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.net.URL</code> object in the
   * Java programming language.
   *
   * @param columnIndex the index of the column 1 is the first, 2 is the
   *   second,...
   * @return the column value as a <code>java.net.URL</code> object; if the
   *   value is SQL <code>NULL</code>, the value returned is <code>null</code>
   *   in the Java programming language
   * @throws SQLException if a database access error occurs, or if a URL is
   *   malformed
   * @todo Implement this java.sql.ResultSet method
   */
  public URL getURL(int columnIndex) throws SQLException
  {
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row of this
   * <code>ResultSet</code> object as a <code>java.net.URL</code> object in the
   * Java programming language.
   *
   * @param columnName the SQL name of the column
   * @return the column value as a <code>java.net.URL</code> object; if the
   *   value is SQL <code>NULL</code>, the value returned is <code>null</code>
   *   in the Java programming language
   * @throws SQLException if a database access error occurs or if a URL is
   *   malformed
   * @todo Implement this java.sql.ResultSet method
   */
  public URL getURL(String columnName) throws SQLException
  {
    return null;
  }

  /**
   * Updates the designated column with a <code>java.sql.Ref</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateRef(int columnIndex, Ref x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Ref</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateRef(String columnName, Ref x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Blob</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBlob(int columnIndex, Blob x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Blob</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateBlob(String columnName, Blob x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Clob</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateClob(int columnIndex, Clob x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Clob</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateClob(String columnName, Clob x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Array</code> value.
   *
   * @param columnIndex the first column is 1, the second is 2, ...
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateArray(int columnIndex, Array x) throws SQLException
  {
  }

  /**
   * Updates the designated column with a <code>java.sql.Array</code> value.
   *
   * @param columnName the name of the column
   * @param x the new column value
   * @throws SQLException if a database access error occurs
   * @todo Implement this java.sql.ResultSet method
   */
  public void updateArray(String columnName, Array x) throws SQLException
  {
  }

}
