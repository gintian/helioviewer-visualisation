package timelines.importer.downloader;

import java.util.Date;
import java.util.Set;

import timelines.importer.csv.GoesSxrLeaf;

public interface IDownloader {

  /**
   * Download and parse GoesSxrLeafs.
   *
   * @param startTimestamp only return leafs which timestamp is after
   *          startTimestamp
   * @param currentDateMidnight download leafs at this date.
   * @return downloader GoesSxrLeafs.
   */
  public Set<GoesSxrLeaf> getGoesSxrLeafs(Date startTimestamp, Date currentDateMidnight);

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