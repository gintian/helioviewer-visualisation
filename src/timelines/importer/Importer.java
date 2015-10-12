package timelines.importer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import timelines.database.MemoryMappedFile;
import timelines.importer.csv.GoesSxrLeaf;
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

  public void importNewData() throws IOException {

    // get timestamp of last entry available in the database
    Date lastAvailableDate = downloader.getStartDateMidnight();
    if (memMappeFile.getFileSize() != 0) {
      float lastEntry = memMappeFile.readLong(memMappeFile.getFileSize() - (2 * 4 + 8));

      // TODO set lastAvailableDate to the date read
    }


    Calendar cal = Calendar.getInstance();
    cal.setTime(lastAvailableDate);
    Date currentDay = cal.getTime();

    while (!lastAvailableDate.equals(TimeUtils.setMidnight(new Date()))) {

      Set<GoesSxrLeaf> data = downloader.getGoesSxrLeafs(lastAvailableDate, currentDay);
      byte[] byteDate = new byte[data.size() * 3 * 4];

      // TODO: try to get a byte array from the downloader, so we can skip this transformation crap
      for (GoesSxrLeaf leaf: data) {

        ByteBuffer.allocate(8).putLong(leaf.getTimestamp().getTime());
        ByteBuffer.allocate(4).putFloat(leaf.getLowChannel()).array();
        ByteBuffer.allocate(4).putFloat(leaf.getHighChannel()).array();
      }

      // write to the end of the mem mapped file
      memMappeFile.write(byteDate, memMappeFile.getFileSize());

      cal.add(Calendar.DAY_OF_YEAR, 1);
      currentDay = cal.getTime();
      // todo: set lastAvailableDate to the last one downloaded
    }


    // download data that's not yet in the database
  }

  public static void main(String[] args) {

    Importer importer = new Importer();

    try {
      importer.importNewData();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
