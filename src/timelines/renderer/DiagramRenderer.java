package timelines.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import timelines.api.web.DiagramAPI;
import timelines.database.TimelinesDB;
import timelines.importer.downloader.GoesNewAvgDownloader;

public class DiagramRenderer {

  private static final Logger logger = Logger.getLogger(DiagramAPI.class.getName());

  private TimelinesDB timelinesDB;

  public static final int IMAGE_HEIGHT = 300;
  public static final int IMAGE_WIDTH = 1000;

  public static final int SUPER_SAMPLE = 3;

  public DiagramRenderer() throws Exception {
    timelinesDB = new TimelinesDB();
  }

  public BufferedImage getDiagramForTimespan(Date from, Date to) throws Exception {

    logger.log(Level.INFO, "Rendering image from {0} to {1}", new Object[] {from, to});
    Date startDate = new Date();

    ByteBuffer bufferL = timelinesDB.getLowChannelData(from, to); // lowChannelDB.read(startIndex, (int) length);
    ByteBuffer bufferH = timelinesDB.getHighChannelData(from, to);

    float rangeStart = 10E-12f;
    float rangeEnd   = 10E-3f;
    float scaling = (float) (IMAGE_HEIGHT / (Math.log10(rangeEnd) - Math.log10(rangeStart)));
    int offset =  (int)(Math.abs(scaling * Math.log10(rangeEnd)));
//    System.out.println("offset: " + offset);
//    System.out.println("scaling: " + scaling);
//    System.out.println("min: " + (int)(Math.abs(scaling * (Math.log10(rangeStart))) - offset));
//    System.out.println("max: " + (int)(Math.abs(scaling * (Math.log10(rangeEnd))) - offset));

    float widthOnePercent = bufferL.remaining() / Float.BYTES / 100;

    int validValCount = 0;
    while(bufferL.hasRemaining()) {
      if(bufferL.getFloat() > 0) {
        validValCount ++;
      }
    }
    bufferL.position(0);
    System.out.println(validValCount);
    int opacity =  (int) Math.max(2, 255f / (validValCount / 5f / IMAGE_WIDTH));
//    opacity *= 2;
    opacity = Math.min(255, opacity);
    System.out.println(opacity);

    BufferedImage image = new BufferedImage(IMAGE_WIDTH * SUPER_SAMPLE, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int index = 0;
    float nextValL = bufferL.getFloat();
    float nextValH = bufferH.getFloat();
    while (bufferL.hasRemaining()) {

      float valL = nextValL;
      nextValL = bufferL.getFloat();
      float valH = nextValH;
      nextValH = bufferH.getFloat();
      int posX = Math.min((int) (index / widthOnePercent * (IMAGE_WIDTH * SUPER_SAMPLE / 100)), IMAGE_WIDTH * SUPER_SAMPLE - 1);
      int posXNext = Math.min((int) ((index + 1) / widthOnePercent * (IMAGE_WIDTH * SUPER_SAMPLE / 100)), IMAGE_WIDTH * SUPER_SAMPLE - 1);

      int posYL = getPosY(valL, scaling, offset);
      int nextPosYL = getPosY(nextValL, scaling, offset);
      int posYH = getPosY(valH, scaling, offset);
      int nextPosYH = getPosY(nextValH, scaling, offset);

      try {
        g.setColor(new Color(255, 0, 0, opacity));
        if(nextPosYL > 0 && posYL > 0) {
          g.drawLine(posX, posYL, posXNext, nextPosYL);
        }

        g.setColor(new Color(0, 0, 255, opacity));
        if(nextPosYH > 0 && posYH > 0) {
          g.drawLine(posX, posYH, posXNext, nextPosYH);
        }

//        image.setRGB(posX, posYL, Color.RED.getRGB());
//        image.setRGB(posX, posYH, Color.BLUE.getRGB());
      } catch (ArrayIndexOutOfBoundsException e) {
        // e.printStackTrace();
        System.out.println("erronous x: " + posX + " y: " + posYL + " val: " + valL);
      }
      index ++;
    }


    logger.log(Level.INFO, "Rendering completed in {0}", new Object[] {new Date().getTime() - startDate.getTime()});
    return getScaledImage(image, IMAGE_WIDTH, IMAGE_HEIGHT);
//    return image;
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
    cal.setTime(GoesNewAvgDownloader.END_DATE);
    cal.add(Calendar.DAY_OF_YEAR, -100);

//    BufferedImage image = renderer.getDiagramForTimespan(TimeUtils.fromString("1992-01-01", "yyyy-MM-dd"), TimeUtils.fromString("1993-01-01", "yyyy-MM-dd"));

    BufferedImage image = renderer.getDiagramForTimespan(cal.getTime(), GoesNewAvgDownloader.END_DATE);

    File file = new File("res/testRender.png");
    ImageIO.write(image, "png", file);

  }

  /**
   * Resizes an image using a Graphics2D object backed by a BufferedImage.
   * @param srcImg - source image to scale
   * @param w - desired width
   * @param h - desired height
   * @return - the new resized image
   */
  private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h){
      BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
      Graphics2D g2 = resizedImg.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(srcImg, 0, 0, w, h, null);
      g2.dispose();
      return resizedImg;
  }

}
