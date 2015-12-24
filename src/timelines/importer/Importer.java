package timelines.importer;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import timelines.config.Config;
import timelines.database.MemoryMappedFile;
import timelines.database.TimelinesDB;
import timelines.importer.csv.GoesSxrLeaf;
import timelines.importer.downloader.GoesNewAvgDownloader;
import timelines.importer.downloader.GoesNewFull3sDownloader;
import timelines.importer.downloader.GoesNewFullDownloader;
import timelines.importer.downloader.GoesOldFullDownloader;
import timelines.importer.downloader.IDownloader;
import timelines.utils.TimeUtils;

public class Importer {

  private static final Logger logger = Logger.getLogger(Importer.class.getName());

  private MemoryMappedFile lowChannelDB;
  private MemoryMappedFile highChannelDB;
  private GoesNewFullDownloader downloader;
  private Config config;

  public Importer() {

    try {

      config = new Config();

      File fileLow = new File(config.getDbPath() + TimelinesDB.LOW_CHANNEL_DB_FILE);
      File fileHigh = new File(config.getDbPath() + TimelinesDB.HIGH_CHANNEL_DB_FILE);
      if (!fileLow.exists()) {
        fileLow.createNewFile();
      }
      if (!fileHigh.exists()) {
        fileHigh.createNewFile();
      }

      lowChannelDB = new MemoryMappedFile(config.getDbPath() + TimelinesDB.LOW_CHANNEL_DB_FILE);
      highChannelDB = new MemoryMappedFile(config.getDbPath() + TimelinesDB.HIGH_CHANNEL_DB_FILE);

    } catch (IOException e) {
      e.printStackTrace();
    }

    downloader = new GoesNewFullDownloader(0, 20);
  }

  /**
   * Used to update the database
   * @return true if the database changed
   * @throws Exception
   */
  public boolean importNewData() throws Exception {

    logger.log(Level.INFO, "Updating the database", new Object[]{});
    boolean changed = false;

    // get timestamp of last entry available in the database
    Date lastAvailableDate = downloader.getStartDateMidnight();
    Date lastWrittenDate = lastAvailableDate;
    long lastTime = lastWrittenDate.getTime();
    if (lowChannelDB.getFileSize() != 0) {
      lastAvailableDate = new Date(GoesOldFullDownloader.START_DATE.getTime() + lowChannelDB.getFileSize() / Float.BYTES * 2000);
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAvailableDate);
    Date currentDay = cal.getTime();

    ByteBuffer lowChannelBuffer;
    ByteBuffer highChannelBuffer;

    // the noaa service seems to get updated once a day, so we only have to check for data before today
    while (!TimeUtils.isSameDay(currentDay, new Date())) {

      List<GoesSxrLeaf> data = downloader.getGoesSxrLeafs(lastAvailableDate, currentDay);

      if (data == null) {
        continue;
      }

      lowChannelBuffer = ByteBuffer.allocate(data.size() * Float.BYTES);
      highChannelBuffer = ByteBuffer.allocate(data.size() * Float.BYTES);

      for (GoesSxrLeaf leaf: data) {
        lowChannelBuffer.putFloat(leaf.getLowChannel()).array();
        highChannelBuffer.putFloat(leaf.getHighChannel()).array();
        lastTime = leaf.getTimestamp().getTime();
      }

      // write to the end of the mem. mapped file
      appendBufferToDb(lowChannelBuffer, lowChannelDB, lastWrittenDate.getTime());
      appendBufferToDb(highChannelBuffer, highChannelDB, lastWrittenDate.getTime());
      lastWrittenDate = new Date(lastTime);

      lowChannelDB.write(lowChannelBuffer.array(), lowChannelDB.getFileSize());
      highChannelDB.write(highChannelBuffer.array(), highChannelDB.getFileSize());

      changed = true;

      cal.add(Calendar.DAY_OF_YEAR, 1);
      currentDay = cal.getTime();
    }
    if(changed) {
      logger.log(Level.INFO, "Database update complete", new Object[]{});
    } else {
      logger.log(Level.INFO, "Database already up to date", new Object[]{});
    }
    return changed;
  }

