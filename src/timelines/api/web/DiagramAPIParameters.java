package timelines.api.web;

import java.util.Date;

public class DiagramAPIParameters {

  public static final String PARAM_DATE_FROM = "dateFrom";
  public static final String PARAM_DATE_TO = "dateTo";
  public static final String PARAM_ZOOM_LEVEL = "zoomLevel";

  public static final String DATE_FORMAT = "yyyy-MM-dd:HH:mm:ss";

  private Date dateFrom;
  private Date dateTo;
  private int zoomLevel;

  public DiagramAPIParameters() {}

  public DiagramAPIParameters(Date from, Date to, int zoomLevel) {
    this.dateFrom = from;
    this.dateTo = to;
    this.zoomLevel = zoomLevel;
  }

  @Override
  public String toString() {
    return "Date from: " + dateFrom + " Date to: " + dateTo + " Zoom level: " + zoomLevel;
  }

  public Date getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(Date dateFrom) {
    this.dateFrom = dateFrom;
  }

  public Date getDateTo() {
    return dateTo;
  }

  public void setDateTo(Date dateTo) {
    this.dateTo = dateTo;
  }

  public int getZoomLevel() {
    return zoomLevel;
  }

  public void setZoomLevel(int zoomLevel) {
    this.zoomLevel = zoomLevel;
  }

}
