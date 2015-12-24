package timelines.api.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import timelines.config.Config;
import timelines.database.TimelinesDB;
import timelines.importer.Importer;
import timelines.renderer.CacheRenderer;
import timelines.renderer.DiagramRenderer;
import timelines.utils.ImageUtils;
import timelines.utils.TimeUtils;

/**
 * The main timelines API
 * Used as an access point to the timeline images
 */
@WebServlet("/api")
public class DiagramAPI extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(DiagramAPI.class.getName());

  public static final int ZOOM_LEVEL_MIN = 1;
  public static final int ZOOM_LEVEL_MAX = 18;

  private Map<String, String> imageMetadata = new HashMap<String, String>();

  private DiagramRenderer renderer;

  private Config config;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public DiagramAPI() {
    super();
    try {
      config = new Config();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    handleRequest(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    handleRequest(request, response);
  }

  private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

    logger.log(Level.INFO, "Diagram API called with parameters: {0}", new Object[]{getParameterString(request.getParameterMap())});

    response.setContentType("text/html");

    DiagramAPIParameters parameters = new DiagramAPIParameters();
    try {
      parameters.setDateFrom(TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_FROM), DiagramAPIParameters.DATE_FORMAT));
      parameters.setZoomLevel(Integer.parseInt(request.getParameter(DiagramAPIParameters.PARAM_ZOOM_LEVEL)));

    } catch (ParseException | NumberFormatException | NullPointerException e) {
      logger.log(Level.INFO, "Diagram API request canceled due to missing / invalid parameters, sending HTTP 400", new Object[]{});
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      writeAPIDoc(response);
      return;
    }

    try {
      getImage(parameters, response);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      updateResources();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Used to get the image fitting the given parameters
   * @param parameters the parameters
   * @param response the response to write the image to
   * @throws Exception on error
   */
  private void getImage(DiagramAPIParameters parameters, HttpServletResponse response) throws Exception {

    renderer = new DiagramRenderer();

    long dataPoints = (long) Math.pow(2, parameters.getZoomLevel()) * DiagramRenderer.IMAGE_WIDTH;
    Date currentStartDate = parameters.getDateFrom();

    long imageOffset = (currentStartDate.getTime() - TimelinesDB.DB_START_DATE.getTime()) / 2000 / dataPoints;
    Date actualStartDate = new Date(TimelinesDB.DB_START_DATE.getTime() + imageOffset * dataPoints * 2000);

    BufferedImage img;

    if (parameters.getZoomLevel() >= CacheRenderer.CACHE_ZOOM_START) {
       img = getFromCache(actualStartDate, parameters.getZoomLevel());
    } else {

      Date endDate = new Date(actualStartDate.getTime() + dataPoints * 2000);

      imageMetadata.put("dateFrom", TimeUtils.toString(actualStartDate, "yyyy-MM-dd:HH:mm:ss"));
      imageMetadata.put("dateTo", TimeUtils.toString(endDate, "yyyy-MM-dd:HH:mm:ss"));
      imageMetadata.put("zoomLevel", "" + parameters.getZoomLevel());

      img = renderer.getDiagramForTimespan(actualStartDate, endDate);
    }

    response.setContentType("image/png");
    OutputStream out = response.getOutputStream();
    ImageUtils.writeWithCustomData(out, img, imageMetadata);
    out.close();
  }


  /**
   * Retrieves an image from the cache
   * @param date the start of the image to be retrieved
   * @param zoomLevel the zoom level for which to retrieve the image
   * @return the image fitting the given start date and zoom level or null if no fitting image could be found
   */
  private BufferedImage getFromCache(Date date, int zoomLevel) {

    logger.log(Level.INFO, "Getting image from Cache. Zoom level: {0}, Date: {1}", new Object[] {zoomLevel, date});

    File f = new File(config.getCachePath() + zoomLevel + "/" + date.getTime() + ".png");
    BufferedImage img = null;
    try {
      img = ImageIO.read(f);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return img;
  }

  /**
   * Writes information about the API
   * @param response the response to write the information to
   * @throws IOException on error
   */
  private void writeAPIDoc(HttpServletResponse response) throws IOException {
    PrintWriter printWriter = response.getWriter();
    printWriter.println("<h1>Diagram API</h1>");
    printWriter.println("<p>Required Parameters:</p>");
    printWriter.println("<ul><li>'" + DiagramAPIParameters.PARAM_DATE_FROM + "' (" + DiagramAPIParameters.DATE_FORMAT + ")</li>");
    printWriter.println("<li>'" + DiagramAPIParameters.PARAM_ZOOM_LEVEL + "' (int)</li></ul>");
    printWriter.close();
  }

  /**
   * Creates a String of the given parameter map
   * @param map the map to create the String from
   * @return a String of the given parameter map
   */
  private String getParameterString(Map<String, String[]> map) {
    String result = "";
    for (Entry<String, String[]> entry : map.entrySet()) {
      result += entry.getKey() + ": " + Arrays.toString(entry.getValue()) + " ";
    }
    return result;
  }

  /**
   * Used to update the database and if needed the cache
   * @throws Exception on error
   */
  private void updateResources() throws Exception {
    Importer importer = new Importer();
    boolean updated = importer.importNewData();

    if(updated) {
      CacheRenderer renderer = new CacheRenderer();
      renderer.updateCache();
    }
  }
}
