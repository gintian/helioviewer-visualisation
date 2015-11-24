package timelines.renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    Date now = new Date();

    BufferedImage img;
    File f;

    Map<String, String> customData = new HashMap<String, String>();

    for (int i = CACHE_ZOOM_START; i <= CACHE_ZOOM_END; i++) {

      logger.log(Level.INFO, "Creating cache for zoom level {0}", new Object[]{i});

      customData.put("zoomLevel", "" + i);
      currentTimePerImage = (long) (DiagramRenderer.IMAGE_WIDTH * Math.pow(2, i) * 1000);
      Date currentStartDate = TimelinesDB.DB_START_DATE;

      while (new Date(currentStartDate.getTime() + currentTimePerImage).before(now)) {
        try {


          Date endDate = new Date(currentStartDate.getTime() + currentTimePerImage);
          customData.put("startDate", TimeUtils.toString(currentStartDate, "yyyy-MM-dd:HH:mm:ss"));
          customData.put("endDate", TimeUtils.toString(endDate, "yyyy-MM-dd:HH:mm:ss"));

          img = renderer.getDiagramForTimespan(currentStartDate, endDate);

          f = new File(CACHE_FODLER + "/" + i + "/" + currentStartDate.getTime() + ".png");
          ImageUtils.writeWithCustomData(new FileOutputStream(f), img, customData);

          currentStartDate = new Date(currentStartDate.getTime() + currentTimePerImage);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }

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
