package timelines.database.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import timelines.database.MemoryMappedFile;

public class MemoryMappedFileTest {


  @Test
  public void testReadWrite() throws IOException {

    MemoryMappedFile file = new MemoryMappedFile("res/db");

    byte[] data = {1, 2, 3, 4, 5, 6};

    file.write(data, 5);
    byte[] result = file.read(5, data.length);

    assertArrayEquals(data, result);
  }



  /**
   * Test the behavior when writing / reading over the border of a buffer
   * @throws IOException
   */
  @Test
  public void testReadWriteBufferBorder() throws IOException {

    MemoryMappedFile file = new MemoryMappedFile("res/db");

    byte[] data = {1, 2, 3, 4, 5, 6};

    file.write(data, Integer.MAX_VALUE - 3);
    byte[] result = file.read(Integer.MAX_VALUE - 3, data.length);

    assertArrayEquals(data, result);
  }

  @Test
  public void testReadSpeed() throws IOException {
    MemoryMappedFile file = new MemoryMappedFile("res/db");

    Date date = new Date();

    byte[] data =  file.read(0, 16000000 * 3);

    Date timePassed = new Date(new Date().getTime() - date.getTime());
    assertTrue(5000l > timePassed.getTime());
  }

}
