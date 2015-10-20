package timelines.importer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import timelines.database.MemoryMappedFile;
import timelines.importer.csv.GoesSxrLeaf;
import timelines.importer.downloader.GoesNewAvgDownloader;
import timelines.importer.downloader.GoesNewFullDownloader;
import timelines.utils.TimeUtils;

public class Importer {

  private MemoryMappedFile memMappeFile;
  private GoesNewFullDownloader downloader;


  public Importer() {

    try {

      File file = new File("res/db");
      if (!file.exists()) {
        file.createNewFile();
      }
      memMappeFile = new MemoryMappedFile("res/db");

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
    if (memMappeFile.getFileSize() != 0) {
      long lastEntry = memMappeFile.readLong(memMappeFile.getFileSize() - (2 * 4 + 8));
      System.out.println(new Date(lastEntry));
      // TODO set lastAvailableDate to the date read
    }


    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAvailableDate);
    Date currentDay = cal.getTime();

    ByteBuffer buffer;

    while (!currentDay.equals(TimeUtils.setMidnight(new Date()))) {

      //getDownloaderForDate(currentDay);

      // TODO write into 2 seperate files
      Set<GoesSxrLeaf> data = downloader.getGoesSxrLeafs(lastAvailableDate, currentDay);
      buffer = ByteBuffer.allocate(data.size() * Float.BYTES * 2);
      byte[] byteData; // = new byte[data.size() * 3 * 4];

      // TODO: try to get a byte array from the downloader, so we can skip this transformation crap
      for (GoesSxrLeaf leaf: data) {
        //buffer.putLong(leaf.getTimestamp().getTime());
        buffer.putFloat(leaf.getLowChannel()).array();
        buffer.putFloat(leaf.getHighChannel()).array();
      }

      // write to the end of the mem mapped file

      System.out.println("writing data");
      memMappeFile.write(buffer.array(), memMappeFile.getFileSize());

      cal.add(Calendar.DAY_OF_YEAR, 1);
      currentDay = cal.getTime();

    }
  }

  /**
   * Used to initialize the database.
   * Downloads all available data from the older services
   * @throws ParseException
   */
  public void initializeDatabase() throws ParseException {

    // TODO get data from old services

    long lastTime = GoesNewAvgDownloader.START_DATE.getTime() / 1000; // TODO get last date in DB

    GoesNewAvgDownloader averageDownloader = new GoesNewAvgDownloader(0, 20);

    // TODO: we probably have to call for every day separately
    Set<GoesSxrLeaf> leafs = averageDownloader.getGoesSxrLeafs(GoesNewAvgDownloader.START_DATE, GoesNewAvgDownloader.END_DATE);

    ArrayList<Float> highChannel = new ArrayList<>();
    ArrayList<Float> lowChannel = new ArrayList<>();
    for (GoesSxrLeaf leaf : leafs) {
      long currentTime = leaf.getTimestamp().getTime() / 1000;
      while (currentTime - lastTime > 2) {
        // add empty entry
        highChannel.add(Float.NaN);
        lowChannel.add(Float.NaN);
        currentTime += 2;
      }
      highChannel.add(leaf.getHighChannel());
      lowChannel.add(leaf.getLowChannel());
    }

    // TODO write to DB

  }

  // probably not used because we seperate the importing into an initial and a "normal" mode
  private void getDownloaderForDate(Date currentDay) {
    if (currentDay.before(GoesNewFullDownloader.START_DATE)) {
      if (currentDay.before(GoesNewAvgDownloader.START_DATE)) {
        // old full downloader
      } else {
        // new average downloader
      }
    } else {
      // new full downloader
    }
  }

  public static void main(String[] args) throws ParseException {


//    Date date = TimeUtils.setMidnight(TimeUtils.fromString("2009-11-30", "yyyy-MM-dd"));
//    System.out.println("avg end date" + date.getTime());

    Importer importer = new Importer();

    try {
      importer.initializeDatabase();
      importer.importNewData();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