  /**
   * Used to initialize the database.
   * Downloads all available data from the older services
   * @throws Exception
   */
  public void initializeDatabase() throws Exception {

    getOldData();

    // new avg
    getData(new GoesNewAvgDownloader(), Calendar.MONTH, Calendar.DAY_OF_MONTH, 1, GoesNewAvgDownloader.START_DATE, GoesNewAvgDownloader.END_DATE);

    // new 3s data
    getData(new GoesNewFull3sDownloader(0, 20), Calendar.DAY_OF_YEAR, Calendar.SECOND, 0, GoesNewFull3sDownloader.START_DATE, GoesNewFull3sDownloader.END_DATE);

    // new data
    getData(downloader, Calendar.DAY_OF_YEAR, Calendar.SECOND, 0, GoesNewFullDownloader.START_DATE, new Date());

  }


//  private void getAverageData() throws Exception {
//    long lastTime = GoesNewAvgDownloader.START_DATE.getTime();
//
//    GoesNewAvgDownloader averageDownloader = new GoesNewAvgDownloader(0, 20);
//
//    Date lastWrittenDate = GoesNewAvgDownloader.START_DATE;
//    System.out.println(lastWrittenDate);
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(lastWrittenDate);
//
////    ArrayList<Float> highChannel = new ArrayList<>();
////    ArrayList<Float> lowChannel = new ArrayList<>();
//
//
//    while (cal.getTime().before(GoesNewAvgDownloader.END_DATE)) {
//
//      List<GoesSxrLeaf> leafs = averageDownloader.getGoesSxrLeafs(cal.getTime(), cal.getTime());
//      System.out.println("got data");
//
//      if (leafs == null) {
//        throw new Exception("Data could not be retrieved");
//      }
//
//      ByteBuffer bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES * 30);
//      ByteBuffer bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES * 30);
//
//
//      for (GoesSxrLeaf leaf : leafs) {
//        long currentTime = leaf.getTimestamp().getTime();
////        System.out.println(leaf.getTimestamp());
////        System.out.println(GoesNewAvgDownloader.START_DATE);
////        System.out.println();
//        int empty = 0;
//        while (currentTime - lastTime > 2000) {
////          System.out.println(currentTime);
////          System.out.println(lastTime);
////          System.out.println(currentTime - lastTime + "\n");
//          // add empty entry
//          bufferLow.putFloat(Float.NaN);
//          bufferHigh.putFloat(Float.NaN);
//          lastTime += 2000;
//          empty ++;
//
//          if (!bufferHigh.hasRemaining()) {
//            appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate.getTime());
//            appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate.getTime());
//            lastWrittenDate = new Date(lastTime);
//            bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
//            bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
//          }
//
//        }
////        System.out.println("adding value " + leaf + " empty values before: " + empty);
//        bufferHigh.putFloat(leaf.getHighChannel());
//        bufferLow.putFloat(leaf.getLowChannel());
//        lastTime = leaf.getTimestamp().getTime();
//
//        if (!bufferHigh.hasRemaining()) {
//          appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate.getTime());
//          appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate.getTime());
//          lastWrittenDate = new Date(lastTime);
//          bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
//          bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
//        }
//        // maybe write here already and empty the lists?
//      }
//      cal.add(Calendar.MONTH, 1);
//      cal.set(Calendar.DAY_OF_MONTH, 1);
//
//      appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate.getTime());
//      appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate.getTime());
//      lastWrittenDate = new Date(lastTime);
//      bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
//      bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
//
////      System.out.println(highChannel);
//    }
//
//
//
//  }

  /**
   * Used to import data from the old full service
   * @throws Exception
   */
  private void getOldData() throws Exception {
    long lastTime = GoesOldFullDownloader.START_DATE.getTime();

    GoesOldFullDownloader downloader = new GoesOldFullDownloader(1, 92);

    Date lastAddedDate = GoesOldFullDownloader.START_DATE;
    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAddedDate);

    long lastWrittenDate = GoesOldFullDownloader.START_DATE.getTime();

    while (cal.getTime().before(GoesOldFullDownloader.END_DATE)) {

      List<GoesSxrLeaf> leafs = downloader.getGoesSxrLeafs(lastAddedDate, cal.getTime());
      cal.add(Calendar.MONTH, 1);
      cal.set(Calendar.DAY_OF_MONTH, 1);

      System.out.println("got data.");

      if (leafs == null) {
        continue;
      }
      if (leafs.size() == 0) {
        continue;
      }

      // we start with the expectation of perfectly valid files.
      // Should we have to insert placeholder values, the buffers are written to the db and newly initialized as soon as they are full
      ByteBuffer bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      ByteBuffer bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);

