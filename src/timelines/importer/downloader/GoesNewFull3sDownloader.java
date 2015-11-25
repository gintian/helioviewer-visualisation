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

public class GoesNewFull3sDownloader extends AbstractGoesDownloader {

  private String templateUrl = "http://satdat.ngdc.noaa.gov/sem/goes/data/new_full/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_3s_{date}_{date}.csv";

  public static final Date START_DATE = new Date(983401200000L); // 2001-03-01 00:00:00
  public static final Date END_DATE = new Date(1259621998000L); // 2009-11-30 23:59:58

  static class Columns {
    public static final int TIME_TAG = 0;
    public static final int A_FLUX = 1;
    public static final int B_FLUX = 2;
  }

  public GoesNewFull3sDownloader(int minGoesNr, int maxGoesNr) {
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
//    System.out.println("start date: " + goesNewFullStartDateMidnight);
    return START_DATE;
  }

  @Override
  public Date getEndDateMidnight() {
//    System.out.println("end date: " + TimeUtils.setMidnight(new Date()));
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
    System.out.println(url); // TODO
    return url;
  }

}
