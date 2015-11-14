package timelines.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import timelines.database.MemoryMappedFile;
import timelines.importer.downloader.GoesOldFullDownloader;
import timelines.utils.TimeUtils;

public class DiagramRenderer {

  private MemoryMappedFile lowChannelDB;
  private MemoryMappedFile highChannelDB;

  public static final int IMAGE_HEIGHT = 300;
  public static final int IMAGE_WIDTH = 1000;

  public DiagramRenderer() throws Exception {
    try {
      lowChannelDB = new MemoryMappedFile("res/dbL");
      highChannelDB = new MemoryMappedFile("res/dbH");
    } catch (IOException e) {
      throw new Exception("Database could not be accessed", e);
    }
  }

  public BufferedImage getDiagramForTimespan(Date from, Date to) throws Exception {

    long startIndex = (from.getTime() - GoesOldFullDownloader.START_DATE.getTime()) / 1000 / 2;
    long length = (to.getTime() - from.getTime()) / 1000 / 2 * Float.BYTES;

    if (length > Integer.MAX_VALUE) {
      throw new Exception("Can't access more than Integer.MAX_VALUE entries at once");
    }

    byte[] dataL = lowChannelDB.read(startIndex, (int) length);

    ByteBuffer bufferL = ByteBuffer.wrap(dataL);

    float rangeStart = 0.0000000000005f;
    rangeStart = (float) 1.0E-12;
    float rangeEnd   = 0.00000002f;
    rangeEnd = (float) 6.745E-7;
    float rangeOnePercent = rangeEnd - rangeStart / 100;

    float widthOnePercent = dataL.length / 4 / 100;


    // draw the image
    BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    int prevIndex = 0;
    int index = 0;
    System.out.println(dataL.length);
    while (bufferL.hasRemaining()) {
      float val = bufferL.getFloat();

//      System.out.println(index % IMAGE_WIDTH);
      int posX = (int) (index / widthOnePercent * 10);
      if (posX != prevIndex && posX != prevIndex + 1) {
        System.out.println(prevIndex + " " + posX);
      } else {
        prevIndex = posX;
      }
      int posY = Math.min(IMAGE_HEIGHT - 1, IMAGE_HEIGHT - (int) (val / rangeOnePercent * 3f));
//      System.out.println(val + " " + posY);
      try {
        Color current = new Color(image.getRGB(posX, posY));
        image.setRGB(posX, posY, new Color(255, 0, 0, Math.min(255, current.getAlpha() + 1)).getRGB());
      } catch (ArrayIndexOutOfBoundsException e) {
        // e.printStackTrace();
        System.out.println("erronous x: " + posX + " y: " + posY + " val: " + val);
      }
      index ++;
    }

    return image;
  }

  public static void main(String[] args) throws Exception {

    DiagramRenderer renderer = new DiagramRenderer();

    Calendar cal = Calendar.getInstance();
    cal.setTime(GoesOldFullDownloader.START_DATE);
    cal.add(Calendar.YEAR, 1);

    BufferedImage image = renderer.getDiagramForTimespan(TimeUtils.fromString("1992-01-01", "yyyy-MM-dd"), TimeUtils.fromString("1993-01-01", "yyyy-MM-dd"));

    File file = new File("res/testRender.jpg");
    ImageIO.write(image, "jpg", file);

  }

}
