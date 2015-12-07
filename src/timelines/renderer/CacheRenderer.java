package timelines.renderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import timelines.api.web.DiagramAPI;
import timelines.database.TimelinesDB;
import timelines.utils.ImageUtils;
import timelines.utils.TimeUtils;

public class CacheRenderer {

  private static final Logger logger = Logger.getLogger(DiagramAPI.class.getName());

  public static final int CACHE_ZOOM_START = 11;
  public static final int CACHE_ZOOM_END = 18;

  public static final String CACHE_FODLER = "cache";

  private DiagramRenderer renderer;

  public CacheRenderer() throws Exception {
    renderer = new DiagramRenderer();
  }

  public void createCache() {

    logger.log(Level.INFO, "Creating cache");

    long currentTimePerImage;
//    Date now = new Date();

//    BufferedImage img;
//    File f;

    Map<String, String> customData = new HashMap<String, String>();

    // create lowest cache the "normal" way
//    logger.log(Level.INFO, "Creating cache for zoom level {0}", new Object[]{CACHE_ZOOM_START});
//    currentTimePerImage = (long) (DiagramRenderer.IMAGE_WIDTH * Math.pow(2, CACHE_ZOOM_START) * 1000);
//    customData.put("zoomLevel", "" + CACHE_ZOOM_START);
//    createCacheForZoomLevel(currentTimePerImage, customData, CACHE_ZOOM_START, TimelinesDB.DB_START_DATE);


    // create higher zoom levels by merging images from lower levels
    for (int i = CACHE_ZOOM_START + 1; i <= CACHE_ZOOM_END; i++) {

      logger.log(Level.INFO, "Creating cache for zoom level {0}", new Object[]{i});

      customData.put("zoomLevel", "" + i);
      currentTimePerImage = (long) (DiagramRenderer.IMAGE_WIDTH * Math.pow(2, i) * 1000);

      createCacheForZoomLevel(currentTimePerImage, customData, i, TimelinesDB.DB_START_DATE);

    }

  }


  private void createCacheForZoomLevel(long currentTimePerImage, Map<String, String> customData, int zoomLevel, Date currentStartDate) {
    BufferedImage img = null;
    File f;
    while (currentStartDate.before(new Date())) {
      try {

        Date endDate = new Date(currentStartDate.getTime() + currentTimePerImage);
        customData.put("startDate", TimeUtils.toString(currentStartDate, "yyyy-MM-dd:HH:mm:ss"));
        customData.put("endDate", TimeUtils.toString(endDate, "yyyy-MM-dd:HH:mm:ss"));

        // get image the normal way for the lowest zoom level
        if(zoomLevel == CACHE_ZOOM_START) {
          img = renderer.getDiagramForTimespan(currentStartDate, endDate);


        // merge two images from the lower level for higher cache levels
        } else {

          BufferedImage left = ImageIO.read(new File(getCacheImagePath(zoomLevel - 1, currentStartDate.getTime())));
          long previousTimePerImage = (long) (DiagramRenderer.IMAGE_WIDTH * Math.pow(2, (zoomLevel - 1)) * 1000);
          System.out.println(getCacheImagePath(zoomLevel - 1, currentStartDate.getTime() + previousTimePerImage));
          BufferedImage right = new BufferedImage(DiagramRenderer.IMAGE_WIDTH, DiagramRenderer.IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
          if (new Date(currentStartDate.getTime() + previousTimePerImage).before(new Date())) {
            right = ImageIO.read(new File(getCacheImagePath(zoomLevel - 1, currentStartDate.getTime() + previousTimePerImage)));
          }
          img = new BufferedImage(DiagramRenderer.IMAGE_WIDTH * 2, DiagramRenderer.IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
          Graphics2D g = img.createGraphics();
          g.drawImage(left, null, 0, 0);
          g.drawImage(right, null, left.getWidth(), 0);
          g.dispose();
          img = ImageUtils.getScaledInstance(img, DiagramRenderer.IMAGE_WIDTH, DiagramRenderer.IMAGE_HEIGHT, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
        }

        f = new File(getCacheImagePath(zoomLevel, currentStartDate.getTime()));
        ImageUtils.writeWithCustomData(new FileOutputStream(f), img, customData);

        currentStartDate = new Date(currentStartDate.getTime() + currentTimePerImage);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private String getCacheImagePath(int zoomLevel, long timestamp) {
    return CACHE_FODLER + "/" + zoomLevel + "/" + timestamp + ".png";
  }

  public static void main(String[] args) {

    try {

      CacheRenderer cacheRenderer = new CacheRenderer();
      cacheRenderer.createCache();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }



}
