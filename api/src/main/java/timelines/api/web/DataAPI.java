package timelines.api.web;

import org.json.simple.JSONObject;
import timelines.config.Config;
import timelines.database.TimelinesDB;
import timelines.utils.TimeUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main data API
 */
@WebServlet("/api")
public class DataAPI extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DataAPI.class.getName());

    private TimelinesDB timelinesDB;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataAPI() {
        super();
        timelinesDB = new TimelinesDB();
    }

    /**
     * Handles get requests and returns data from DB
     *
     * @param request  must have parameters "from", "to", "points"
     * @param response JSON formatted data set
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.log(Level.INFO, "Data API called with parameters: {0}",
            new Object[]{request.toString()});

        response.setContentType("application/json");


        PrintWriter printWriter = response.getWriter();

        try {
            Date from = TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_FROM), DiagramAPIParameters.DATE_FORMAT);
            Date to = TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_TO), DiagramAPIParameters.DATE_FORMAT);
            JSONObject.writeJSONString(getDataForTimespan(from, to), printWriter);
        } catch (ParseException pe) {
            // TODO set http error header, error message
            JSONObject.writeJSONString(new HashMap(), printWriter);
        }

        printWriter.close();
    }

    private Map getDataForTimespan(Date from, Date to) throws IOException {
        ByteBuffer buffer = timelinesDB.getLowChannelData(from, to);

        LinkedHashMap<Long, Float> dataMap = new LinkedHashMap<>();
        long index = timelinesDB.getIndexForDate(from);

        while (buffer.hasRemaining()) {
            // TODO check if timestamp and data match
            Long timestamp = (index * 500) / Float.BYTES + Config.getStartDate().getTime();
            dataMap.put(timestamp, buffer.getFloat());
        }


        return dataMap;
    }
}
