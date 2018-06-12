package timelines.api.webapp;

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

    private static final int METHOD = 5;

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
        logger.log(Level.INFO, "Data API called with parameters: {0}", new Object[] { request.toString() });

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        PrintWriter printWriter = response.getWriter();

        try {
            // Date from =
            // TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_FROM),
            // DiagramAPIParameters.DATE_FORMAT);
            // Date to =
            // TimeUtils.fromString(request.getParameter(DiagramAPIParameters.PARAM_DATE_TO),
            // DiagramAPIParameters.DATE_FORMAT);
            Date from = new Date(Long.parseLong(request.getParameter(DiagramAPIParameters.PARAM_DATE_FROM)));
            Date to = new Date(Long.parseLong(request.getParameter(DiagramAPIParameters.PARAM_DATE_TO)));
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

            switch (METHOD) {
            case 0:
                // Average
                int count = 1;

                for (int i = 0; i < resolution; ++i) {
                    if (buffer.hasRemaining()) {
                        float v = buffer.getFloat();
                        if (!Float.isNaN(v) && v > 0) {
                            val += v;
                            ++count;
                        }
                    }
                    ++index;
                }
                val /= count;
                timeIndex = index;

                break;
            case 1:
                // Median
                boolean isOdd = 0 == resolution % 2;

                float medianHelper = 0;
                for (int i = 0; i < resolution; ++i) {
                    if (buffer.hasRemaining()) {
                        float v = buffer.getFloat();

                        if (!Float.isNaN(v) && v > 0) {
                            if (isOdd && i == Math.floor(resolution / 2)) {
                                val = v;
                            }

                            if (!isOdd && i == resolution / 2 - 1) {
                                medianHelper = v;
                            }
                            if (!isOdd && i == resolution / 2 + 1) {
                                val = (medianHelper + v) / 2;
                            }
                        }
                    }
                    ++index;
                }
                timeIndex = index;

                break;
            default:
                // Max value
                for (int i = 0; i < resolution; ++i) {
                    if (buffer.hasRemaining()) {
                        float next = buffer.getFloat();
                        if (next > val) {
                            val = next;
                            timeIndex = index;
                        }
                    }
                    ++index;
                }
            }

            writer.write('[');
            // TODO check if timestamp and data match
            Long timestamp = from.getTime() + 2000 * (timeIndex - ticks);
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
    }

    private int getResolution(Date from, Date to, int points) {
        long spanSeconds = (to.getTime() - from.getTime()) / 1000;
        long resolutionSeconds = spanSeconds / (2 * points);

        if (1 > resolutionSeconds)
            resolutionSeconds = 1;

        return (int) resolutionSeconds;
    }
}
