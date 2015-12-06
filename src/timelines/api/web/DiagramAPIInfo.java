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
 * Servlet implementation class DiagramAPIInfo
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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  handleRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  handleRequest(request, response);
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
	  logger.log(Level.INFO, "Diagram API called with parameters: {0}", new Object[]{getParameterString(request.getParameterMap())});

    response.setContentType("application/json");
    PrintWriter printWriter = response.getWriter();
    printWriter.println("{");
    printWriter.println("\"width\": " + DiagramRenderer.IMAGE_WIDTH + ",");
    printWriter.println("\"height\": " + DiagramRenderer.IMAGE_HEIGHT + ",");
    printWriter.println("\"zoomLevelFrom\": " + 1 + ","); // TODO define that somewhere properly
    printWriter.println("\"zoomLevelTo\": " + 18 + ","); // TODO define that somewhere properly
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
