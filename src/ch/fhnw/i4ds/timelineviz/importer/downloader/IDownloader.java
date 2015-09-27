package ch.fhnw.i4ds.timelineviz.importer.downloader;

import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.Instant;

import ch.fhnw.i4ds.timelineviz.domain.GoesSxrLeaf;

public interface IDownloader {

	/**
	 * Download and parse GoesSxrLeafs.
	 * @param startTimestamp only return leafs which timestamp is after startTimestamp
	 * @param currentDateMidnight download leafs at this date.
	 * @return downloader GoesSxrLeafs.
	 */
	public Set<GoesSxrLeaf> getGoesSxrLeafs(Instant startTimestamp, DateMidnight currentDateMidnight);
	
	/**
	 * Compares the two DateMidnight and checks if it would use another download url.
	 * @param thisDateMidnight
	 * @param otherDateMidnight
	 * @return uses same download url
	 */
	public boolean isSameDownloadSite(DateMidnight thisDateMidnight, DateMidnight otherDateMidnight);
	
	/**
	 * Get start of the data.
	 * @return start of data
	 */
	public DateMidnight getStartDateMidnight();
	
	/**
	 * Get end of the data.
	 * @return end of data
	 */
	public DateMidnight getEndDateMidnight();

}