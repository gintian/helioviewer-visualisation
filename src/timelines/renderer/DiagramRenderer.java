package timelines.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import timelines.api.web.DiagramAPI;
import timelines.database.TimelinesDB;
import timelines.utils.ImageUtils;

/**
 * Used to create diagram images
 */
public class DiagramRenderer {

  private static final Logger logger = Logger.getLogger(DiagramAPI.class.getName());

  private TimelinesDB timelinesDB;

  public static final int IMAGE_HEIGHT = 300;
  public static final int IMAGE_WIDTH = 1000;

  public static final int SUPER_SAMPLE = 3;

  /**
   * Creates a new renderer
   * @throws Exception if something goes wrong accessing the database
   */
  public DiagramRenderer() throws Exception {
    timelinesDB = new TimelinesDB();
  }

  /**
   * Creates a diagram image  mathing the given parameters
   * @param from the start of the time frame to be shown in the diagram
   * @param to the end of the time frame to be shown in the diagram
   * @return a BufferedImage containing the diagram
   * @throws IOException if accessing the database goes wrong
   */
  public BufferedImage getDiagramForTimespan(Date from, Date to) throws IOException {

    logger.log(Level.INFO, "Rendering image from {0} to {1}", new Object[] {from, to});
    Date startDate = new Date();

    ByteBuffer bufferL = timelinesDB.getLowChannelData(from, to);
    ByteBuffer bufferH = timelinesDB.getHighChannelData(from, to);

    float rangeStart = 10E-12f;
    float rangeEnd   = 10E-3f;
    float scaling = (float) (IMAGE_HEIGHT / (Math.log10(rangeEnd) - Math.log10(rangeStart)));
    int offset =  (int)(Math.abs(scaling * Math.log10(rangeEnd)));
    int opacity = 255;

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
      int posX = index;
      int posXNext = index + 1;

      int posYL = getPosY(valL, scaling, offset);
      int nextPosYL = getPosY(nextValL, scaling, offset);
      int posYH = getPosY(valH, scaling, offset);
      int nextPosYH = getPosY(nextValH, scaling, offset);

      try {
        // draw the low channel
        g.setColor(new Color(255, 0, 0, opacity));
        if(nextPosYL > 0 && posYL > 0) {
          g.drawLine(posX, posYL, posXNext, nextPosYL);
        }

        // draw the high channel
        g.setColor(new Color(0, 0, 255, opacity));
        if(nextPosYH > 0 && posYH > 0) {
          g.drawLine(posX, posYH, posXNext, nextPosYH);
        }

      } catch (ArrayIndexOutOfBoundsException e) {
        // thrown in case the given drawing coordinates are outside of the images size
        // should not occur with the checks in place
        e.printStackTrace();
      }
      index ++;
    }
    g.dispose();

    logger.log(Level.INFO, "Rendering completed in {0}ms", new Object[] {new Date().getTime() - startDate.getTime()});
    return ImageUtils.multiplyAlpha(3, ImageUtils.getScaledInstance(image, IMAGE_WIDTH, IMAGE_HEIGHT, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true));
  }

  private int getPosY(Float f, float scaling, float offset) {
      return (int) Math.min(IMAGE_HEIGHT - 1, (Math.abs(scaling * (Math.log10(f))) - offset));
  }

}
