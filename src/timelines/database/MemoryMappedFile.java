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
  // So far it looks like it's far more efficient to read an array instead of reading value by value

  /**
   * Read length bytes from the file starting at index
   * @param index from where to read
   * @param length how many bytes to read
   * @return the bytes read from index to index + length
   * @throws IOException
   */
  public byte[] read(long index, int length) throws IOException {
    byte[] result = new byte[length];
    read(index, result, 0, length);
    return result;
  }

  private void read(long index, byte[] result, int offset, int length) throws IOException {

    MappedByteBuffer buffer = getBufferForIndex(index);

    // return once we reached an index outside of the files length
    if (buffer == null) {
      return;
    }

    int originalPosition = buffer.position();
    buffer.position((int) (index % Integer.MAX_VALUE));

    // check whether we need to access multiple buffers
    if (length > buffer.remaining()) {
      read(index + buffer.remaining(), result, buffer.remaining(), result.length - buffer.remaining());
      buffer.get(result, 0, buffer.remaining());

    } else {
      buffer.get(result, offset, length);
    }

    buffer.position(originalPosition);
  }

  //
  // potentially rather inefficient reading methods, not suited for a large amount of calls
  //

  public GoesSxrLeaf readLeaf(long index) throws IOException {
    // TODO
    return null;
  }

  public float readFloat(long index) throws IOException {
    MappedByteBuffer buffer = getBufferForIndex(index);
    return buffer.getFloat((int) (index % Integer.MAX_VALUE));
  }

  public long readLong(long index) throws IOException {
    MappedByteBuffer buffer = getBufferForIndex(index);
    return buffer.getLong((int) (index % Integer.MAX_VALUE));
  }

  public int readInt(long index) throws IOException {
    MappedByteBuffer buffer = getBufferForIndex(index);
    return buffer.getInt((int) (index % Integer.MAX_VALUE));
  }


  // TODO
  // make sure, that we "hop" to the next buffer
  // once the end of the current one is reached.
  // Should we reach the end of the last buffer,
  // then this buffers size has to be increased.
  // If its size reaches INTEGER.MAX_VALUE,
  // create and add a new buffer

  /**
   * Writes the given byte array to the file, starting at index
   * @param value the value to write
   * @param index the index to write from
   * @throws IOException
   */
  public void write(byte[] value, long index) throws IOException {

    // case 1: empty file. Create a new buffer with math.min(value.length, int.max_val) capacity, recursive write call
    if (getFileSize() == 0) {
      MappedByteBuffer buffer = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, Math.min(value.length + index, Integer.MAX_VALUE));
      buffers.add(buffer);
      write(value, index);
    }

    // case 2: file too small. Increase last buffers size, add new buffer if necessary. Recursive write call
    else if (getFileSize() < (index + value.length)) {
      MappedByteBuffer buffer = buffers.get(buffers.size() - 1);
      if (buffer.capacity() < Integer.MAX_VALUE) {
        // increase last buffers size
        long newIndex = (long)(buffers.size() - 1) * (long)Integer.MAX_VALUE;
        int newCapacity = (int) Math.min(index + value.length - newIndex, Integer.MAX_VALUE);

        MappedByteBuffer newBuffer = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, newIndex , newCapacity);
        buffers.remove(buffers.size() - 1);
        buffers.add(newBuffer);


      } else {
        // add new buffer
        long newIndex = (long)buffers.size() * (long)Integer.MAX_VALUE;
        int newCapacity = (int) Math.min(index + value.length - newIndex, Integer.MAX_VALUE);
        MappedByteBuffer newBuffer = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, newIndex, newCapacity);
        buffers.add(newBuffer);
      }

      // recursive write call
      write(value, index);
    }

    // default: write from index to buffers capacity, recursive call with new index and remaining data if any
    else {
      MappedByteBuffer buffer = getBufferForIndex(index);
      int p0 = buffer.position();
      buffer.position((int) (index % Integer.MAX_VALUE));

      // check whether we have to write more than there's space remaining
      // If so, write to the remaining space, then call write again recursively
      // with the remaining values and an index right after the buffers end
      if (value.length > buffer.remaining()) {

        byte[] newValue = new byte[value.length - buffer.remaining()];
        System.arraycopy(value, buffer.remaining(), newValue, 0, newValue.length);


        buffer.put(value, 0, buffer.remaining());
        long newIndex = index + value.length - newValue.length;
        buffer.position(p0);

        write(newValue, newIndex);

      } else {
        buffer.put(value);
        buffer.position(p0);
      }
    }
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
   * @return The length of the file, measured in bytes
   * @throws IOException
   */
  public long getFileSize() throws IOException {
    return memoryMappedFile.length();
  }

}
