package timelines.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import timelines.api.web.DiagramAPI;
import timelines.database.TimelinesDB;
import timelines.importer.downloader.GoesNewAvgDownloader;
import timelines.importer.downloader.GoesNewFull3sDownloader;
import timelines.importer.downloader.GoesNewFullDownloader;
import timelines.importer.downloader.GoesOldFullDownloader;
import timelines.utils.ImageUtils;

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

//    float widthOnePercent = bufferL.remaining() / Float.BYTES / 100;

//    int validValCount = 0;
//    while(bufferL.hasRemaining()) {
//      if(bufferL.getFloat() > 0) {
//        validValCount ++;
//      }
//    }
//    bufferL.position(0);
//    System.out.println(validValCount);
//    int opacity =  (int) Math.max(2, 255f / (validValCount / 5f / IMAGE_WIDTH));
//  opacity = Math.min(255, opacity);
    int opacity = 255;
//    opacity *= 2;

//    System.out.println(opacity);

//    BufferedImage image = new BufferedImage(IMAGE_WIDTH * SUPER_SAMPLE, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    BufferedImage image = new BufferedImage(bufferL.remaining() / Float.BYTES, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
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
      int posX = index; //Math.min((int) (index / widthOnePercent * (IMAGE_WIDTH * SUPER_SAMPLE / 100)), IMAGE_WIDTH * SUPER_SAMPLE - 1);
      int posXNext = index + 1; //Math.min((int) ((index + 1) / widthOnePercent * (IMAGE_WIDTH * SUPER_SAMPLE / 100)), IMAGE_WIDTH * SUPER_SAMPLE - 1);

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
    g.dispose();

    logger.log(Level.INFO, "Rendering completed in {0}ms", new Object[] {new Date().getTime() - startDate.getTime()});
    return ImageUtils.multiplyAlpha(3, ImageUtils.getScaledInstance(image, IMAGE_WIDTH, IMAGE_HEIGHT, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true));
//    return ImageUtils.scaleImage(image, IMAGE_WIDTH, IMAGE_HEIGHT);
//    return image;
  }

  private int getPosY(Float f, float scaling, float offset) {
//    if (f == Float.NaN) {
//      System.out.println(f);
//    }
//    if (f != Float.NaN && f != 0 && f!= Float.MIN_VALUE) {

      return (int) Math.min(IMAGE_HEIGHT - 1, (Math.abs(scaling * (Math.log10(f))) - offset));
//    }
//    System.out.println(f);
//    return IMAGE_HEIGHT - 1;
  }

  public static void main(String[] args) throws Exception {

//    DiagramRenderer renderer = new DiagramRenderer();
//
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(GoesNewAvgDownloader.END_DATE);
//    cal.add(Calendar.DAY_OF_YEAR, -100);
//
//    BufferedImage image = renderer.getDiagramForTimespan(cal.getTime(), GoesNewAvgDownloader.END_DATE);
//
//    File file = new File("res/testRender.png");
//    ImageIO.write(image, "png", file);


    System.out.println("start old:    " + GoesOldFullDownloader.START_DATE);
    System.out.println("end old:      " + GoesOldFullDownloader.END_DATE);
    System.out.println("start avg:    " + GoesNewAvgDownloader.START_DATE);
    System.out.println("end avg:      " + GoesNewAvgDownloader.END_DATE);
    System.out.println("start new 3s: " + GoesNewFull3sDownloader.START_DATE);
    System.out.println("end new 3s:   " + GoesNewFull3sDownloader.END_DATE);
    System.out.println("start new:    " + GoesNewFullDownloader.START_DATE);

//    System.out.println("actual old end date: " + TimeUtils.fromString("1996-08-13 20:35:14", "yyyy-MM-dd HH:mm:ss").getTime());
//    System.out.println("actual new avg start date: " + TimeUtils.fromString("1996-08-13 20:35:16", "yyyy-MM-dd HH:mm:ss").getTime());
//    System.out.println("actual new avg end date: " + TimeUtils.fromString("2001-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime() );

//    System.out.println("actual new full 3s start date: " + TimeUtils.fromString("2001-03-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime());
//    System.out.println("actual new full 3s end date: " + TimeUtils.fromString("2009-11-30 23:59:58", "yyyy-MM-dd HH:mm:ss").getTime());

//    System.out.println("actual new full start date: " + TimeUtils.fromString("2009-12-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime());



  }



}