      int c = 0;
      long prevTime = 0;
      for (GoesSxrLeaf leaf : leafs) {
        long currentTime = leaf.getTimestamp().getTime();

        if(currentTime < prevTime) {
          continue;
        }
        prevTime = currentTime;

        try {
          while (currentTime - lastTime > 2000) {

            // add empty entry
            bufferLow.putFloat(Float.NaN);
            bufferHigh.putFloat(Float.NaN);
            lastTime += 2000;
            c ++;

            if (!bufferHigh.hasRemaining()) {
              appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
              appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
              lastWrittenDate = lastTime;
              bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
              bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
            }
          }

          bufferLow.putFloat(leaf.getLowChannel());
          bufferHigh.putFloat(leaf.getHighChannel());
          lastTime += 2000;
          lastAddedDate = new Date(lastTime);
          c ++;

          // TODO refactor this so we don't have the block twice
          if (!bufferHigh.hasRemaining()) {
            appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
            appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
            lastWrittenDate = lastTime;
            bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
            bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
          }

        } catch (BufferOverflowException e) {
          System.out.println("buffer overflow: " + c + " total count: " + leafs.size());
        }
      }


      //
      // write
      //
      appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
      appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
      lastWrittenDate = lastTime;
      bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
    }
  }

  /**
   * Used to import data from the new average and new full services
   * @param downloader the downloader to use
   * @param calendarFieldToIncrement the calendar field to increment after every downloaded file.<br>
   *  Specifies the time frame covered by a single CSV file in the used service
   * @param calendarFieldToReset the calendar field to reset after downloading a CSV file.<br>
   *  This specifies the start of the time frame a imported record has to match in order to be stored
   * @param resetValue the value the reset calendar field has to be set to
   * @param startDate the date starting at which data has to be imported
   * @param endDate the date up to which data has to be imported
   * @throws Exception on error
   */
  private void getData(IDownloader downloader, int calendarFieldToIncrement, int calendarFieldToReset, int resetValue, Date startDate, Date endDate) throws Exception {

    long lastTime = startDate.getTime();
    Date lastAddedDate = startDate;
    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAddedDate);

    long lastWrittenDate = startDate.getTime();

    while (cal.getTime().before(endDate)) {

      cal.add(calendarFieldToIncrement, 1);
      cal.set(calendarFieldToReset, resetValue);

      List<GoesSxrLeaf> leafs = downloader.getGoesSxrLeafs(lastAddedDate, cal.getTime());


      if (leafs == null) {
        // no data (no matching file found)
        continue;
      }
      if (leafs.size() == 0) {
        // no data (found a file but no valid data is available)
        // We try to find another file that hopefully contains valid data by resetting the goesNr
        downloader.resetGoesNr();
        leafs = downloader.getGoesSxrLeafs(lastAddedDate, cal.getTime());

        // if we find another / the same file again that has no valid data stored, we skip to the next date
        if (leafs.size() == 0) {
          continue;
        }
      }

      // we start with the expectation of perfectly valid files.
      // Should we have to insert placeholder values, the buffers are written to the db and newly initialized as soon as they are full
      ByteBuffer bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      ByteBuffer bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);


      int c = 0;
      long prevTime = 0;
      for (GoesSxrLeaf leaf : leafs) {

        long currentTime = leaf.getTimestamp().getTime();

        if(currentTime < prevTime) {
          // the current entry is not in its proper chronological order
          // skip it
          continue;
        }
        prevTime = currentTime;

        try {
          while (currentTime - lastTime > 2000) {

            // add empty entry
            bufferLow.putFloat(Float.NaN);
            bufferHigh.putFloat(Float.NaN);
            lastTime += 2000;
            c ++;

            if (!bufferHigh.hasRemaining()) {
              appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
              appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
              lastWrittenDate = lastTime;
              bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
              bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
            }
          }

          bufferLow.putFloat(leaf.getLowChannel());
          bufferHigh.putFloat(leaf.getHighChannel());
          lastTime += 2000;
          lastAddedDate = new Date(lastTime);
          c ++;

          // TODO refactor this so we don't have the block twice
          if (!bufferHigh.hasRemaining()) {
            appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
            appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
            lastWrittenDate = lastTime;
            bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
            bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
          }

        } catch (BufferOverflowException e) {
          System.out.println("buffer overflow: " + c + " total count: " + leafs.size());
        }
      }

      //
      // write
      //
      appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
      appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
      lastWrittenDate = lastTime;
      bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
    }
  }

  private void appendBufferToDb(ByteBuffer buffer, MemoryMappedFile db, long date) throws IOException {
    long index = (date - GoesOldFullDownloader.START_DATE.getTime()) / 1000 / 2 * Float.BYTES;
    db.write(buffer.array(), index);
  }


  /**
   * Use this main method to initialize the database
   * @param args
   */
  public static void main(String[] args){

    Importer importer = new Importer();

    try {
      importer.initializeDatabase();
     } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
