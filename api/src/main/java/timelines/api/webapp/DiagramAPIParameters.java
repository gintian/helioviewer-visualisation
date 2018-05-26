package timelines.api.webapp;

import java.util.Date;

/**
 * Stores request parameters
 */
public class DiagramAPIParameters {

    static final String PARAM_DATE_FROM = "from";
    static final String PARAM_DATE_TO = "to";
    static final String PARAM_DATA_POINTS = "points";

    static final String PARAM_ZOOM_LEVEL = "zoomLevel";

    static final String DATE_FORMAT = "yyyy-M-d:H:m:s";

    private Date dateFrom;
    private int zoomLevel;

    @Override
    public String toString() {
        return "Date from: " + dateFrom + " Zoom level: " + zoomLevel;
    }

    Date getDateFrom() {
        return dateFrom;
    }

    void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    int getZoomLevel() {
        return zoomLevel;
    }

    void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

}
