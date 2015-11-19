package timelines.importer;

import java.io.File;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timelines.database.MemoryMappedFile;
import timelines.importer.csv.GoesSxrLeaf;
import timelines.importer.downloader.GoesNewAvgDownloader;
import timelines.importer.downloader.GoesNewFullDownloader;
import timelines.importer.downloader.GoesOldFullDownloader;
import timelines.utils.TimeUtils;

public class Importer {

  private MemoryMappedFile lowChannelDB;
  private MemoryMappedFile highChannelDB;
  private GoesNewFullDownloader downloader;


  public Importer() {

    try {

      File fileLow = new File("res/dbL");
      File fileHigh = new File("res/dbH");
      if (!fileLow.exists()) {
        fileLow.createNewFile();
      }
      if (!fileHigh.exists()) {
        fileHigh.createNewFile();
      }

      lowChannelDB = new MemoryMappedFile("res/dbL");
      highChannelDB = new MemoryMappedFile("res/dbH");

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    downloader = new GoesNewFullDownloader(0, 20);
  }

  /**
   * Used to update the database
   * @throws IOException
   */
  public void importNewData() throws IOException {

    // get timestamp of last entry available in the database
    Date lastAvailableDate = downloader.getStartDateMidnight();
    if (lowChannelDB.getFileSize() != 0) {
      lastAvailableDate = new Date(GoesOldFullDownloader.START_DATE.getTime() + lowChannelDB.getFileSize() * 2000);
    }

    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAvailableDate);
    Date currentDay = cal.getTime();

    ByteBuffer lowChannelBuffer;
    ByteBuffer highChannelBuffer;

    while (!currentDay.equals(TimeUtils.setMidnight(new Date()))) {

      List<GoesSxrLeaf> data = downloader.getGoesSxrLeafs(lastAvailableDate, currentDay);
      lowChannelBuffer = ByteBuffer.allocate(data.size() * Float.BYTES);
      highChannelBuffer = ByteBuffer.allocate(data.size() * Float.BYTES);

      // TODO: try to get a byte array from the downloader, so we can skip this transformation crap
      for (GoesSxrLeaf leaf: data) {
        lowChannelBuffer.putFloat(leaf.getLowChannel()).array();
        highChannelBuffer.putFloat(leaf.getHighChannel()).array();
      }

      // write to the end of the mem mapped file
      System.out.println("writing data");
      lowChannelDB.write(lowChannelBuffer.array(), lowChannelDB.getFileSize());
      highChannelDB.write(highChannelBuffer.array(), highChannelDB.getFileSize());

      cal.add(Calendar.DAY_OF_YEAR, 1);
      currentDay = cal.getTime();

    }
  }

  /**
   * Used to initialize the database.
   * Downloads all available data from the older services
   * @throws Exception
   */
  public void initializeDatabase() throws Exception {

    getOldData();
//    getAverageData();

  }


  private void getAverageData() throws Exception {
    long lastTime = GoesNewAvgDownloader.START_DATE.getTime(); // TODO get last date in DB

    GoesNewAvgDownloader averageDownloader = new GoesNewAvgDownloader(0, 20);

    Date lastWrittenDate = GoesNewAvgDownloader.START_DATE;
    System.out.println(lastWrittenDate);
    Calendar cal = Calendar.getInstance();
    cal.setTime(lastWrittenDate);

//    ArrayList<Float> highChannel = new ArrayList<>();
//    ArrayList<Float> lowChannel = new ArrayList<>();


    while (cal.getTime().before(GoesNewAvgDownloader.END_DATE)) {

      List<GoesSxrLeaf> leafs = averageDownloader.getGoesSxrLeafs(cal.getTime(), cal.getTime());
      System.out.println("got data");

      if (leafs == null) {
        throw new Exception("Data could not be retrieved");
      }

      ByteBuffer bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES * 30);
      ByteBuffer bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES * 30);


      for (GoesSxrLeaf leaf : leafs) {
        long currentTime = leaf.getTimestamp().getTime();
//        System.out.println(leaf.getTimestamp());
//        System.out.println(GoesNewAvgDownloader.START_DATE);
//        System.out.println();
        int empty = 0;
        while (currentTime - lastTime > 2000) {
//          System.out.println(currentTime);
//          System.out.println(lastTime);
//          System.out.println(currentTime - lastTime + "\n");
          // add empty entry
          bufferLow.putFloat(Float.NaN);
          bufferHigh.putFloat(Float.NaN);
          lastTime += 2000;
          empty ++;

          if (!bufferHigh.hasRemaining()) {
            appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate.getTime());
            appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate.getTime());
            lastWrittenDate = new Date(lastTime);
            bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
            bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
          }

        }
