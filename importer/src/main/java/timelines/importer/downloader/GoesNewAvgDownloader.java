package timelines.importer.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timelines.importer.csv.CsvToGoesSxrLeafConverter;
import timelines.utils.StringUtils;
import timelines.utils.TimeUtils;

/**
 * Downloader for the new NOAA service
 * Used to access the time frame in which data is available in a 1 minute interval as average values
 */
public class GoesNewAvgDownloader extends AbstractGoesDownloader  {

  private String urlTemplate = "http://satdat.ngdc.noaa.gov/sem/goes/data/new_avg/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_{resolution}_{startdate}_{enddate}.csv";

  public static final Date START_DATE= new Date(504921600000L); // 1986-01-01 00:00:00
  public static final Date END_DATE= new Date(983401198000L); // the full downloader starts after this

  public static final int DEFAULT_GOES_NR_MIN = 6;
  public static final int DEFAULT_GOES_NR_MAX = 8;

  static class Columns {
    public static final int TIME_TAG = 0;
    public static final int XS = 2;
    public static final int XL = 1;
  }

  /**
   * Creates a new {@link GoesNewAvgDownloader} using the given goes nr. range
   * @param minGoesNr the minumum goes nr. to load data from
   * @param maxGoesNr the maximum goes nr. to load data from
   */
  public GoesNewAvgDownloader(int minGoesNr, int maxGoesNr) {
    super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter());
  }

  /**
   * Creates a new {@link GoesNewAvgDownloader} using the default goes nr. range
   */
  public GoesNewAvgDownloader() {
    super(DEFAULT_GOES_NR_MIN, DEFAULT_GOES_NR_MIN, createCsvToGoesSxrLeafConverter());
  }

  private static CsvToGoesSxrLeafConverter createCsvToGoesSxrLeafConverter() {
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
    params.put("resolution", "1m");

    final String urlString = StringUtils.format(urlTemplate, params);
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return url;
  }

  /**
   * Use the static final field START_DATE instead
   */
  @Override
  public Date getStartDateMidnight() {
    return START_DATE;
  }

  /**
   * Use the static final field END_DATE instead
   */
  @Override
  public Date getEndDateMidnight() {
    return END_DATE;
  }

}
