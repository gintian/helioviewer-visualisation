package timelines.api.web;

import java.util.Date;

/**
 * Stores request parameters
 */
public class DiagramAPIParameters {

  public static final String PARAM_DATE_FROM = "dateFrom";
  public static final String PARAM_ZOOM_LEVEL = "zoomLevel";

  public static final String DATE_FORMAT = "yyyy-MM-dd:HH:mm:ss";

  private Date dateFrom;
  private int zoomLevel;

  @Override
  public String toString() {
    return "Date from: " + dateFrom + " Zoom level: " + zoomLevel;
  }

  public Date getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(Date dateFrom) {
    this.dateFrom = dateFrom;
  }

  public int getZoomLevel() {
    return zoomLevel;
  }

  public void setZoomLevel(int zoomLevel) {
    this.zoomLevel = zoomLevel;
  }

}
