package ch.fhnw.i4ds.timelineviz.importer.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ch.fhnw.i4ds.timelineviz.importer.downloader.converter.CsvToGoesSxrLeafConverter;
import ch.fhnw.i4ds.timelineviz.utils.StringUtils;

public class GoesNewAvgDownloader extends AbstractGoesNewDownloader {

	private String urlTemplate = "http://satdat.ngdc.noaa.gov/sem/goes/data/new_avg/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_1m_{startdate}_{enddate}.csv";

	private DateMidnight goesNewAvgStartDateMidnight = new DateMidnight("1996-08-13");

	private DateMidnight goesNewAvgEndDateMidnight = new DateMidnight(2009-11-30);

	static class Columns {
		public static final int TIME_TAG = 0;
		public static final int XS = 1;
		public static final int XL = 2;
	}

	public GoesNewAvgDownloader(int minGoesNr, int maxGoesNr) {
		super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter());
	}

	private static CsvToGoesSxrLeafConverter createCsvToGoesSxrLeafConverter() {
		final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").withZoneUTC();
		return new CsvToGoesSxrLeafConverter(Columns.TIME_TAG, Columns.XS, Columns.XL, dateTimeFormatter);
	}

	@Override
	public boolean isSameDownloadSite(DateMidnight thisDateMidnight, DateMidnight otherDateMidnight) {
		return thisDateMidnight.getYear() == otherDateMidnight.getYear()
				&& thisDateMidnight.getMonthOfYear() == otherDateMidnight.getMonthOfYear();
	}

	@Override
	public DateMidnight getStartDateMidnight() {
		return goesNewAvgStartDateMidnight;
	}

	@Override
	public DateMidnight getEndDateMidnight() {
		return goesNewAvgEndDateMidnight;
	}

	@Override
	protected URL createUrl(DateMidnight currentDateMidnight, Integer goesNr) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("goesnr", String.format("%02d", goesNr));
		params.put("year", Integer.toString(currentDateMidnight.getYear()));
		params.put("month", String.format("%02d", currentDateMidnight.getMonthOfYear()));
		params.put("startdate", currentDateMidnight.dayOfMonth().withMinimumValue().toString("yyyyMMdd"));
		params.put("enddate", currentDateMidnight.dayOfMonth().withMaximumValue().toString("yyyyMMdd"));

		final String urlString = StringUtils.format(urlTemplate, params);
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

}
