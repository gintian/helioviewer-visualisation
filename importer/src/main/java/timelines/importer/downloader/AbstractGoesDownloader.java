package timelines.importer.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import timelines.importer.csv.CsvToGoesSxrLeafConverter;
import timelines.importer.csv.GoesSxrLeaf;
import timelines.utils.TimeUtils;

/**
 * Abstract downloader class defining the base structure for all downloaders
 */
abstract class AbstractGoesDownloader implements IDownloader {

  private static final Logger logger = Logger.getLogger(AbstractGoesDownloader.class.getName());

  protected int currentGoesNr;

  private final int MIN_GOESNR;

  private final int MAX_GOESNR;

  private final CsvToGoesSxrLeafConverter csvParser;

  private boolean skipToData;

  /**
   * @param minGoesNr the minimum goes nr to get data from
   * @param maxGoesNr the maximum goes nr to get data from
   * @param csvToGoesSxrLeafConverter the CSV converter
   * @param skipToData whether or not we have to skip through header data in the csv file
   */
  public AbstractGoesDownloader(int minGoesNr, int maxGoesNr, CsvToGoesSxrLeafConverter csvToGoesSxrLeafConverter, boolean skipToData) {
    this.MIN_GOESNR = minGoesNr;
    this.currentGoesNr = minGoesNr;
    this.MAX_GOESNR = maxGoesNr;
    this.csvParser = csvToGoesSxrLeafConverter;
    this.skipToData = skipToData;
  }


  /**
   * Creates a new downloader which will skip to data by default
   * @see AbstractGoesDownloader#AbstractGoesDownloader(int, int, CsvToGoesSxrLeafConverter, boolean)
   * @param minGoesNr
   * @param maxGoesNr
   * @param csvToGoesSxrLeafConverter
   */
  public AbstractGoesDownloader(int minGoesNr, int maxGoesNr, CsvToGoesSxrLeafConverter csvToGoesSxrLeafConverter) {
    this(minGoesNr, maxGoesNr, csvToGoesSxrLeafConverter, true);
  }

  @Override
  public List<GoesSxrLeaf> getGoesSxrLeafs(Date minTimestamp, Date csvFileDate) {

    logger.log(Level.INFO, "getting all data from {0} which is dated after {1}", new Object[]{csvFileDate, minTimestamp});

    int downloadTrials = 0;

    while (downloadTrials < MAX_GOESNR) {
      try {
        URL url = createUrl(csvFileDate, currentGoesNr);
        final Date maxStartTimestamp = TimeUtils.getLaterDate(minTimestamp, getStartDateMidnight());
        final Date endTimestamp = getEndDateMidnight();

        List<GoesSxrLeaf> goesSxrLeafs = downloadGoesSxrLeafs(url, maxStartTimestamp, endTimestamp);
        return goesSxrLeafs;
      } catch (IOException e) {
        logger.log(Level.WARNING, e.toString());

        downloadTrials++;
        currentGoesNr++;
        if (currentGoesNr > MAX_GOESNR) {
          currentGoesNr = MIN_GOESNR;
        }
      } catch (Exception e) {
        e.printStackTrace();
        logger.log(Level.WARNING, e.toString());
      }
    }

    return null;
  }

  /**
   * Creates the appropriate URL with the given parameters.
   *
   * @param currentDateMidnight
   * @param goesNr
   * @return download URL
   */
  protected abstract URL createUrl(Date currentDateMidnight, Integer goesNr);

  /**
   * Downloads the CSV-File from the given URL and parses it to GoesSxrLeafs.
   *
   * @param url
   * @param startTimestamp only return GoesSxrLeafs with timestamp which are
   *          AFTER or EQUALS the startTimestamp.
   * @param endTimestamp only return GoesSxrLeafs with timestamp which are
   *          BEFORE or EQUALS the endTimestamp.
   * @return parsed GoesSxrLeafs.
   * @throws IOException
   */
  private List<GoesSxrLeaf> downloadGoesSxrLeafs(URL url, Date startTimestamp, Date endTimestamp) throws IOException {

    logger.log(Level.INFO, "accessing {0} from {1} to {2}", new Object[]{url, startTimestamp, endTimestamp});

    BufferedReader dataReader = new BufferedReader(new InputStreamReader(url.openStream())); // IOUtils.toBufferedReader(stringReader);

    List<GoesSxrLeaf> parsedLeafs = null;
    try {

      if(skipToData) {
        skipToData(dataReader);
      }
      parsedLeafs = csvParser.parseFile(startTimestamp, endTimestamp, dataReader);

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      dataReader.close();
    }
    return parsedLeafs;
  }


  /**
   * Moves the reader to the line which starts with "data:".
   *
   * @param reader
   * @throws IOException
   */
  private void skipToData(BufferedReader reader) throws IOException {
    String currentLine;

    while ((currentLine = reader.readLine()) != null) {
      if (currentLine.startsWith("data:")) {
        return;
      }
    }
  }

  @Override
  public void resetGoesNr() {
    currentGoesNr = MIN_GOESNR;
  }
}
