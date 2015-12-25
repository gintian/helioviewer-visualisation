package timelines.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Class containing general utility methods for handling dates
 */
public class TimeUtils {

  /**
   * Calendar instance for date manipulations
   */
  private static Calendar calendar = Calendar.getInstance();

  /**
   * Does this time interval (defined with startReadableInstant and
   * endReadableInstant) contain the readableInstant.
   * <p>
   * Non-zero duration intervals are inclusive of the start instant and
   * inclusive of the end.
   * <p>
   * For example:
   *
   * <pre>
   * [09:00 to 10:00) contains 08:59  = false (before start)
   * [09:00 to 10:00) contains 09:00  = true
   * [09:00 to 10:00) contains 09:59  = true
   * [09:00 to 10:00) contains 10:00  = true
   * [09:00 to 10:00) contains 10:01  = false (after end)
   *
   * [14:00 to 14:00) contains 14:00  = true
   * </pre>
   *
   * @param readableInstant the instant
   * @param startReadableInstant start of interval
   * @param endReadableInstant end of interval
   * @return true if the time interval contains the instant
   */
  public static boolean isFittingInInterval(Date readableInstant, Date startReadableInstant, Date endReadableInstant) {
    return readableInstant.equals(startReadableInstant) || readableInstant.equals(endReadableInstant) || (readableInstant.after(startReadableInstant) && readableInstant.before(endReadableInstant));
  }

  /**
   * Get the later ReadableInstant.
   *
   * @param readableInstantA
   * @param readableInstantB
   * @return max ReadableInstant
   */
  public static Date getLaterDate(Date readableInstantA, Date readableInstantB) {
    if (readableInstantA == null) {
      return readableInstantB;
    }
    if (readableInstantB == null) {
      return readableInstantA;
    }

    if (readableInstantA.compareTo(readableInstantB) >= 0) {
      return readableInstantA;
    } else {
      return readableInstantB;
    }
  }

  /**
   * Sets the given date to midnight
   * @param date the date to set to midnight
   * @return the given Date set to midnight
   */
  public static Date setMidnight(Date date) {

    calendar.setTime(date);

    // reset hour, minutes, seconds and millis
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
  }

  /**
   * Parses a Date from the given String with the given format String
   * @param dateString the String to parse from
   * @param format the format to use for parsing
   * @return the Date parsed from the given String with the given format String
   * @throws ParseException if the parsing goes wrong
   */
  public static Date fromString(String dateString, String format) throws ParseException {

    DateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.parse(dateString);
  }

  /**
   * Creates a String representation of the given Date using the given format String
   * @param date the date to get a String representation to
   * @param formatString the format String
   * @return a String representation of the given Date using the given format String
   */
  public static String toString(Date date, String formatString) {
    DateFormat format = new SimpleDateFormat(formatString);
    return format.format(date);
  }

  /**
   * Returns the year of a given date
   * @param date the date to get the year from
   * @return the year of a given date
   */
  public static int getYear(Date date) {
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR);
  }

  /**
   * Returns the month of a given date
   * @param date the date to get the month from
   * @return the month of a given date
   */
  public static int getMonth(Date date) {
    calendar.setTime(date);
    return calendar.get(Calendar.MONTH) + 1; // For some horrendous reason, the month is 0 based...
  }

  /**
   * Returns the day of month of a given date
   * @param date the date to get the day of month from
   * @return the day of month of a given date
   */
  public static int getDayOfMonth(Date date) {
    calendar.setTime(date);
    return calendar.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Resets the days of month for a given date
   * @param date the date to reset the day of month on
   * @return a date with the day of month reset to it's minimum
   */
  public static Date firstDayOfMonth(Date date) {
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    return calendar.getTime();
  }

  /**
   * Sets the day of month for the given date to its maximum
   * @param date the date on which to set the day of month to its maximum
   * @return the given date with its day of month set to its maximum
   */
  public static Date lastDayOfMonth(Date date) {
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    return calendar.getTime();
  }

  /**
   * Alters a date by the specified value on the specified calendar field
   * @param date the date to manipulate
   * @param calendarField the calendar field to be changed
   * @param amount the amount to change the specified field by
   * @return a date modified by the specified value on the specified calendar field
   */
  public static Date changeDateBy(Date date, int calendarField, int amount){
    calendar.setTime(date);
    calendar.add(calendarField, amount);
    return calendar.getTime();
  }

  /**
   * Calculates the difference in milliseconds between the two given dates
   * @param date1 the first date
   * @param date2 the second date
   * @return the difference in milliseconds between the two given dates
   */
  public static long difference(Date date1, Date date2){
    return date1.getTime() - date2.getTime();
  }

  /**
   * Adds the specified amount of time in milliseconds to the given date
   * @param date the date to add time to
   * @param timeInMilliSec the amount of time to be added
   * @return the given date with the given amount of milliseconds added to it
   */
  public static Date addTime(Date date, long timeInMilliSec){
    return new Date(date.getTime()+timeInMilliSec);
  }

  /**
   * Tests whether two dates are on the same day
   * @param date1 the first date
   * @param date2 the second date
   * @return true if both given days are on the same day, false if not
   */
  public static boolean isSameDay(Date date1, Date date2) {
    Calendar cal2 = Calendar.getInstance();
    calendar.setTime(date1);
    cal2.setTime(date2);
    return calendar.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        && calendar.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }


}