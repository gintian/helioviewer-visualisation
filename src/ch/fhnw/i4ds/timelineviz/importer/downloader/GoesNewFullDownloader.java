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

public class GoesNewFullDownloader extends AbstractGoesNewDownloader {
	private String templateUrl = "http://satdat.ngdc.noaa.gov/sem/goes/data/new_full/{year}/{month}/goes{goesnr}/csv/g{goesnr}_xrs_2s_{date}_{date}.csv";

	private DateMidnight goesNewFullStartDateMidnight = new DateMidnight("2009-12-01");

	static class Columns {
		public static final int TIME_TAG = 0;
		public static final int A_FLUX = 3;
		public static final int B_FLUX = 6;
	}

	public GoesNewFullDownloader( int minGoesNr, int maxGoesNr) {
		super(minGoesNr, maxGoesNr, createCsvToGoesSxrLeafConverter());
	}

	private static CsvToGoesSxrLeafConverter createCsvToGoesSxrLeafConverter() {
		final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").withZoneUTC();
		return new CsvToGoesSxrLeafConverter(Columns.TIME_TAG, Columns.A_FLUX, Columns.B_FLUX, dateTimeFormatter);
	}

	@Override
	public boolean isSameDownloadSite(DateMidnight thisDateMidnight, DateMidnight otherDateMidnight) {
		return thisDateMidnight.isEqual(otherDateMidnight);
	}

	@Override
	public DateMidnight getStartDateMidnight() {
		return goesNewFullStartDateMidnight;
	}

	@Override
	public DateMidnight getEndDateMidnight() {
		return DateMidnight.now();
	}

	@Override
	protected URL createUrl(DateMidnight currentDateMidnight, Integer goesNr) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("goesnr", String.format("%02d", goesNr));
		params.put("year", Integer.toString(currentDateMidnight.getYear()));
		params.put("month", String.format("%02d", currentDateMidnight.getMonthOfYear()));
		params.put("date", currentDateMidnight.toString("yyyyMMdd"));

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
