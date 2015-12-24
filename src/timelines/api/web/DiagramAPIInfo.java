package timelines.api.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import timelines.renderer.DiagramRenderer;

/**
 * Used to retrieve information regarding the DiagramAPI
 */
@WebServlet("/apiInfo")
public class DiagramAPIInfo extends HttpServlet {
  private static final long serialVersionUID = 2L;
  private static final Logger logger = Logger.getLogger(DiagramAPIInfo.class.getName());

  /**
   * @see HttpServlet#HttpServlet()
   */
  public DiagramAPIInfo() {
    super();
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

  /**
   * Writes information regarding the API as JSON into the given response
   * @param request the request
   * @param response the response to write to
   * @throws IOException on error
   */
  private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
    logger.log(Level.INFO, "API Info called with parameters: {0}", new Object[] { getParameterString(request.getParameterMap()) });

    response.setContentType("application/json");
    PrintWriter printWriter = response.getWriter();
    printWriter.println("{");
    printWriter.println("\"width\": " + DiagramRenderer.IMAGE_WIDTH + ",");
    printWriter.println("\"height\": " + DiagramRenderer.IMAGE_HEIGHT + ",");
    printWriter.println("\"zoomLevelFrom\": " + DiagramAPI.ZOOM_LEVEL_MIN + ",");
    printWriter.println("\"zoomLevelTo\": " + DiagramAPI.ZOOM_LEVEL_MAX + ",");
    printWriter.println("}");
    printWriter.close();
  }

  //TODO move somewhere accesible for all servlets
  private String getParameterString(Map<String, String[]> map) {
    String result = "";
    for (Entry<String, String[]> entry : map.entrySet()) {
      result += entry.getKey() + ": " + Arrays.toString(entry.getValue()) + " ";
    }
    return result;
  }

}
