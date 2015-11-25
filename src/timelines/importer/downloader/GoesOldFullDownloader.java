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

/**
 * @author Matthias Hug
 *
 * Used to download goes data from the old service.
 * GoesNr range of the available data: 1-7 and 91-92
 *
 */
public class GoesOldFullDownloader extends AbstractGoesDownloader {

  private String templateUrl = "http://satdat.ngdc.noaa.gov/sem/goes/data/full/xrays/{year}/x{goesnr}3{yymm}.csv";

  public static final Date START_DATE = new Date(141865200000L); // 1974-07-01
  public static final Date END_DATE = new Date(839961314000L); // 1996-08-13 20:35:14

  static class Columns {
    public static final int TIMESTAMP = 0;
    public static final int XS = 3;
    public static final int XL = 4;
  }

  public GoesOldFullDownloader(int minGoesNr, int maxGoesNr) {
    super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter(), false);
  }

  private static CsvToGoesSxrLeafConverter createCsvToGoesSxrLeafConverter() {
    final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return new CsvToGoesSxrLeafConverter(Columns.TIMESTAMP, Columns.XS, Columns.XL, dateTimeFormatter);
  }

  @Override
  public boolean isSameDownloadSite(Date thisDateMidnight, Date otherDateMidnight) {
    return TimeUtils.getYear(thisDateMidnight) == TimeUtils.getYear(otherDateMidnight);
  }

  @Override
  public Date getStartDateMidnight() {
    return START_DATE;
  }

  @Override
  public Date getEndDateMidnight() {
    return END_DATE;
  }

  @Override
  protected URL createUrl(Date currentDateMidnight, Integer goesNr) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("goesnr", String.format("%02d", goesNr));
    params.put("year", Integer.toString(TimeUtils.getYear(currentDateMidnight)));
    params.put("yymm", TimeUtils.toString(currentDateMidnight, "yyMM"));

    final String urlString = StringUtils.format(templateUrl, params);
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return url;
  }

  public static void main(String[] args) throws ParseException {
    Date date = TimeUtils.fromString("1974-07-01", "yyyy-MM-dd");
//    System.out.println(date.getTime());
    System.out.println((END_DATE.getTime() - START_DATE.getTime()) / 1000 / 2 * Float.BYTES / 1024 / 1024 );
  }

}
