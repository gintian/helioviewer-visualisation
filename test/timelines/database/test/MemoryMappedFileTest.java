package timelines.database.test;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

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

}
