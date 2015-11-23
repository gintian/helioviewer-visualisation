package timelines.api.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
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

import timelines.renderer.DiagramRenderer;
import timelines.utils.TimeUtils;

/**
 * Servlet implementation class DiagramAPI
 */
@WebServlet("/api")
public class DiagramAPI extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(DiagramAPI.class.getName());

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
      parameters.setDateTo(TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_TO), DiagramAPIParameters.DATE_FORMAT));
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

    BufferedImage img = renderer.getDiagramForTimespan(parameters.getDateFrom(), parameters.getDateTo());

    response.setContentType("image/png");
    OutputStream out = response.getOutputStream();
    ImageIO.write(img, "png", out);
    out.close();

  }


  private void writeAPIDoc(HttpServletResponse response) throws IOException {
    PrintWriter printWriter = response.getWriter();
    printWriter.println("<h1>Diagram API</h1>");
    printWriter.println("<p>Required Parameters:</p>");
    printWriter.println("<ul><li>'" + DiagramAPIParameters.PARAM_DATE_FROM + "' (" + DiagramAPIParameters.DATE_FORMAT + ")</li>");
    printWriter.println("<li>'" + DiagramAPIParameters.PARAM_DATE_TO + "' (" + DiagramAPIParameters.DATE_FORMAT + ")</li>");
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
}
