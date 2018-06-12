package timelines.database;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Instances of this class allow reading and writing from/to a file in a memory
 * mapped fashion
 */
public class MemoryMappedFile {

  private RandomAccessFile memoryMappedFile;

  private ArrayList<MappedByteBuffer> buffers = new ArrayList<>();

  /**
   * Creates a new MemoryMappedFile object
   * 
   * @param file the file to be handled in a memory mapped fashion
   * @throws IOException on error
   */
  public MemoryMappedFile(String file) throws IOException {

    memoryMappedFile = new RandomAccessFile(file, "rw");

    // create buffers over the entire files size
    long length = memoryMappedFile.length();

    while (length >= Integer.MAX_VALUE) {
      MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE,
          (long) buffers.size() * (long) Integer.MAX_VALUE, Integer.MAX_VALUE);
      buffers.add(out);
      length -= Integer.MAX_VALUE;
    }
    if (length > 0) {
      MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE,
          buffers.size() * Integer.MAX_VALUE, length);
      buffers.add(out);
    }
  }

  /**
   * Read length bytes from the file starting at index
   * 
   * @param index  from where to read
   * @param length how many bytes to read
   * @return the bytes read from index to index + length
   * @throws IOException
   */
  public byte[] read(long index, int length) throws IOException {
    byte[] result = new byte[length];

    if (index > getFileSize()) {
      return result;

    } else if (index + length > getFileSize()) {
      length = (int) (getFileSize() - index);
    }

    read(index, result, 0, length);
    return result;
  }

  /**
   * Reads data starting at the given index
   * 
   * @param index  the index from where to start reading
   * @param result an array to which the result will be written
   * @param offset the offset within the result array at which the result has to
   *               be written to
   * @param length amount of bytes to be read and written into the result array
   * @throws IOException on error
   */
  private void read(long index, byte[] result, int offset, int length) throws IOException {

    MappedByteBuffer buffer = getBufferForIndex(index);

    // return once we reached an index outside of the files length
    if (buffer == null) {
      return;
    }

    int originalPosition = buffer.position();
    buffer.position((int) (index % Integer.MAX_VALUE));

    if (buffer.remaining() == 0) {
      return;
    }

    // check whether we need to access multiple buffers
    if (length > buffer.remaining()) {
      read(index + buffer.remaining(), result, buffer.remaining(), result.length - buffer.remaining());
      buffer.get(result, 0, buffer.remaining());

    } else {
      buffer.get(result, offset, length);
    }

    buffer.position(originalPosition);
  }

  /**
   * Writes the given byte array to the file, starting at index
   * 
   * @param value the value to write
   * @param index the index to write from
   * @throws IOException
   */
  public void write(byte[] value, long index) throws IOException {

    // case 1: empty file. Create a new buffer with math.min(value.length,
    // int.max_val) capacity, recursive write call
    if (getFileSize() == 0) {
        MappedByteBuffer buffer = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
            Math.min(value.length + index, Integer.MAX_VALUE));
        buffers.add(buffer);
        write(value, index);
    }

    // case 2: file too small. Increase last buffers size, add new buffer if
    // necessary. Recursive write call
    else if (getFileSize() < (index + value.length)) {
      MappedByteBuffer buffer = buffers.get(buffers.size() - 1);
      if (buffer.capacity() < Integer.MAX_VALUE) {
        // increase last buffers size
        long newIndex = (long) (buffers.size() - 1) * (long) Integer.MAX_VALUE;
        int newCapacity = (int) Math.min(index + value.length - newIndex, Integer.MAX_VALUE);

        MappedByteBuffer newBuffer = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, newIndex,
            newCapacity);
        buffers.remove(buffers.size() - 1);
        buffers.add(newBuffer);

      } else {
        // add new buffer
        long newIndex = (long) buffers.size() * (long) Integer.MAX_VALUE;
        int newCapacity = (int) Math.min(index + value.length - newIndex, Integer.MAX_VALUE);
        MappedByteBuffer newBuffer = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, newIndex,
            newCapacity);
        buffers.add(newBuffer);
      }

      // recursive write call
      write(value, index);
    }

    // default: write from index to buffers capacity, recursive call with new index
    // and remaining data if any
    else {
      MappedByteBuffer buffer = getBufferForIndex(index);
      int p0 = buffer.position();
      try {
        buffer.position((int) (index % Integer.MAX_VALUE));
      } catch (IllegalArgumentException e) {
        buffer.position(0);
      }
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

  /**
   * Get the buffer that's needed to access the files given byte index
   * 
   * @param index the index from where the file has to be accessed.
   * @return the buffer needed to read from the file starting at the given
   *         index.<br>
   *         Null if the index is outside of the files size
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

  /**
   * Closes the file. A closed file cannot be reopened.
   * 
   * @see {@link RandomAccessFile#close()}
   * @throws IOException
   */
  public void close() throws IOException {
    memoryMappedFile.close();
  }

}