//        System.out.println("adding value " + leaf + " empty values before: " + empty);
        bufferHigh.putFloat(leaf.getHighChannel());
        bufferLow.putFloat(leaf.getLowChannel());
        lastTime = leaf.getTimestamp().getTime();

        if (!bufferHigh.hasRemaining()) {
          appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate.getTime());
          appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate.getTime());
          lastWrittenDate = new Date(lastTime);
          bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
          bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
        }
        // maybe write here already and empty the lists?
      }
      cal.add(Calendar.MONTH, 1);
      cal.set(Calendar.DAY_OF_MONTH, 1);

      appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate.getTime());
      appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate.getTime());
      lastWrittenDate = new Date(lastTime);
      bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);

//      System.out.println(highChannel);
    }

    // TODO write to DB

  }

  private void getOldData() throws Exception {
    long lastTime = GoesOldFullDownloader.START_DATE.getTime();

    GoesOldFullDownloader downloader = new GoesOldFullDownloader(1, 92);

    Date lastAddedDate = GoesOldFullDownloader.START_DATE; // TODO if we want this to be capable of writing with some data available already, we need the start date from the DB
    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAddedDate);

    long lastWrittenDate = GoesOldFullDownloader.START_DATE.getTime();

    while (cal.getTime().before(GoesOldFullDownloader.END_DATE)) {

      List<GoesSxrLeaf> leafs = downloader.getGoesSxrLeafs(lastAddedDate, cal.getTime());
      cal.add(Calendar.MONTH, 1);
      cal.set(Calendar.DAY_OF_MONTH, 1);

      System.out.println("got data.");

      if (leafs == null) {
//        throw new Exception("Data could not be retrieved");
        System.out.println("no data for month?!");
        continue;
      }
      if (leafs.size() == 0) {
        System.out.println("no data?!");
        continue;
      }

//      long start = lowChannelDB.getFileSize() == 0 ? GoesOldFullDownloader.START_DATE.getTime() : leafs.get(leafs.size() - 1).getTimestamp().getTime();
//      long timespan = leafs.get(leafs.size() - 1).getTimestamp().getTime() - start;
//      int bufferSize = (int) (timespan / 1000 / 2 * Float.BYTES);
//      System.out.println("timespan: " + timespan + " buffer size: " + bufferSize);

      // we start with the expectation of perfectly valid files.
      // Should we have to insert placeholder values, the buffers are written to the db and newly initialized as soon as they are full
      ByteBuffer bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      ByteBuffer bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);


      int c = 0;
      long prevTime = 0;
      for (GoesSxrLeaf leaf : leafs) {
        long currentTime = leaf.getTimestamp().getTime();

        if(currentTime < prevTime) {
//          System.out.println("weird stuff with times not being ordered properly");
          continue;
//          throw new Exception("weird stuff " + leaf.getTimestamp() + new Date(prevTime));
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
      System.out.println("writing data");
      appendBufferToDb(bufferLow, lowChannelDB, lastWrittenDate);
      appendBufferToDb(bufferHigh, highChannelDB, lastWrittenDate);
      lastWrittenDate = lastTime;
      bufferLow = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      bufferHigh = ByteBuffer.allocate(leafs.size() * Float.BYTES);
      //
      // reset the lists
      //
//      highChannel = new ArrayList<Float>();
//      lowChannel = new ArrayList<Float>();

    }
  }

  private void appendBufferToDb(ByteBuffer buffer, MemoryMappedFile db, long date) throws IOException {
    long index = (date - GoesOldFullDownloader.START_DATE.getTime()) / 1000 / 2 * Float.BYTES;
    db.write(buffer.array(), index);
  }


  public static void main(String[] args) throws ParseException {


//    Date date = TimeUtils.setMidnight(TimeUtils.fromString("2009-11-30", "yyyy-MM-dd"));
//    System.out.println("avg end date" + date.getTime());
    System.out.println(GoesNewAvgDownloader.START_DATE);
//    Date date = TimeUtils.setMidnight(TimeUtils.fromString("2009-11-30", "yyyy-MM-dd"));
    Importer importer = new Importer();

    try {
      importer.initializeDatabase();
//      importer.importNewData();
     } catch (Exception e) {
//      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
