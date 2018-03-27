package timelines.api;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.stream.ImageInputStream;

import timelines.utils.ImageUtils;
import timelines.utils.TimeUtils;

/**
 * Class used to read the metadata from an image sent by the API
 */
public class APIImageMetadata {
    private static final Logger logger = Logger.getLogger(APIImageMetadata.class.getName());

    private Date dateFrom;
    private Date dateTo;
    private int zoomLevel;

    Map<String, String> imageMetadataMap;
    ArrayList<String> keys = new ArrayList<String>();

    /**
     * Used to read the metadata from an image
     * @param iis the input stream to read from
     */
    public APIImageMetadata(ImageInputStream iis){
        keys.add("dateFrom");
        keys.add("dateTo");
        keys.add("zoomLevel");
        try {
            imageMetadataMap = ImageUtils.readCustomData(iis,keys);
            dateFrom = TimeUtils.fromString(imageMetadataMap.get("dateFrom"),"yyyy-MM-dd:HH:mm:ss");
            dateTo = TimeUtils.fromString(imageMetadataMap.get("dateTo"),"yyyy-MM-dd:HH:mm:ss");
            zoomLevel = Integer.parseInt(imageMetadataMap.get("zoomLevel"));
        }catch (IOException e){
            logger.log(Level.WARNING, "Metadata could not be read");
        }catch (ParseException e){
            logger.log(Level.WARNING, "Not all data could be parsed");
        }
    }

    /**
     * Creates a new metadata object using the given parameters
     * @param dateFrom the start date of the timeframe covered by the image
     * @param dateTo the end date of the timeframe covered by the image
     * @param zoomLevel the zoom level of the image
     */
    public APIImageMetadata(Date dateFrom, Date dateTo, int zoomLevel){
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.zoomLevel = zoomLevel;
    }


    public Date getDateFrom() {
        Date returnDate = dateFrom;
        return returnDate;
    }

    public Date getDateTo() {
        Date returnDate = dateTo;
        return returnDate;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }
}
