package ch.fhnw.i4ds.timelineviz.importer.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.fhnw.i4ds.timelineviz.importer.downloader.converter.CsvToGoesSxrLeafConverter;
import ch.fhnw.i4ds.timelineviz.utils.StringUtils;
import ch.fhnw.i4ds.timelineviz.utils.TimeUtils;

public class GoesNewFullDownloader extends AbstractGoesNewDownloader {

  private String templateUrl = "http://satdat.ngdc.noaa.gov/sem/goes/data/new_full/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_2s_{date}_{date}.csv";

  private Date goesNewFullStartDateMidnight;


  static class Columns {
    public static final int TIME_TAG = 0;
    public static final int A_FLUX = 3;
    public static final int B_FLUX = 6;
  }

  public GoesNewFullDownloader(int minGoesNr, int maxGoesNr) {
    super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter());

    try {
      goesNewFullStartDateMidnight = TimeUtils.setMidnight(TimeUtils.fromString("2009-12-01", "yyyy-MM-dd"));
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


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
    return goesNewFullStartDateMidnight;
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
