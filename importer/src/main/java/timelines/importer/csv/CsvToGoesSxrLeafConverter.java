package timelines.importer.csv;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timelines.utils.TimeUtils;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Used to parse NOAA CSV files
 */
public class CsvToGoesSxrLeafConverter {

    private final int timestampColumn;
    private final int lowChannelColumn;
    private final int highChannelColumn;
    private final SimpleDateFormat dateTimeFormatter;

    /**
     * Creates a new {@link CsvToGoesSxrLeafConverter}.
     * @param timestampColumn the timestamps column index in the file to be parsed
     * @param lowChannelColumn the low channels column index in the file to be parsed
     * @param highChannelColumn the high channels column index in the file to be parsed
     * @param dateTimeFormatter the formatter to parse the timestamps with
     */
    public CsvToGoesSxrLeafConverter(int timestampColumn, int lowChannelColumn, int highChannelColumn,
            SimpleDateFormat dateTimeFormatter) {
        this.timestampColumn = timestampColumn;
        this.lowChannelColumn = lowChannelColumn;
        this.highChannelColumn = highChannelColumn;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Parses the given file and returns a list of {@link GoesSxrLeaf}s
     * @param startTimestamp the date a records timestamp has to be after in order to be deemed valid
     * @param endTimestamp the date a records timestamp has to be before in order to be deemed valid
     * @param fileReader the reader for the CSV file
     * @return a list of all valid {@link GoesSxrLeaf}s contained in the file
     * @throws IOException on error
     */
    public List<GoesSxrLeaf> parseFile(Date startTimestamp, Date endTimestamp, Reader fileReader) throws IOException {
        List<GoesSxrLeaf> goesSxrLeafs = new ArrayList<GoesSxrLeaf>();
        String[] nextLine;
        CSVReader csvReader = new CSVReader(fileReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER,
                1);

        try {
            while ((nextLine = csvReader.readNext()) != null) {
                GoesSxrLeaf goesSxrLeaf = parseDataRow(nextLine);

                if (isValid(goesSxrLeaf)
                        && TimeUtils.isFittingInInterval(goesSxrLeaf.getTimestamp(), startTimestamp, endTimestamp)) {
                    goesSxrLeafs.add(goesSxrLeaf);
                }
            }
        } finally {
            csvReader.close();
        }

        return goesSxrLeafs;
    }

    private GoesSxrLeaf parseDataRow(String[] line) {
        try {
            GoesSxrLeaf goesSxrLeaf = new GoesSxrLeaf();

            Date timestamp = dateTimeFormatter.parse(line[timestampColumn]);
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
            //e.printStackTrace();
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
        } else if (goesSxrLeaf.getLowChannel() < 1E-10 || goesSxrLeaf.getLowChannel() > 1E-2) {
            return false;
        } else if (goesSxrLeaf.getHighChannel() < 1E-10 || goesSxrLeaf.getHighChannel() > 1E-2) {
            return false;
        }
        return true;
    }
}
