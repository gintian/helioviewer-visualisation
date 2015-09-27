package ch.fhnw.i4ds.timelineviz.importer.downloader.converter;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import ch.fhnw.i4ds.timelineviz.domain.GoesSxrLeaf;
import ch.fhnw.i4ds.timelineviz.utils.TimeUtils;

public class CsvToGoesSxrLeafConverter {

//	private static final Logger LOGGER = LoggerFactory.getLogger(CsvToGoesSxrLeafConverter.class);

	private final int timestampColumn;
	private final int lowChannelColumn;
	private final int highChannelColumn;
	private final DateTimeFormatter dateTimeFormatter;

	public CsvToGoesSxrLeafConverter(int timestampColumn, int lowChannelColumn, int highChannelColumn,
			DateTimeFormatter dateTimeFormatter) {
		this.timestampColumn = timestampColumn;
		this.lowChannelColumn = lowChannelColumn;
		this.highChannelColumn = highChannelColumn;
		this.dateTimeFormatter = dateTimeFormatter;
	}

	public Set<GoesSxrLeaf> parseFile(ReadableInstant startTimestamp, ReadableInstant endTimestamp, Reader fileReader)
			throws IOException {
		Set<GoesSxrLeaf> goesSxrLeafs = new HashSet<GoesSxrLeaf>();
		String[] nextLine;
		CSVReader csvReader = new CSVReader(fileReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER,	1);

		try {
			while ((nextLine = csvReader.readNext()) != null) {
				GoesSxrLeaf goesSxrLeaf = parseDataRow(nextLine);

				if (isValid(goesSxrLeaf)
						&& TimeUtils.isFittingInInterval(goesSxrLeaf.getTimestamp(), startTimestamp, endTimestamp)) {
					goesSxrLeafs.add(goesSxrLeaf);
				}
			}
		} finally {
			IOUtils.closeQuietly(csvReader);
		}

		return goesSxrLeafs;
	}

	private GoesSxrLeaf parseDataRow(String[] line) {
		try {
			GoesSxrLeaf goesSxrLeaf = new GoesSxrLeaf();

			Instant timestamp = Instant.parse(line[timestampColumn], dateTimeFormatter);
			float lowChannel = Float.parseFloat(line[lowChannelColumn]);
			float highChannel = Float.parseFloat(line[highChannelColumn]);

			// sanitize data
			if (lowChannel < 1E-10 || lowChannel > 1E-2) {
				lowChannel = 0;
			}
			if (highChannel < 1E-10 || highChannel > 1E-2) {
				highChannel = 0;
			}

			goesSxrLeaf.setTimestamp(timestamp);
			goesSxrLeaf.setLowChannel(lowChannel);
			goesSxrLeaf.setHighChannel(highChannel);

			return goesSxrLeaf;
		} catch (Exception e) {
		  e.printStackTrace();
			//LOGGER.info(e.toString() + " at line: " + Arrays.toString(line));
			return null;
		}
	}

	/**
	 * Validate GoesSxrLeaf.
	 *
	 * @param goesSxrLeaf
	 * @return is valid
	 */
	private boolean isValid(GoesSxrLeaf goesSxrLeaf) {
		if (goesSxrLeaf == null) {
			return false;
		} else if (goesSxrLeaf.getTimestamp() == null) {
			return false;
		} else if (goesSxrLeaf.getParentNode() != null) {
			return false;
		} else if (goesSxrLeaf.getLowChannel() < 1E-10 || goesSxrLeaf.getLowChannel() > 1E-2) {
			return false;
		} else if (goesSxrLeaf.getHighChannel() < 1E-10 || goesSxrLeaf.getHighChannel() > 1E-2) {
			return false;
		}
		return true;
	}
}
