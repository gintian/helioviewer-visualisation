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
 * Used to access the time frame in which data is available in a 2 second interval
 */
public class GoesNewFullDownloader extends AbstractGoesDownloader {

  private String templateUrl = "https://satdat.ngdc.noaa.gov/sem/goes/data/new_full/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_2s_{date}_{date}.csv";

  public static final Date START_DATE = new Date(1259621998000L); // TimeUtils.setMidnight(TimeUtils.fromString("2009-12-01", "yyyy-MM-dd"));

  static class Columns {
    public static final int TIME_TAG = 0;
    public static final int A_FLUX = 3;
    public static final int B_FLUX = 6;
  }

  /**
   * Creates a new {@link GoesNewFullDownloader} using the given goes nr. range
   * @param minGoesNr the minumum goes nr. to load data from
   * @param maxGoesNr the maximum goes nr. to load data from
   */
  public GoesNewFullDownloader(int minGoesNr, int maxGoesNr) {
    super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter());

  }

  private static CsvToGoesSxrLeafConverter createCsvToGoesSxrLeafConverter() {
    final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").withZoneUTC();
    return new CsvToGoesSxrLeafConverter(Columns.TIME_TAG, Columns.A_FLUX, Columns.B_FLUX, dateTimeFormatter);
  }

  @Override
  public boolean isSameDownloadSite(Date thisDateMidnight, Date otherDateMidnight) {
    return thisDateMidnight.equals(otherDateMidnight);
  }

  @Override
  public Date getStartDateMidnight() {
    return START_DATE;
  }

  @Override
  public Date getEndDateMidnight() {
    return TimeUtils.setMidnight(new Date());
  }

  @Override
  protected URL createUrl(Date currentDateMidnight, Integer goesNr) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("goesnr", String.format("%02d", goesNr));
    params.put("year", Integer.toString(TimeUtils.getYear(currentDateMidnight)));
    params.put("month", String.format("%02d", TimeUtils.getMonth(currentDateMidnight)));
    params.put("date", TimeUtils.toString(currentDateMidnight, "yyyyMMdd"));

    final String urlString = StringUtils.format(templateUrl, params);
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return url;
  }

}
