package org.firebirdsql.javaudf;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;
import org.firebirdsql.jdbc.AbstractConnection;
import org.firebirdsql.jdbc.FBBlob;
import org.firebirdsql.jdbc.field.FBField;
import org.firebirdsql.gds.impl.GDSHelper;
import org.firebirdsql.gds.impl.jni.InternalNewGDSImpl;
import org.firebirdsql.encodings.EncodingFactory;
import java.io.*;
import org.firebirdsql.gds.impl.jni.GDSInternalHelper;

import java.sql.*;
import java.math.BigDecimal;
/*
 * <p>Title:Firebird Open Source support Java users function and external procedure</p>
 *
 * Distributable under LGPL license.
 * You may obtain a copy of the License at http://www.gnu.org/copyleft/lgpl.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGPL License for more details.
 *
 *  The Original Code was created by Eugeney Putilin
 *  for the Firebird Open Source RDBMS project.
 *
 *  Copyright (c) 2004 Eugeney Putilin <evgeneyputilin@mail.ru>
 *  and all contributors signed below.
 *
 * All rights reserved.
 *  Contributor(s): ______________________________________.
 *
 * CallJavaMethod is class for wrapper Java methods call
 *
 */

public class CallJavaMethod {
//  static final int N_MESSAGE=1024;
  static final int N_MESSAGE=8128;
  //Коллекция классов методов
    static TreeMap map=new TreeMap(Collator.getInstance());
    static TreeMap mapm=new TreeMap(Collator.getInstance());
   static {
       try {
           Class.forName("org.firebirdsql.jdbc.FBDriver");
       } catch (ClassNotFoundException ex) {}
   }
  /**
   * close
   * Method close ResultSet Statment and Connection automatic called for release
   * resource after
   *
   * @param rs ResultSet
   * @throws SQLException
   * @todo we clear all exception
   */
  public static void close(java.sql.ResultSet rs)throws SQLException
  {
    rs.close();
    Statement st = rs.getStatement();
    if (st != null)
    {
      st.close();
      Connection con = st.getConnection();
      if (con != null)
        con.close();
    }
  }

