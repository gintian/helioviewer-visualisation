package timelines.database;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import timelines.config.Config;

/**
 * Wrapper for the timelines database
 * Use this class to retrieve data from the timelines database
 */
public class TimelinesDB {

  public static final String LOW_CHANNEL_DB_FILE = "dbL";
  public static final String HIGH_CHANNEL_DB_FILE = "dbH";

  private MemoryMappedFile lowChannelDB;
  private MemoryMappedFile highChannelDB;

  public static final Date DB_START_DATE = Config.getStartDate();

  public TimelinesDB() {
    try {
      File fileLow = new File(Config.getDbPath() + LOW_CHANNEL_DB_FILE);
      File fileHigh = new File(Config.getDbPath() + HIGH_CHANNEL_DB_FILE);
      if (!fileLow.exists()) {
        System.out.println(fileLow.getAbsolutePath());
        fileLow.createNewFile();
      }
      if (!fileHigh.exists()) {
        fileHigh.createNewFile();
      }

      lowChannelDB = new MemoryMappedFile(Config.getDbPath() + LOW_CHANNEL_DB_FILE);
      highChannelDB = new MemoryMappedFile(Config.getDbPath() + HIGH_CHANNEL_DB_FILE);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads data from the low channel database from the given start to end date
   * @param start the date starting from which data has to be retrieved
   * @param end the end date to which data has to be retrieved
   * @return a ByteBuffer containing the data
   * @throws IOException
   */
  public ByteBuffer getLowChannelData(Date start, Date end) throws IOException {
    return getData(start, end, lowChannelDB);
  }

  /**
   * Reads data from the high channel database from the given start to end date
   * @param start the date starting from which data has to be retrieved
   * @param end the end date to which data has to be retrieved
   * @return a ByteBuffer containing the data
   * @throws IOException
   */
  public ByteBuffer getHighChannelData(Date start, Date end) throws IOException {
    return getData(start, end, highChannelDB);
  }

  private ByteBuffer getData(Date start, Date end, MemoryMappedFile db) throws IOException {

    long index = getIndexForDate(start);
    if (index == -1) {
      return null;
    }

    return ByteBuffer.wrap(db.read(index, (int) getDBLengthForTimespan(start, end)));
  }

  /**
   * Gets the index in the database for a given date
   * @param date the date for which to get the index
   * @return the index in the database for the given date. -1 if there's no data for the given index
   */
  public long getIndexForDate(Date date) {

    if (date.before(DB_START_DATE) || date.after(new Date())) {
      return -1;
    }

    // count of passed 2sec intervals between start date and date * single DB entry length
    return Math.max(0, (date.getTime() - Config.getStartDate().getTime()) / 1000 / 2 * Float.BYTES - Float.BYTES);
  }

  private long getDBLengthForTimespan(Date from, Date to) {
    return (to.getTime() - from.getTime()) / 1000 / 2 * Float.BYTES;
  }


  /**
   * Closes the database files.
   * Once closed they cannot be reopened
   * @throws IOException
   */
  public void close() throws IOException {
     lowChannelDB.close();
     highChannelDB.close();
  }

}
