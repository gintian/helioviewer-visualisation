package timelines.importer.test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import timelines.database.MemoryMappedFile;
import timelines.importer.downloader.GoesOldFullDownloader;
import timelines.utils.TimeUtils;

public class ImporterTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testInitializeDatabase() throws IOException, ParseException {

    MemoryMappedFile db = new MemoryMappedFile("res/dbL");

    // first available entry in the data
    Date date = TimeUtils.fromString("1974-07-01 00:01:16", "yyyy-MM-dd hh:mm:ss");
    long index = (date.getTime() - GoesOldFullDownloader.START_DATE.getTime()) / 1000L / 2L * 4L - 4L;

//    System.out.println(index);
//    System.out.println(db.readFloat(index));
//
//    for (int i = 0; i < 20; i++) {
//      Date d = new Date(GoesOldFullDownloader.START_DATE.getTime() + ((i + 144 / 4) * 2000));
//      System.out.println(d + " " + db.readFloat(i * 4 + 144));
//    }

    // test data from old service
    float f = db.readFloat(index);
    Assert.assertEquals("expected 1.000e-09, found " + f, 1.000e-09, f, 0.2e-9);


    // one of the last entries in the old data
//    Date date2 = TimeUtils.fromString("1996-08-10 19:25:56", "yyyy-MM-dd hh:mm:ss");
//    long index2 = (date2.getTime() - GoesOldFullDownloader.START_DATE.getTime()) / 1000L / 2L * 4L - 4L;
//    float f2 = db.readFloat(index2);
//
//    System.out.println(index2);
//    System.out.println(f2);
//
//    for (long i = index2; i < index2 + (20L * 4L); i += 4L) {
//      System.out.println(db.readFloat(index2));
//    }
//
//    Assert.assertEquals("expected 5.000e-10, found " + f2, 5.000e-10, f2, 0.2e-9);
  }

}
