
package firebird.java.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.io.IOException;

/**
 * Example UDFs written in Java language that can be executed within the
 * Firebird database engine.
 *
 * @author Eugeney Putilin, Copyright (c) 2005
 * @version 1.0
 */

public class Examples extends UDF {

    /**
     * Get current time as timestamp.
     *
     * @return Timestamp
     */
    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Function format date and time by mask
     *
     * @param format
     *            String
     * @param date
     *            Date
     * @return String
     */
    public static String formatDate(String format, java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * Function format date and time by mask and locale name
     *
     * @param format
     *            String
     * @param date
     *            Date
     * @param locale
     *            String
     * @return String
     */
    public static String formatDate(String format, java.util.Date date,
            String locale) {
        SimpleDateFormat df = new SimpleDateFormat(format, new Locale(locale));
        return df.format(date);
    }

    /**
     * Calculate row count in table specified by table name. This is a simple
     * UDF example that opens connection within the current execution context and
     * performs "SELECT count(*) FROM <tableName>".
     *
     * @param tableName name of the table in which rows should be counted.
     *
     * @return int rows count in the specified table.
     *
     * @throws SQLException if something went wrong.
     */
    public static int tableCount(String tableName) throws SQLException {
        Connection c = getCurrentConnection();
        try {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT count(*) FROM " + tableName);
            try {
                ResultSet rs = ps.executeQuery();
                try {
                    rs.next();
                    return rs.getInt(1);
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }
        } finally {
            c.close();
        }
    }

    /**
     * Simple Java UDF that converts the specified string into lower case.
     *
     * @param s string to convert.
     *
     * @return String string in lower case.
     */
    public static String lower(String s) {
        return s == null ? null : s.toLowerCase();
    }

    /**
     * Simple Java UDF that converts the specified string into lower case using
     * the specified locale.
     *
     * @param s string to convert.
     * @param locale locale to use.
     *
     * @return String converted string.
     */
    public static String lower(String s, String locale) {
        return s == null ? null : s.toLowerCase(new Locale(locale));
    }

    /**
     * Simple selectable procedure in Java language. This method opens
     * connection in current execution context, selects all field names defined
     * in the database and the number it appears and returns that as an result
     * set to the database engine.
     *
     * @return instance of {@link ResultSet} containing that data.
     *
     * @throws SQLException if something went wrong.
     */
    public static ResultSet fieldNames() throws SQLException {
        Connection c = getCurrentConnection();
        PreparedStatement ps = c.prepareStatement(
                "SELECT rdb$field_name, count(rdb$field_name) " +
                "FROM rdb$relation_fields GROUP BY rdb$field_name");

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    /**
     * Selectable procedure that connects to an external Firebird database and
     * performs same operation as in {@link #fieldNames()} call.
     * <p>
     * Please note that this example does not close the connection, since that
     * would close result set too. This also means that in current version
     * connection will be first closed when it is garbage collected.
     *
     * @param dbUrl JDBC URL that should be used to connect to external database.
     *
     * @param user user name that will be used for connection.
     *
     * @param password corresponding password.
     *
     * @return instance of {@link ResultSet} with data.
     *
     * @throws SQLException if SQL error happened.
     * @throws ClassNotFoundException if JDBC driver class was not found.
     */
    public static ResultSet fieldNames(String dbUrl, String user,
            String password) throws SQLException, ClassNotFoundException {

        Class.forName("org.firebirdsql.jdbc.FBDriver");
        Connection c = DriverManager.getConnection(dbUrl, user, password);

        PreparedStatement ps = c.prepareStatement(
                "SELECT rdb$field_name, count(rdb$field_name) " +
                "FROM rdb$relation_fields GROUP BY rdb$field_name");

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    /**
     * Simple example of recursive call of the Java UDF. This method assumes
     * that this function is declared as:
     *
     * <pre>
     *
     *  DECLARE FUNCTION treeCount
     *       int
     *  RETURNS
     *       int
     *  LANGUAGE
     *       java
     *  EXTERNAL NAME
     *       'org.firebirdsql.javaudf.Examples.treeCount';
     *
     * </pre>
     *
     * Also it assumes that following table is declared:
     *
     * <pre>
     *
     *  CREATE TABLE tree(
     *       id INTEGER NOT NULL PRIMARY KEY,
     *       id_p INTEGER,
     *       v VARCHAR(20)
     *  );
     *
     *  INSERT INTO tree VALUES (1, NULL, 'root');
     *  INSERT INTO tree VALUES (2,null,'root graph');
     *  INSERT INTO tree VALUES (3,1,'child 1');
     *  INSERT INTO tree VALUES (4,1,'child 2');
     *  INSERT INTO tree VALUES (5,1,'child 3');
     *  INSERT INTO tree VALUES (6,1,'child 3');
     *  INSERT INTO tree VALUES (7,3,'child 1 1');
     *  INSERT INTO tree VALUES (8,3,'child 1 2');
     *  INSERT INTO tree VALUES (10,2,'child c 1');
     *  INSERT INTO tree VALUES (11,10,'child c 2');
     *  UPDATE tree SET id_p = 11 WHERE id = 2;
     *
     * </pre>
     *
     * @param id ID of the tree node.
     *
     * @return number of children nodes in a subtree.
     *
     * @throws SQLException if SQL error happened.
     */
    public static int treeCount(int id) throws SQLException {
        Connection c = getCurrentConnection();
        try {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT sum(treeCount(id)) FROM tree WHERE id_p = ?");
            try {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                try {
                    rs.next();
                    return rs.getInt(1) + 1;
                } finally {
                    rs.close();
                }
            } finally {
                ps.close();
            }
        } finally {
            c.close();
        }
    }

    /**
     * Simple UDF that works with BLOBs. This method returns length of the
     * BLOB passed as parameter.
     *
     * @param b instance of {@link org.firebirdsql.jdbc.FBBlob}
     *
     * @return long length of the BLOB.
     *
     * @throws SQLException if SQL error happened.
     * @throws IOException if I/O error happened.
     */
    public static long blobLength(org.firebirdsql.jdbc.FirebirdBlob b)
            throws SQLException, IOException {
        if (b == null) return -1;
        try {
            return b.length();
        } finally {
//            b.close(true);
        }
    }

    /**
     * Simple UDF that works with timestamps and computes difference between
     * two timestamps in days.
     *
     * @param start start date
     * @param end end date
     *
     * @return number of days (0 = same)
     */
    public static int daysBetween(Timestamp start, Timestamp end) {
        boolean negative = false;
        if (end.before(start)) {
            negative = true;
            Timestamp temp = start;
            start = end;
            end = temp;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.setTime(end);
        calEnd.set(Calendar.HOUR_OF_DAY, 0);
        calEnd.set(Calendar.MINUTE, 0);
        calEnd.set(Calendar.SECOND, 0);
        calEnd.set(Calendar.MILLISECOND, 0);

        // in same year
        if (cal.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR)) {
            if (negative)
                return (calEnd.get(Calendar.DAY_OF_YEAR) - cal
                        .get(Calendar.DAY_OF_YEAR))
                        * -1;
            return calEnd.get(Calendar.DAY_OF_YEAR)
                    - cal.get(Calendar.DAY_OF_YEAR);
        }

        // not very efficient, but correct
        int counter = 0;
        while (calEnd.after(cal)) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            counter++;
        }
        if (negative) return counter * -1;
        return counter;
    }

    /**
     * Simple UDF that add days to the specified UDF.
     *
     * @param day timestamp to which date should be added.
     *
     * @param offset offset in days.
     *
     * @return new timestamp.
     */
    public static Timestamp addDays (Timestamp day, int offset) {
            if (day == null)
                    day = new Timestamp(System.currentTimeMillis());

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(day);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if (offset != 0)
                    cal.add(Calendar.DAY_OF_YEAR, offset);

            return new Timestamp (cal.getTime().getTime());
    }

    static int getTranNumber(Connection c)
		throws SQLException
	{
		PreparedStatement ps =
			c.prepareStatement("SELECT CURRENT_TRANSACTION FROM RDB$DATABASE");
		try
		{
			ResultSet rs = ps.executeQuery();
			try {
				rs.next();
				return rs.getInt(1);
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

    public static int endTransaction(int how)
		throws SQLException
	{
        Connection c = getCurrentConnection();
		c.setAutoCommit(false);

        try {
			if (how <= 1)
			{
				PreparedStatement ps;
				if (how == 1)
					ps = c.prepareStatement("COMMIT");
				else
					ps = c.prepareStatement("ROLLBACK");

				try {
					ps.execute();
					return getTranNumber(c);
				}
				finally {
					ps.close();
				}
			}
			else if (how == 2)
				c.rollback();
			else
				c.commit();
		}
		finally {
			c.close();
		}

		return getTranNumber(c);
	}

    public static int raiseError(String text)
		throws Exception
	{
		throw new Exception(text);
	}

    public static int getTransaction()
		throws SQLException
	{
		return getTranNumber( getCurrentConnection() );
	}

    public static int autonomousTran()
		throws SQLException, ClassNotFoundException
	{
        Class.forName("org.firebirdsql.jdbc.FBDriver");
		Connection c = DriverManager.getConnection("jdbc:new:connection:");
		try {
			return getTranNumber(c);
		}
		finally {
			c.close();
		}
	}
    public static java.sql.ResultSet CalcRS(int rowCount)
    {
      return new TestRS(rowCount);
    }

  /**
   * executeStmp
   *
   * @param sql String
   * @throws SQLException
   */
  public int executeStmp(String sql) throws SQLException {
      Connection c = DriverManager.getConnection("jdbc:new:connection:");
      try {
        PreparedStatement ps = c.prepareStatement(sql);
        try {
          return ps.executeUpdate();
        }
        finally {
          ps.close();
        }
      }
      finally {
        c.close();
      }
    }
}

