package timelines.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import timelines.database.TimelinesDB;
import timelines.importer.downloader.GoesOldFullDownloader;
import timelines.utils.TimeUtils;

public class DiagramRenderer {

  private TimelinesDB timelinesDB;

  public static final int IMAGE_HEIGHT = 300;
  public static final int IMAGE_WIDTH = 1000;

  public DiagramRenderer() throws Exception {
    timelinesDB = new TimelinesDB();
  }

  public BufferedImage getDiagramForTimespan(Date from, Date to) throws Exception {

    ByteBuffer bufferL = timelinesDB.getLowChannelData(from, to); // lowChannelDB.read(startIndex, (int) length);
    ByteBuffer bufferH = timelinesDB.getHighChannelData(from, to);

    float rangeStart = 10E-11f;
    float rangeEnd   = 10E-3f;
    float scaling = (float) (IMAGE_HEIGHT / (Math.log10(rangeEnd) - Math.log10(rangeStart)));
    int offset =  (int)(Math.abs(scaling * Math.log10(rangeEnd)));
    System.out.println("offset: " + offset);
    System.out.println("scaling: " + scaling);
    System.out.println("min: " + (int)(Math.abs(scaling * (Math.log10(rangeStart))) - offset));
    System.out.println("max: " + (int)(Math.abs(scaling * (Math.log10(rangeEnd))) - offset));

    float widthOnePercent = bufferL.remaining() / Float.BYTES / 100;

    BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    int index = 0;
    while (bufferL.hasRemaining()) {

      float valL = bufferL.getFloat();
      float valH = bufferH.getFloat();
      int posX = (int) (index / widthOnePercent * (IMAGE_WIDTH / 100));

      int posYL = getPosY(valL, scaling, offset);
      int posYH = getPosY(valH, scaling, offset);

      try {
        image.setRGB(posX, posYL, Color.RED.getRGB());
        image.setRGB(posX, posYH, Color.BLUE.getRGB());
      } catch (ArrayIndexOutOfBoundsException e) {
        // e.printStackTrace();
        System.out.println("erronous x: " + posX + " y: " + posYL + " val: " + valL);
      }
      index ++;
    }

    return image;
  }

  private int getPosY(Float f, float scaling, float offset) {
    if (f != Float.NaN && f != 0) {
      return (int) Math.min(299, (Math.abs(scaling * (Math.log10(f))) - offset));
    }
    return 299;
  }

  public static void main(String[] args) throws Exception {

    DiagramRenderer renderer = new DiagramRenderer();

    Calendar cal = Calendar.getInstance();
    cal.setTime(GoesOldFullDownloader.START_DATE);
    cal.add(Calendar.YEAR, 1);

    BufferedImage image = renderer.getDiagramForTimespan(TimeUtils.fromString("1992-01-01", "yyyy-MM-dd"), TimeUtils.fromString("1993-01-01", "yyyy-MM-dd"));

    File file = new File("res/testRender.png");
    ImageIO.write(image, "png", file);

  }

}