  /**
   * getBlob
   *
   * @param id long BLOB id
   * @return Object
   */
  public static Object getBlob(long id)
  {
    try {
     return new org.firebirdsql.jdbc.FBBlob(new GDSInternalHelper(), id);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   * @param rs ResultSet
   * @param i int
   * @return Object
   * @throws SQLException
   */
  public static Object getBlobId(java.sql.ResultSet rs, int i) throws Exception {
    try {
        FBBlob blb = (FBBlob) rs.getBlob(i);
        if (blb != null) {
            Long blob_id = new Long(blb.getBlobId());
            return blob_id;
        }
        return null;
    }
    catch (Exception e) {
      throw ExpandStacktrace(e);
    }
  }


   /**
   *
   * @param blb FBBlob
   * @return Object
   * @throws SQLException
   */
  public static Object getBlobId(FBBlob blb) throws Exception {
    try {
        if (blb != null) {
            Long blob_id = new Long(blb.getBlobId());
            return blob_id;
        }
        return null;
    }
    catch (Exception e) {
      throw ExpandStacktrace(e);
    }
  }

  /**
   * Convert primitive type to java object
   * @param i boolean
   * @return Object
   */
  public static Object get(boolean i)
  {return new Boolean(i);}
  /**
   * Convert primitive type to java object
   * @param i byte
   * @return Object
   */
  public static Object get(byte i)
  {return new Byte(i);}
  /**
   * Convert primitive type to java object
   * @param i short
   * @return Object
   */
  public static Object get(short i)
  {return new Short(i);}
  /**
   * Convert primitive type to java object
   * @param i int
   * @return Object
   */
  public static Object get(int i)
  {return new Integer(i);}
  /**
   * Convert primitive type to java object
   * @param i long
   * @return Object
   */
  public static Object get(long i)
  {return new Long(i);}
  /**
   * Convert primitive type to java object
   * @param i long
   * @return Object
   */
  public static Object getDts(long i)
  {return (new datetime(i)).toTimestamp();}
  /**
   * Convert firebird representation in primitive type to timestamp object
   * @param i int
   * @return Object
   */
  public static Object getDt(int i)
  {return (new datetime(i,0)).toDate();}
  /**
   * Convert firebird representation in primitive type to timestamp object
   * @param i int
   * @return Object
   */
  public static Object getTm(int i)
  {return (new datetime(0,i)).toTime();}
  /**
   * Convert primitive type to java object
   * @param i float
   * @return Object
   */
  public static  Object get(float i)
  {return new Float(i);}
  /**
   * Convert primitive type to java object
   * @param i double
   * @return Object
   */
  public static Object get(double i)
  {return new Double(i);}
  /**
   * Convert primitive type to java object
   * @param i long
   * @param s short
   * @return Object
   */
  public static Object get(long i,short s)
  {return java.math.BigDecimal.valueOf(i,-s);}

  /**
   *   На следующие 2 метода добавить поддержку локали
   */
  /**
   * Convert byte array to String
   * @param i byte[]
   * @param dsc_sub_type short
   * @return Object
   */
  public static Object get(byte[] i,short dsc_sub_type)
  {
    if(i==null)
      return null;
    if(dsc_sub_type==0)
      return new String(i);
    try {
      String charSetName = InternalNewGDSImpl.getCharSetNameByID( (int) dsc_sub_type);
      String javaEncoding = EncodingFactory.getJavaEncoding(charSetName);
      if(javaEncoding==null)
        javaEncoding=System.getProperty("file.encoding");
      return new String(i, javaEncoding);
    }
    catch (UnsupportedEncodingException ex) {
      return new String(i);
    }
  }

  /**
   * geta
   *
   * @param i String
   * @param dsc_sub_type short
   * @return byte[]
   */
  public static byte[] geta(String i,short dsc_sub_type)
  {
    if(i==null)
      return null;
    if(dsc_sub_type==0)
      i.getBytes();
    try {
      String charSetName = InternalNewGDSImpl.getCharSetNameByID( (int) dsc_sub_type);
      String javaEncoding = EncodingFactory.getJavaEncoding(charSetName);
      if(javaEncoding==null)
        javaEncoding=System.getProperty("file.encoding");
      return i.getBytes(javaEncoding);
    }
    catch (UnsupportedEncodingException ex) {
      return i.getBytes();
    }
  }

  /**
   * Convert time object to firebird representation in primitive type
   * @param d Time
   * @return int
   */
  public static int getDT(Time d) {
    return (new datetime(d)).toTimeBytes();
  }

  /**
   * Convert timestamp object to firebird representation in primitive type
   * @param d Timestamp
   * @return long
   */
  public static long getDT(Timestamp d) {
    return (new datetime(d)).toTimestampBytes();
  }

  /**
   * Convert date object to firebird representation in primitive type
   * @param d Date
   * @return int
   */
  public static int getDT(Date d) {
    return (new datetime(d)).toDateBytes();
  }

  /**
   * Fetch next row from ResultSet
   * @param rs ResultSet
   * @return boolean
   */
  public static boolean next(java.sql.ResultSet rs)throws Exception {
    try {
      return rs.next();
    }
    catch(Exception e){
      throw ExpandStacktrace(e);
    }
  }

  /**
   *
   * @param rs ResultSet
   * @param i int
   * @return Object
   * @throws SQLException
   */
  public static Object getObject(java.sql.ResultSet rs, int i) throws Exception {
    try {
        return rs.getObject(i);
    }
    catch (Exception e) {
      throw ExpandStacktrace(e);
    }
  }



  public CallJavaMethod() {}

//Метод который вызывает требуемый метод
  /**
   *
   * @param c String
   * @param a Object[]
   * @return Object
   */
  static public Object call(String c, Object a[]) {
    try {
      return call(build(c, a.length), a);
    }
    catch (Exception e) {}
    return null;
  }

  static private Object call(mthCall c, Object a[]) throws Exception {
    try{
    Object r = c.call(a);
    if (r != null) {
      if ( (r instanceof Number) ||
          (r instanceof Date) ||
          (r instanceof Time) ||
          (r instanceof Timestamp) ||
          (r instanceof java.sql.ResultSet) ||
          (r instanceof String) ||
          (r instanceof FBBlob))
        return r;
      return r.toString();
    }
   }catch(Throwable e){
       throw ExpandStacktrace(e);
   }
    return null;
  }

  public static mthCall build(String c, int n) throws ClassNotFoundException {
      Class l;
      Method m;
      if(c!=null){
          c = c.trim();
      }
      if (map.containsKey(c + "$@" + n)) {
        l = (Class) map.get(c + "$@" + n);
        m = (Method) mapm.get(c + "$@" + n);
      }
      else {
        int i = c.lastIndexOf('.');
        String cn = c.substring(i + 1), cl = c.substring(0, i);
        l = Class.forName(cl);
        Method ms[] = l.getMethods();
        m = null;
        while (m == null && ! (l.equals(Object.class))) {
          for (i = 0; i < ms.length && m == null; i++)
            if (ms[i].getName().equals(cn) &&
                (n == -1 || ms[i].getParameterTypes().length == n))
              m = ms[i];
          if (m == null && ! (l.equals(Object.class)))
            l = l.getSuperclass();
        }
        if (m == null && n >= 0)
          return build(c, -1);
        map.put(c + "$@" + n, l);
        mapm.put(c + "$@" + n, m);
      }
      if(m==null){
          return null;
      }
      return new mthCall(l, m);
  }
  private static Exception  ExpandStacktrace(Throwable e)
  {
    while(e instanceof InvocationTargetException)
      e=((InvocationTargetException)e).getTargetException();
    String s=e.getMessage()+'\n';
    for(int i=0;i<e.getStackTrace().length;i++)
      s+=e.getStackTrace()[i].toString()+'\n';
    if(s.length()>N_MESSAGE)
      s=s.substring(1,N_MESSAGE);
      return new Exception(s);
  }
  public static class mthCall {
    Class l;
    Method m;
    mthCall(Class l, Method m) {
      this.l = l;
      this.m = m;
    }

    public Object call(Object a[]) throws IllegalAccessException,
        InvocationTargetException, ParseException{
      Object JavaParams[] = ConvertParam(a);
      return m.invoke(null, JavaParams);
    }

    private Object[] ConvertParam(Object a[]) throws ParseException, RuntimeException {
        Object convertParams[] = new Object[a.length];
        int pos = 0;
        for(Object parTypes : m.getParameterTypes())
        {
            convertParams[pos] = a[pos];
            if ((a[pos] != null) && (a[pos].getClass() != parTypes))
            {
                String str = new String(parTypes.toString());
                // from Integer to Long, Short, BigDecimal, Float, Double
                if ((a[pos] instanceof Integer)  && (str.equals("class java.lang.Long") || str.equals("long"))) {
                    convertParams[pos] = new Long(((Integer)a[pos]).longValue());
                }
                else if ((a[pos] instanceof Integer) && (((Integer)a[pos] >= Short.MIN_VALUE) && ((Integer)a[pos] <= Short.MAX_VALUE))
                        && (str.equals("class java.lang.Short") || str.equals("short"))) {
                    convertParams[pos] = new Short(((Integer)a[pos]).shortValue());
                }
                else if ((a[pos] instanceof Integer)  && (str.equals("class java.math.BigDecimal"))) {
                    convertParams[pos] = new BigDecimal(((Integer)a[pos]).intValue());
                }
                else if ((a[pos] instanceof Integer)  && (str.equals("class java.lang.Float") || str.equals("float"))) {
                    convertParams[pos] = new Float(((Integer)a[pos]).floatValue());
                }
                else if ((a[pos] instanceof Integer)  && (str.equals("class java.lang.Double") || str.equals("double"))) {
                    convertParams[pos] = new Double(((Integer)a[pos]).doubleValue());
                }
                // from Long to Integer, Short, BigDecimal, Double, Float
                else if ((a[pos] instanceof Long)  && (((Long)a[pos] >= Short.MIN_VALUE) && ((Long)a[pos] <= Short.MAX_VALUE)) &&
                        (str.equals("class java.lang.Short") || str.equals("short"))) {
                    convertParams[pos] = new Short(((Long)a[pos]).shortValue());
                }
                else if ((a[pos] instanceof Long)  && (((Long)a[pos] >= Integer.MIN_VALUE) && ((Long)a[pos] <= Integer.MAX_VALUE)) &&
                        (str.equals("class java.lang.Integer") || str.equals("int"))) {
                    convertParams[pos] = new Integer(((Long)a[pos]).intValue());
                }
                else if ((a[pos] instanceof Long)  && (str.equals("class java.math.BigDecimal"))) {
                    convertParams[pos] = new BigDecimal(((Long)a[pos]).intValue());
                }
                else if ((a[pos] instanceof Long)  && (str.equals("class java.lang.Double") || str.equals("double"))) {
                    convertParams[pos] = new Double(((Long)a[pos]).doubleValue());
                }
                else if ((a[pos] instanceof Long)  && (str.equals("class java.lang.Float") || str.equals("float"))) {
                    convertParams[pos] = new Float(((Long)a[pos]).floatValue());
                }
                //from BigDecimal to Double, Float
                else if ((a[pos] instanceof BigDecimal)  && (str.equals("class java.lang.Double") || str.equals("double"))) {
                    convertParams[pos] = new Double(((BigDecimal)a[pos]).doubleValue());
                }
                else if ((a[pos] instanceof BigDecimal)  && (str.equals("class java.lang.Float") || str.equals("float"))) {
                    convertParams[pos] = new Float(((BigDecimal)a[pos]).floatValue());
                }
                //from String to Time, Date, Timestamp
                else if ((a[pos] instanceof String)  && str.equals("class java.sql.Time")) {
                    convertParams[pos] = dateParser.parseTime(((String)a[pos]).trim());
                }
                else if ((a[pos] instanceof String)  && str.equals("class java.sql.Date")) {
                    convertParams[pos] = dateParser.parseDate(((String)a[pos]).trim());
                }
                else if ((a[pos] instanceof String)  && str.equals("class java.sql.Timestamp")) {
                    convertParams[pos] = dateParser.parseTimestamp(((String)a[pos]));
                }
            }
            pos++;
        }
        return convertParams;
    }
}

private static class dateParser {

    static String[] datePatternList = { "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd",
                                    "dd-MM-yyyy", "dd/MM/yyyy", "dd.MM.yyyy",
                                    "dd-MM-yy", "dd/MM/yy", "dd.MM.yy"};
    static String[] timePatternList = {"HH:mm:ss.SSSS", "HH:mm:ss",  "HH:mm"};
    public static java.sql.Date parseDate(String parseString){
        for(String str : datePatternList){
            SimpleDateFormat timeFmt  = new SimpleDateFormat(str);
            java.util.Date date;
            try {
                date = timeFmt.parse((parseString).trim());
            } catch (ParseException e) {
                continue;
            }
            return new java.sql.Date(date.getTime());
        }
        throw new RuntimeException("argument type mismatch");
    }

    public static java.sql.Time parseTime(String parseString){
        for(String str : timePatternList){
            SimpleDateFormat timeFmt  = new SimpleDateFormat(str);
            java.util.Date date;
            try {
                date = timeFmt.parse((parseString).trim());
            } catch (ParseException e) {
                continue;
            }
            return new java.sql.Time(date.getTime());
        }
        throw new RuntimeException("argument type mismatch");
    }

     public static java.sql.Timestamp parseTimestamp(String parseString){
         String datetimestr;
         for (String datestr : datePatternList) {
            for(String timestr : timePatternList){
                datetimestr = datestr + " " + timestr;
                SimpleDateFormat timeFmt  = new SimpleDateFormat(datetimestr);
                java.util.Date date;
                try {
                    date = timeFmt.parse((parseString).trim());
                } catch (ParseException e) {
                    continue;
                }
                return new java.sql.Timestamp(date.getTime());
            }
         }
        throw new RuntimeException("argument type mismatch");
     }
}
//
// Helper Class to encode/decode times/dates
//Class copy from JayBird
private static class datetime{
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int millisecond;

    datetime(Timestamp value){
        Calendar c = new GregorianCalendar();
        c.setTime(value);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH)+1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        millisecond = value.getNanos()/1000000;
    }

    datetime(Date value){
        Calendar c = new GregorianCalendar();
        c.setTime(value);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH)+1;
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = 0;
        minute = 0;
        second = 0;
        millisecond = 0;
    }

    datetime(Time value){
        Calendar c = new GregorianCalendar();
        c.setTime(value);
        year = 0;
        month = 0;
        day = 0;
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);
        millisecond = c.get(Calendar.MILLISECOND);
    }
    datetime(long t){
      this((int)(t&0xffffffff),(int)(t>>32));
    }

