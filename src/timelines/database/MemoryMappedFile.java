package timelines.database;

import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class MemoryMappedFile {

  private static long count = 7L * 1024L * 1024L * 1024L; // 7GB
//  private static int bufferSize = 1024 * 1024 * 500; // 500MB
//  private static int bufferPos = 0;

  public static void main(String[] args) throws Exception {

    Date date = new Date();

    System.out.println(Long.toUnsignedString(count));

    RandomAccessFile memoryMappedFile = new RandomAccessFile("res/db", "rw");

    // Mapping a file into memory
    MappedByteBuffer out1 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, Integer.MAX_VALUE);
    MappedByteBuffer out2 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 2147483647L, Integer.MAX_VALUE);
    MappedByteBuffer out3 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 4294967294L, Integer.MAX_VALUE);
    MappedByteBuffer out4 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 6442450941L, Integer.MAX_VALUE);

    MappedByteBuffer[] buffer = {out1, out2, out3, out4};

    // Writing to Memory Mapped File


      for (long i = 0; i < count; i++) {

        int bufferIndex = (int) (i / Integer.MAX_VALUE);
        try {

    //      System.out.println("i: " + i + " bufferIndex: " + bufferIndex);
          buffer[bufferIndex].put((byte) 'A');

        } catch (BufferOverflowException e) {
          System.out.println(bufferIndex);
          System.out.println(i);
          e.printStackTrace();
        }
      }


    System.out.println("Writing completed");

    // reading from memory file
    for (int i = 1024; i < 1034; i++) {
      int bufferIndex = i / Integer.MAX_VALUE;
      System.out.print((char) buffer[bufferIndex].get(i));
    }

    System.out.println("\nReading completed");
    Date timePassed = new Date(new Date().getTime() - date.getTime());
    System.out.println("time passed: " + timePassed.getTime() + "ms");

  }

}
