package ch.fhnw.i4ds.timelineviz.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeUtils {

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
  public static Date getMaxReadableInstant(Date readableInstantA, Date readableInstantB) {
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

  public static Date setMidnight(Date date) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    // reset hour, minutes, seconds and millis
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  public static Date fromString(String dateString, String format) throws ParseException {

    DateFormat dateFormat = new SimpleDateFormat(format);
    System.out.println(dateString + " to: " + dateFormat.parse(dateString));
    return dateFormat.parse(dateString);
  }

  public static String toString(Date date, String formatString) {
    DateFormat format = new SimpleDateFormat(formatString);
    return format.format(date);
  }

  public static int getYear(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.YEAR);
  }

  public static int getMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.MONTH) + 1; // For some horrendous reason, the month is 0 based...
  }

  public static int getDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  public static Date firstDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    return cal.getTime();
  }

  public static Date lastDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    return cal.getTime();
  }


}
