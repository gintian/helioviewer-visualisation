package timelines.performanceTest;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import timelines.config.Config;
import timelines.database.MemoryMappedFile;
import timelines.database.TimelinesDB;

public class TimelinesDBTest {

  private static Config config;

  /**
   * Main method for running the timelinesDB performance tests
   * @param args
   */
  public static void main(String[] args) throws IOException, InterruptedException {

    config = new Config();
    File f = new File(config.getDbPath() + TimelinesDB.HIGH_CHANNEL_DB_FILE);

    //
    // to make sure that the database is no longer mapped into the RAM
    // from a previous test, run all the tests individually
    // (comment all except the test that has to be run)
    //

    //
    // write tests
    //
    MemoryMappedFile db = new MemoryMappedFile(f.getPath());

    System.out.println("1'000: " + write(1000, db) + "ms");
    System.out.println("1'000: " + write(1000, db) + "ms");
    reset(db, f);

    db = new MemoryMappedFile(f.getPath());
    System.out.println("100'000: " + write(100000, db) + "ms");
    System.out.println("100'000: " + write(100000, db) + "ms");
    reset(db, f);

    db = new MemoryMappedFile(f.getPath());
    System.out.println("1'000'000: " + write(1000000, db) + "ms");
    System.out.println("1'000'000: " + write(1000000, db) + "ms");
    reset(db, f);

    db = new MemoryMappedFile(f.getPath());
    System.out.println("100'000'000: " + write(100000000, db) + "ms");
    System.out.println("100'000'000: " + write(100000000, db) + "ms");
    reset(db, f);

    db = new MemoryMappedFile(f.getPath());
    System.out.println("500'000'000: " + write(500000000, db) + "ms");
    System.out.println("500'000'000: " + write(500000000, db) + "ms");

    //
    // reading tests
    //
    TimelinesDB timelinesDb = new TimelinesDB();

    System.out.println("\n reading 1'000: " + read(timelinesDb, 1000) + "ms");
    System.out.println("\n reading 1'000: " + read(timelinesDb, 1000));

    System.out.println("\n reading 100'000: " + read(timelinesDb, 100000) + "ms");
    System.out.println("\n reading 100'000: " + read(timelinesDb, 100000) + "ms");

    System.out.println("\n reading 1'000'000: " + read(timelinesDb, 1000000) + "ms");
    System.out.println("\n reading 1'000'000: " + read(timelinesDb, 1000000) + "ms");

    System.out.println("\n reading 100'000'000: " + read(timelinesDb, 100000000) + "ms");
    System.out.println("\n reading 100'000'000: " + read(timelinesDb, 100000000) + "ms");

    System.out.println("\n reading 500'000'000: " + read(timelinesDb, 500000000) + "ms");
    System.out.println("\n reading 500'000'000: " + read(timelinesDb, 500000000) + "ms");

  }

  /**
   * Reads the given amount of entries from the given database
   * @param db the database to read from
   * @param count the amount of records to read
   * @return the time required for reading the given amount of records
   * @throws IOException on error
   */
  private static long read(TimelinesDB db, int count) throws IOException {
    Date start = new Date();
    // 1 entry for every 2 seconds --> 2000ms * record count
    db.getHighChannelData(TimelinesDB.DB_START_DATE, new Date(TimelinesDB.DB_START_DATE.getTime() + 2000L * count));
    return new Date().getTime() - start.getTime();
  }

  /**
   * uUsed to reset the given {@link MemoryMappedFile} and delete the given file
   * @param db the {@link MemoryMappedFile} to reset
   * @param f the file to delete
   * @throws IOException if an IO error occurs when closing the database
   * @throws InterruptedException
   */
  private static void reset(MemoryMappedFile db, File f) throws IOException, InterruptedException {
    Thread.sleep(5000);
    db.close();
    db = null;
    System.gc();
    f.delete();
  }

  /**
   * Writes the given amount of records into the given database
   * @param count the amount of records to write
   * @param db the database to write to
   * @return the amount of milliseconds required for writing
   * @throws IOException on error
   */
  private static long write(int count, MemoryMappedFile db) throws IOException {

    ByteBuffer buffer = ByteBuffer.allocate(count * Float.BYTES);

    for (int i = 0; i < count; i++) {
      buffer.putFloat(1.234f);
    }

    Date start = new Date();

    db.write(buffer.array(), 0);

    return new Date().getTime() - start.getTime();
  }

}
