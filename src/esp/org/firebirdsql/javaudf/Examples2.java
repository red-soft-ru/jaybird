package org.firebirdsql.javaudf;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>Title: Examples from compiere </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Eugeney Putilin
 * @version 1.0
 */

public class Examples2 {

  /**
   * 	Calculate the number of days between start and end.
   * 	@param start start date
   * 	@param end end date
   * 	@return number of days (0 = same)
   */
  static public int getDaysBetween (Timestamp start, Timestamp end)
  {
          boolean negative = false;
          if (end.before(start))
          {
                  negative = true;
                  Timestamp temp = start;
                  start = end;
                  end = temp;
          }
          //
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

  //	System.out.println("Start=" + start + ", End=" + end + ", dayStart=" + cal.get(Calendar.DAY_OF_YEAR) + ", dayEnd=" + calEnd.get(Calendar.DAY_OF_YEAR));

          //	in same year
          if (cal.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR))
          {
                  if (negative)
                          return (calEnd.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR)) * -1;
                  return calEnd.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
          }

          //	not very efficient, but correct
          int counter = 0;
          while (calEnd.after(cal))
          {
                  cal.add (Calendar.DAY_OF_YEAR, 1);
                  counter++;
          }
          if (negative)
                  return counter * -1;
          return counter;
  }	//	getDaysBetween

  /**
   * 	Return Day + offset (truncates)
   * 	@param day Day
   * 	@param offset day offset
   * 	@return Day + offset at 00:00
   */
  static public Timestamp addDays (Timestamp day, int offset)
  {
          if (day == null)
                  day = new Timestamp(System.currentTimeMillis());
          //
          GregorianCalendar cal = new GregorianCalendar();
          cal.setTime(day);
          cal.set(Calendar.HOUR_OF_DAY, 0);
          cal.set(Calendar.MINUTE, 0);
          cal.set(Calendar.SECOND, 0);
          cal.set(Calendar.MILLISECOND, 0);
          if (offset != 0)
                  cal.add(Calendar.DAY_OF_YEAR, offset);	//	may have a problem with negative (before 1/1)
          //
          java.util.Date temp = cal.getTime();
          return new Timestamp (temp.getTime());
  }	//	addDays

  /**
   * 	Next Business Day.
   * 	(Only Sa/Su -> Mo)
   *	@param day day
   *	@return next business dat if day is "off"
   */
  static public Timestamp nextBusinessDay (Timestamp day)
  {
          if (day == null)
                  day = new Timestamp(System.currentTimeMillis());
          //
          GregorianCalendar cal = new GregorianCalendar();
          cal.setTime(day);
          cal.set(Calendar.HOUR_OF_DAY, 0);
          cal.set(Calendar.MINUTE, 0);
          cal.set(Calendar.SECOND, 0);
          cal.set(Calendar.MILLISECOND, 0);
          //
          int dow = cal.get(Calendar.DAY_OF_WEEK);
          if (dow == Calendar.SATURDAY)
                  cal.add(Calendar.DAY_OF_YEAR, 2);
          else if (dow == Calendar.SUNDAY)
                  cal.add(Calendar.DAY_OF_YEAR, 1);
          //
          java.util.Date temp = cal.getTime();
          return new Timestamp (temp.getTime());
  }	//	nextBusinessDay


  /**
   * 	Character At Position
   *	@param source source
   *	@param posIndex position 1 = first
   *	@return substring or null
   */
  public static String charAt (String source, int posIndex)
  {
          if (source == null)
                  return null;
          try
          {
                  return (source.substring(posIndex+1, posIndex+2));
          }
          catch (Exception e)
          {}
          return null;
  }	//	charAt

}
