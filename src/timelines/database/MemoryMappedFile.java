package timelines.database;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import timelines.importer.csv.GoesSxrLeaf;

/**
 *
 * @author Matthias Hug
 * Instances of this class allow reading and writing from/to a file in a memory mapped fashion
 *
 */
public class MemoryMappedFile {

  private RandomAccessFile memoryMappedFile;

  private ArrayList<MappedByteBuffer> buffers = new ArrayList<>();

  /**
   *
   * @param file
   * @throws IOException
   */
  public MemoryMappedFile (String file) throws IOException {

    memoryMappedFile = new RandomAccessFile(file, "rw");

    // create buffers over the entire files size
    long length = memoryMappedFile.length();

    while (length >= Integer.MAX_VALUE) {
//      System.out.println("buffers size: " + buffers.size());
//      System.out.println((long) buffers.size() * (long) Integer.MAX_VALUE);
      MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, (long) buffers.size() * (long) Integer.MAX_VALUE, Integer.MAX_VALUE);
      buffers.add(out);
      length -= Integer.MAX_VALUE;
    }
    if (length > 0) {
      MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, buffers.size() * Integer.MAX_VALUE, length);
      buffers.add(out);
    }
  }


  // TODO figure out, what sort of read methods we really need and make sure they have high performance
  /**
   * Read length bytes from the file starting at index
   * @param index from where to read
   * @param length how many bytes to read
   * @return the bytes read from index to index + length
   * @throws IOException
   */
  public byte[] read(long index, int length) throws IOException {

    MappedByteBuffer buffer = getBufferForIndex(index);
    byte[] result = new byte[length];

    buffer.position((int) (index % Integer.MAX_VALUE));
    buffer.get(result);

    return result;
  }

  public GoesSxrLeaf readLeaf(long index) throws IOException {
    // TODO
    return null;
  }

  public float readFloat(long index) throws IOException {
    MappedByteBuffer buffer = getBufferForIndex(index);
    return buffer.getFloat((int) (index % Integer.MAX_VALUE));
  }

  public int readInt(long index) throws IOException {
    MappedByteBuffer buffer = getBufferForIndex(index);
    return buffer.getInt((int) (index % Integer.MAX_VALUE));
  }



  /**
   * Writes the given bytes to the end of the file
   * @param value the bytes to write
   */
  public void write(byte[] value) {
    // TODO
    // make sure, that we "hop" to the next buffer
    // once the end of the current one is reached.
    // Should we reach the end of the last buffer,
    // then this buffers size has to be increased.
    // If its size reaches INTEGER.MAX_VALUE,
    // create and add a new buffer
  }

  /**
   * Needed?
   * Writes the given value to the file, starting at index
   * @param value the value to write
   * @param index the index to write from
   */
  public void write(byte[] value, long index) {
    // TODO
  }

  public void writeFloat(Float f, long index) throws IOException {
    MappedByteBuffer buffer = getBufferForIndex(index);
    buffer.putFloat((int) (index % Integer.MAX_VALUE), f);
  }

  /**
   * Get the buffer that's needed to access the files given byte index
   * @param index the index from where the file has to be accessed.
   * @return the buffer needed to read from the file starting at the given index.<br>
   * Null if the index is outside of the files size
   * @throws IOException
   */
  private MappedByteBuffer getBufferForIndex(long index) throws IOException {
    int bufferIndex = (int) (index / Integer.MAX_VALUE);
    if (bufferIndex > buffers.size() - 1 || index > memoryMappedFile.length()) {
      return null;
    }
    return buffers.get(bufferIndex);
  }


  /**
   * Used for testing purposes only
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {


    MemoryMappedFile meMoMaFi = new MemoryMappedFile("res/db");

    meMoMaFi.writeFloat(123f, 123);

    System.out.println(meMoMaFi.readFloat(123));



//    long count = 7L * 1024L * 1024L * 1024L; // 7GB
//
//    Date date = new Date();
//
//    System.out.println(Long.toUnsignedString(count));
//
//    RandomAccessFile memoryMappedFile = new RandomAccessFile("res/db", "rw");
//
//    // Mapping a file into memory
//    MappedByteBuffer out1 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, Integer.MAX_VALUE);
//    MappedByteBuffer out2 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 2147483647L, Integer.MAX_VALUE);
//    MappedByteBuffer out3 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 4294967294L, Integer.MAX_VALUE);
//    MappedByteBuffer out4 = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 6442450941L, Integer.MAX_VALUE);
//
//    MappedByteBuffer[] buffer = {out1, out2, out3, out4};
//
//    // Writing to Memory Mapped File
//
//
//      for (long i = 0; i < count; i++) {
//
//        int bufferIndex = (int) (i / Integer.MAX_VALUE);
//        try {
//
//    //      System.out.println("i: " + i + " bufferIndex: " + bufferIndex);
//          buffer[bufferIndex].put((byte) 'A');
//
//        } catch (BufferOverflowException e) {
//          System.out.println(bufferIndex);
//          System.out.println(i);
//          e.printStackTrace();
//        }
//      }
//
//
//    System.out.println("Writing completed");
//
//    // reading from memory file
//    for (int i = 1024; i < 1034; i++) {
//      int bufferIndex = i / Integer.MAX_VALUE;
//      System.out.print((char) buffer[bufferIndex].get(i));
//    }
//
//    System.out.println("\nReading completed");
//    Date timePassed = new Date(new Date().getTime() - date.getTime());
//    System.out.println("time passed: " + timePassed.getTime() + "ms");

  }



}
