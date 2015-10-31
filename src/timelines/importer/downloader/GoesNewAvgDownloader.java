package timelines.importer.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timelines.importer.csv.CsvToGoesSxrLeafConverter;
import timelines.utils.StringUtils;
import timelines.utils.TimeUtils;

public class GoesNewAvgDownloader extends AbstractGoesDownloader  {

  private String urlTemplate = "http://satdat.ngdc.noaa.gov/sem/goes/data/new_avg/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_1m_{startdate}_{enddate}.csv";

  public static final Date START_DATE= new Date(842565600000L);
  public static final Date END_DATE= new Date(1259535600000L); // the full downloader starts after this

//  private Date goesNewAvgEndDateMidnight;

  static class Columns {
    public static final int TIME_TAG = 0;
    public static final int XS = 1;
    public static final int XL = 2;
  }

  public GoesNewAvgDownloader(int minGoesNr, int maxGoesNr) throws ParseException {
    super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter());

    //goesNewAvgStartDateMidnight = TimeUtils.setMidnight(TimeUtils.fromString("1996-08-13", "yyyy-MM-dd"));
//    goesNewAvgEndDateMidnight = TimeUtils.setMidnight(TimeUtils.fromString("2009-11-30", "yyyy-MM-dd"));
  }

  private static CsvToGoesSxrLeafConverter createCsvToGoesSxrLeafConverter() {
//    final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").withZoneUTC();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return new CsvToGoesSxrLeafConverter(Columns.TIME_TAG, Columns.XS, Columns.XL, format);
  }

  @Override
  public boolean isSameDownloadSite(Date thisDateMidnight, Date otherDateMidnight) {
    return TimeUtils.getYear(thisDateMidnight) == TimeUtils.getYear(otherDateMidnight) && TimeUtils.getMonth(thisDateMidnight) == TimeUtils.getMonth(otherDateMidnight);
  }


  @Override
  protected URL createUrl(Date currentDateMidnight, Integer goesNr) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("goesnr", String.format("%02d", goesNr));
    params.put("year", Integer.toString(TimeUtils.getYear(currentDateMidnight)));
    params.put("month", String.format("%02d", TimeUtils.getMonth(currentDateMidnight)));
    params.put("startdate", TimeUtils.toString(TimeUtils.firstDayOfMonth(currentDateMidnight), "yyyyMMdd")); // currentDateMidnight.dayOfMonth().withMinimumValue().toString("yyyyMMdd"));
    params.put("enddate", TimeUtils.toString(TimeUtils.lastDayOfMonth(currentDateMidnight), "yyyyMMdd")); // currentDateMidnight.dayOfMonth().withMaximumValue().toString("yyyyMMdd"));

    final String urlString = StringUtils.format(urlTemplate, params);
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    System.out.println(url);
    return url;
  }

  /**
   * Use the static final field START_DATE instead
   */
  @Deprecated
  @Override
  public Date getStartDateMidnight() {
    // TODO Auto-generated method stub
    return START_DATE;
  }

  /**
   * Use the static final field END_DATE instead
   */
  @Deprecated
  @Override
  public Date getEndDateMidnight() {
    // TODO Auto-generated method stub
    return END_DATE;
  }

}
