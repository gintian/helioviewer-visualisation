package timelines.importer.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import timelines.database.TimelinesDB;
import timelines.utils.TimeUtils;

public class ImporterTest {

  TimelinesDB db;

  @Before
  public void setUp() throws Exception {

    db = new TimelinesDB();
  }

  @Test
  public void testFirstEntry() throws IOException, ParseException {

    // first available entry in the data
    Date date = TimeUtils.fromString("1974-07-01 00:01:16", "yyyy-MM-dd hh:mm:ss");
    Date date2 = new Date(date.getTime() + 2000);
    ByteBuffer buffer = db.getLowChannelData(date, date2);

    float f = buffer.getFloat();
    Assert.assertEquals("expected 1.000e-09, found " + f, 1.000e-09, f, 0.2e-9);

  }


  @Test
  public void testLastOldEntry() throws IOException, ParseException {

    Date date = TimeUtils.fromString("1996-07-31 23:59:54", "yyyy-MM-dd hh:mm:ss");
    Date date2 = new Date(date.getTime() + 2000);
    ByteBuffer buffer = db.getLowChannelData(date, date2);

    float f = buffer.getFloat();
    Assert.assertEquals("expected 5.000e-10, found " + f, 5.000e-10, f, 0.2e-9);

    buffer = db.getHighChannelData(date, date2);
    f = buffer.getFloat();
    Assert.assertEquals("expected 6.871e-08, found " + f, 6.871e-08, f, 0.2e-9);

  }

  @Test
  public void testAvgEntry() throws IOException, ParseException {

    Date date = TimeUtils.fromString("1996-09-01 00:00:00.000", "yyyy-MM-dd hh:mm:ss");
    Date date2 = new Date(date.getTime() + 2000 * 30);
    ByteBuffer buffer = db.getLowChannelData(date, date2);

    float f = buffer.getFloat();
    while(buffer.hasRemaining()) {
      System.out.println(buffer.getFloat());
    }

    Assert.assertEquals("expected 1.0000E-09, found " + f, 1.0000E-09, f, 0.2e-9);

    buffer = db.getHighChannelData(date, date2);
    f = buffer.getFloat();
    Assert.assertEquals("expected 7.5300E-08, found " + f, 7.5300E-08, f, 0.2e-9);

  }

  @Test
  public void testNewEntry() throws IOException, ParseException {

    Date date = TimeUtils.fromString("2015-11-24 00:00:02", "yyyy-MM-dd hh:mm:ss");
    Date date2 = new Date(date.getTime() + 2000 * 30);
    ByteBuffer buffer = db.getLowChannelData(date, date2);

    float f = buffer.getFloat();
    while(buffer.hasRemaining()) {
      System.out.println(buffer.getFloat());
    }

    Assert.assertEquals("expected 1.0000E-09, found " + f, 1.0000E-09, f, 0.2e-9);

    buffer = db.getHighChannelData(date, date2);
    f = buffer.getFloat();
    while(buffer.hasRemaining()) {
      System.out.println(buffer.getFloat());
    }
    Assert.assertEquals("expected 4.6415E-07, found " + f, 4.6415E-07, f, 0.2e-9);

  }


}
