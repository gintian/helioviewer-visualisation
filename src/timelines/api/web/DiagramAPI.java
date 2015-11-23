package timelines.api.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import timelines.renderer.DiagramRenderer;
import timelines.utils.TimeUtils;

/**
 * Servlet implementation class DiagramAPI
 */
@WebServlet("/api")
public class DiagramAPI extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(DiagramAPI.class.getName());

  private Map<String, String> imageMetadata = new HashMap<String, String>();

  private DiagramRenderer renderer;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public DiagramAPI() {
    super();
    // TODO Auto-generated constructor stub
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
//      parameters.setDateTo(TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_TO), DiagramAPIParameters.DATE_FORMAT));
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

//    PrintWriter printWriter = response.getWriter();
//    printWriter.println("<h1>Diagram API</h1>");
//    printWriter.print(parameters.toString());
//    printWriter.close();

  }

  private void getImage(DiagramAPIParameters parameters, HttpServletResponse response) throws Exception {

    renderer = new DiagramRenderer();

    long dataPoints = (long) Math.pow(2, parameters.getZoomLevel()) * DiagramRenderer.IMAGE_WIDTH;
//    dataPoints = (parameters.getZoomLevel() + 1) * DiagramRenderer.IMAGE_WIDTH;
    Date currentEndDate = new Date(parameters.getDateFrom().getTime() + dataPoints * 2000);
    Date currentStartDate = parameters.getDateFrom();

    imageMetadata.put("dateFrom", TimeUtils.toString(currentStartDate, "yyyy-MM-dd:HH:mm:ss"));
    imageMetadata.put("dateTo", TimeUtils.toString(currentEndDate, "yyyy-MM-dd:HH:mm:ss"));

    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

//    while (currentEndDate.before(parameters.getDateTo())) {
      images.add(renderer.getDiagramForTimespan(currentStartDate, currentEndDate));
//      currentStartDate = new Date(currentEndDate.getTime() + 2000);
//      currentEndDate = new Date(currentEndDate.getTime() + dataPoints * 2000);
//    }

//    BufferedImage img = renderer.getDiagramForTimespan(parameters.getDateFrom(), parameters.getDateTo());

    response.setContentType("image/png");
    OutputStream out = response.getOutputStream();
    for (BufferedImage img : images) {
      writeImageWithCustomData(out, img, imageMetadata);
//      ImageIO.write(img, "png", out);
//      out.flush();
    }
    out.close();

  }


  private void writeAPIDoc(HttpServletResponse response) throws IOException {
    PrintWriter printWriter = response.getWriter();
    printWriter.println("<h1>Diagram API</h1>");
    printWriter.println("<p>Required Parameters:</p>");
    printWriter.println("<ul><li>'" + DiagramAPIParameters.PARAM_DATE_FROM + "' (" + DiagramAPIParameters.DATE_FORMAT + ")</li>");
//    printWriter.println("<li>'" + DiagramAPIParameters.PARAM_DATE_TO + "' (" + DiagramAPIParameters.DATE_FORMAT + ")</li>");
    printWriter.println("<li>'" + DiagramAPIParameters.PARAM_ZOOM_LEVEL + "' (int)</li></ul>");
    printWriter.close();
  }

  private String getParameterString(Map<String, String[]> map) {
    String result = "";
    for (Entry<String, String[]> entry : map.entrySet()) {
      result += entry.getKey() + ": " + Arrays.toString(entry.getValue()) + " ";
    }
    return result;
  }

  private void writeImageWithCustomData(OutputStream out, BufferedImage buffImg, Map<String, String> customData) throws Exception {
    ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

    ImageWriteParam writeParam = writer.getDefaultWriteParam();
    ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);

    //adding metadata
    IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
    IIOMetadataNode text = new IIOMetadataNode("tEXt");

    for (Entry<String, String> entry : customData.entrySet()) {

      IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
      textEntry.setAttribute("keyword", entry.getKey());
      textEntry.setAttribute("value", entry.getValue());
      text.appendChild(textEntry);
    }

    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
    root.appendChild(text);

    metadata.mergeTree("javax_imageio_png_1.0", root);

    //writing the data
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageOutputStream stream = ImageIO.createImageOutputStream(out);
    writer.setOutput(stream);
    writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
    stream.close();
  }
}
