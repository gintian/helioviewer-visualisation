package timelines.importer.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import timelines.importer.csv.CsvToGoesSxrLeafConverter;
import timelines.importer.csv.GoesSxrLeaf;
import timelines.utils.TimeUtils;

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
  public Set<GoesSxrLeaf> getGoesSxrLeafs(Date startTimestamp, Date currentDateMidnight) {
    int downloadTrials = 0;

    while (downloadTrials < MAX_GOESNR) {
      try {
        URL url = createUrl(currentDateMidnight, currentGoesNr);
        final Date maxStartTimestamp = TimeUtils.getMaxReadableInstant(startTimestamp, getStartDateMidnight());
        final Date endTimestamp = getEndDateMidnight();

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


  public byte[] getGoesSxrData(Date startTimestamp, Date currentDateMidnight) {
    int downloadTrials = 0;

    while (downloadTrials < MAX_GOESNR) {
      try {
        URL url = createUrl(currentDateMidnight, currentGoesNr);
        final Date maxStartTimestamp = TimeUtils.getMaxReadableInstant(startTimestamp, getStartDateMidnight());
        final Date endTimestamp = getEndDateMidnight();

        // TODO
        // byte[] goesSxrData = downloadGoesSxrLeafs(url, maxStartTimestamp, endTimestamp);
        // return goesSxrData;
        throw new IOException(); // TODO
      } catch (IOException e) {
        //        logger.warn(e.toString());

        downloadTrials++;
        currentGoesNr++;
        if (currentGoesNr > MAX_GOESNR) {
          currentGoesNr = MIN_GOESNR;
        }
      } catch (Exception e) {
        e.printStackTrace();
        //        logger.warn(e.toString());
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
  private Set<GoesSxrLeaf> downloadGoesSxrLeafs(URL url, Date startTimestamp, Date endTimestamp) throws IOException {

    BufferedReader dataReader = new BufferedReader(new InputStreamReader(url.openStream())); // IOUtils.toBufferedReader(stringReader);

    Set<GoesSxrLeaf> parsedLeafs = null;
    try {
      skipToData(dataReader);
      parsedLeafs = csvParser.parseFile(startTimestamp, endTimestamp, dataReader);

    } catch (IOException e) {
      // TODO: handle exception
      e.printStackTrace();

    } finally {
      dataReader.close();
//      IOUtils.closeQuietly(dataReader);
    }
    return parsedLeafs;
  }

  private byte[] downloadGoesSxrData(URL url, Date startTimestamp, Date endTimestamp) throws IOException {

    BufferedReader dataReader = new BufferedReader(new InputStreamReader(url.openStream())); // IOUtils.toBufferedReader(stringReader);

    byte[] parsedData = null;
    try {
      skipToData(dataReader);
      //parsedData = csvParser.parseFile(startTimestamp, endTimestamp, dataReader); TODO

    } catch (IOException e) {
      // TODO: handle exception
      e.printStackTrace();

    } finally {
      dataReader.close();
//      IOUtils.closeQuietly(dataReader);
    }
    return parsedData;
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