    datetime(int date, int time){

            int sql_date = (date);
            int century;
            sql_date -= 1721119 - 2400001;
            century = (4 * sql_date - 1) / 146097;
            sql_date = 4 * sql_date - 1 - 146097 * century;
            day = sql_date / 4;

            sql_date = (4 * day + 3) / 1461;
            day = 4 * day + 3 - 1461 * sql_date;
            day = (day + 4) / 4;

            month = (5 * day - 3) / 153;
            day = 5 * day - 3 - 153 * month;
            day = (day + 5) / 5;

            year = 100 * century + sql_date;

            if (month < 10) {
                month += 3;
            } else {
                month -= 9;
                year += 1;
            }
            int millisInDay = (time)/10;
            hour = millisInDay / 3600000;
            minute = (millisInDay - hour*3600000) / 60000;
            second = (millisInDay - hour*3600000 - minute * 60000) / 1000;
            millisecond = millisInDay - hour*3600000 - minute * 60000 - second * 1000;
    }


    Time toTime(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, 1970);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,second);
        c.set(Calendar.MILLISECOND,millisecond);
        return new Time(c.getTime().getTime());
    }

    Timestamp toTimestamp(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month-1);
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,second);
        c.set(Calendar.MILLISECOND,millisecond);
        return new Timestamp(c.getTime().getTime());
    }

    Date toDate(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month-1);
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return new Date(c.getTime().getTime());
    }
    int toTimeBytes(){
        int millisInDay = (hour * 3600000 + minute * 60000 + second * 1000 + millisecond)*10;
        return millisInDay;
    }
    int toDateBytes(){
        int cpMonth = month;
        int cpYear = year;
        int c, ya;

        if (cpMonth > 2) {
            cpMonth -= 3;
        } else {
            cpMonth += 9;
            cpYear -= 1;
        }

        c = cpYear / 100;
        ya = cpYear - 100 * c;

        int value = ((146097 * c) / 4 +
             (1461 * ya) / 4 +
             (153 * cpMonth + 2) / 5 +
             day + 1721119 - 2400001);
        return (value);
    }
    long toTimestampBytes()
    {
     return ((long)toDateBytes())+(((long)toTimeBytes())<<32);
    }
}
}
