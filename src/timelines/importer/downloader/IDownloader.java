package timelines.importer.downloader;

import java.util.Date;
import java.util.List;

import timelines.importer.csv.GoesSxrLeaf;

public interface IDownloader {

  /**
   * Download and parse one GoesSxrLeafs CSV.
   *
   * @param minTimestamp only get leafs with a timestamp that's after minTimestamp
   * @param csvFileDate download file for this date.
   * @return the GoesSxrLeafs from the file specified by csvFileDate with a timestamp >= minTimestamp.
   */
  public List<GoesSxrLeaf> getGoesSxrLeafs(Date minTimestamp, Date csvFileDate);

  /**
   * Compares the two DateMidnight and checks if it would use another download
   * url.
   *
   * @param thisDateMidnight
   * @param otherDateMidnight
   * @return uses same download url
   */
  public boolean isSameDownloadSite(Date thisDateMidnight, Date otherDateMidnight);

  /**
   * Get start of the data.
   *
   * @return start of data
   */
  public Date getStartDateMidnight();

  /**
   * Get end of the data.
   *
   * @return end of data
   */
  public Date getEndDateMidnight();

}