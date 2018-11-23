package timelines.api.webapp;

// import timelines.config.Config;
import timelines.database.TimelinesDB;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main data API
 */
@WebServlet(name = "DataAPI", urlPatterns = { "/" }, loadOnStartup = 1)
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
                new Object[] { request.getParameterMap().toString() });

        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter printWriter = response.getWriter();

        try {
            String paramFrom = request.getParameter(DiagramAPIParameters.PARAM_DATE_FROM);
            paramFrom = paramFrom.substring(0, paramFrom.length() - 3);
            Date from = new Date(Long.parseLong(paramFrom + "000"));

            String paramTo = request.getParameter(DiagramAPIParameters.PARAM_DATE_TO);
            paramTo = paramTo.substring(0, paramTo.length() - 3);
            Date to = new Date(Long.parseLong(paramTo + "000"));

            int res = getResolution(from, to,
                    Integer.parseInt(request.getParameter(DiagramAPIParameters.PARAM_DATA_POINTS)));

            writeDataForTimespan(from, to, res, printWriter);
        } catch (Exception pe) {
            logger.log(Level.WARNING, pe.getMessage());
            printWriter.write("[]");
        }

        printWriter.close();
    }

    /**
     * Writes data from and to the specified Dates to the Writer
     */
    private void writeDataForTimespan(Date from, Date to, int resolution, Writer writer) throws IOException {
        long timeBefore = System.currentTimeMillis();

        ByteBuffer buffer = timelinesDB.getHighChannelData(from, to);
        long index = timelinesDB.getIndexForDate(from);
        long end = timelinesDB.getIndexForDate(to);
        long ticks = index;

        writer.write('[');
        boolean isFirst = true;

        while (buffer.hasRemaining() && index < end) {
            if (!isFirst)
                writer.write(',');
            isFirst = false;

            // aggregation
            float val = buffer.getFloat();
            if (Float.isNaN(val) || val < 0)
                val = 0;
            long timeIndex = index;
            ++index;

            // Max value
            for (int i = 0; i < resolution; ++i) {
                if (buffer.hasRemaining()) {
                    float next = buffer.getFloat();
                    if (next > val) {
                        val = next;
                        timeIndex = index + 1;
                    }
                }
                ++index;
            }

            writer.write('[');
            Long timestamp = from.getTime() + (timeIndex - ticks) * 2000;
            // timestamp = Config.getStartDate().getTime() + 2000 * timeIndex;
            writer.write(String.format("%d", timestamp));
            writer.write(',');
            if (Float.isNaN(val) || 0 > val) {
                writer.write('0');
            } else {
                writer.write(String.format("%.10f", val));
            }
            writer.write(']');
        }

        writer.write(']');

        long timeAfter = System.currentTimeMillis();
        long elapsed = timeAfter - timeBefore;
        logger.log(Level.INFO, "elapsed: " + elapsed + " ms");

    }

    private int getResolution(Date from, Date to, int points) {
        long spanSeconds = (to.getTime() - from.getTime()) / 1000;
        long resolutionSeconds = spanSeconds / (2 * points);

        if (1 > resolutionSeconds)
            resolutionSeconds = 1;

        return (int) resolutionSeconds;
    }
}
