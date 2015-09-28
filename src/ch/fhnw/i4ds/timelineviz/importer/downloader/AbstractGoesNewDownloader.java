package ch.fhnw.i4ds.timelineviz.importer.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateMidnight;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import ch.fhnw.i4ds.timelineviz.domain.GoesSxrLeaf;
import ch.fhnw.i4ds.timelineviz.importer.downloader.converter.CsvToGoesSxrLeafConverter;
import ch.fhnw.i4ds.timelineviz.utils.TimeUtils;

abstract class AbstractGoesNewDownloader implements IDownloader {
  //	protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected int currentGoesNr;

  private final int MIN_GOESNR;

  private final int MAX_GOESNR;

  private final CsvToGoesSxrLeafConverter csvParser;

  public AbstractGoesNewDownloader(int minGoesNr, int maxGoesNr, CsvToGoesSxrLeafConverter csvToGoesSxrLeafConverter) {
    this.MIN_GOESNR = minGoesNr;
    this.currentGoesNr = minGoesNr;
    this.MAX_GOESNR = maxGoesNr;
    this.csvParser = csvToGoesSxrLeafConverter;
  }

  @Override
  public Set<GoesSxrLeaf> getGoesSxrLeafs(Instant startTimestamp, DateMidnight currentDateMidnight) {
    int downloadTrials = 0;

    while (downloadTrials < MAX_GOESNR) {
      try {
        URL url = createUrl(currentDateMidnight, currentGoesNr);
        final ReadableInstant maxStartTimestamp = TimeUtils.getMaxReadableInstant(startTimestamp, getStartDateMidnight());
        final DateMidnight endTimestamp = getEndDateMidnight();

        Set<GoesSxrLeaf> goesSxrLeafs = downloadGoesSxrLeafs(url, maxStartTimestamp, endTimestamp);
        return goesSxrLeafs;
      } catch (IOException e) {
        //				logger.warn(e.toString());

        downloadTrials++;
        currentGoesNr++;
        if (currentGoesNr > MAX_GOESNR) {
          currentGoesNr = MIN_GOESNR;
        }
      } catch (Exception e) {
        e.printStackTrace();
        //				logger.warn(e.toString());
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
  protected abstract URL createUrl(DateMidnight currentDateMidnight, Integer goesNr);

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
  private Set<GoesSxrLeaf> downloadGoesSxrLeafs(URL url, ReadableInstant startTimestamp, ReadableInstant endTimestamp) throws IOException {
    //		logger.info(DateTime.now().toString() + " - Importing data from: " + url);

    String data = IOUtils.toString(url);
    final StringReader stringReader = new StringReader(data);
    BufferedReader dataReader = IOUtils.toBufferedReader(stringReader);

    Set<GoesSxrLeaf> parsedLeafs = null;
    try {
      skipToData(dataReader);
      parsedLeafs = csvParser.parseFile(startTimestamp, endTimestamp, dataReader);
    } finally {
      IOUtils.closeQuietly(dataReader);
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
}
