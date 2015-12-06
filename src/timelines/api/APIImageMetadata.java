package timelines.api;

import timelines.utils.ImageUtils;
import timelines.utils.TimeUtils;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Tobi on 24.11.2015.
 */
public class APIImageMetadata {
    private static final Logger logger = Logger.getLogger(APIImageMetadata.class.getName());

    private Date dateFrom;
    private Date dateTo;
    private int zoomLevel;

    Map<String, String> imageMetadataMap;
    ArrayList<String> keys = new ArrayList<String>();

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
