package timelines.database;

public class DataEntry {

  private float low;
  private float high;

  // we are using a 32 bit --> second only date for performance reasons
  private int date;

  public static final int ENTRY_LENGTH = 3;

  public static final int VALUE_LENGTH = 4;

  public DataEntry(float low, float high, float date) {

  }


  public float getLow() {
    return low;
  }
  public float getHigh() {
    return high;
  }
  public float getDate() {
    return date;
  }

}
