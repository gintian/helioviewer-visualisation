package timelines.importer.downloader;

import timelines.importer.csv.GoesSxrLeaf;
import timelines.utils.StringUtils;
import timelines.utils.TimeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import nom.tam.fits.*;
import nom.tam.util.ArrayDataInput;

public class FitsDownloader implements IDownloader {

    private static final Logger logger = Logger.getLogger(FitsDownloader.class.getName());

    private String urlTemplate = "ftp://umbra.nascom.nasa.gov/goes/fits/{yyyy}/go{goesnr}{yy}{MM}{dd}.fits";

    public static final Date START_DATE = new Date(1104537600000L); // 1980-04-01 00:00:00
    public static final Date END_DATE = new Date(); // today

    private final int MIN_GOESNR = 2;
    private final int MAX_GOESNR = 16;

    public FitsDownloader() {

    }

    @Override
    public Date getStartDateMidnight() {
        return START_DATE;
    }

    @Override
    public Date getEndDateMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(END_DATE);

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    protected URL createUrl(final Date currentDate, final Integer goesNr) throws MalformedURLException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("goesnr", String.format("%02d", goesNr));
        params.put("yyyy", Integer.toString(TimeUtils.getYear(currentDate)));
        params.put("yy", currentDate.getYear() + 1900 < 2000 ? TimeUtils.toString(currentDate, "yy")
                : TimeUtils.toString(currentDate, "yyyy"));
        params.put("MM", TimeUtils.toString(currentDate, "MM"));
        params.put("dd", TimeUtils.toString(currentDate, "dd"));

        final String urlString = StringUtils.format(urlTemplate, params);

        return new URL(urlString);
    }

    @Override
    public boolean isSameDownloadSite(Date thisDateMidnight, Date otherDateMidnight) {
        return TimeUtils.getYear(thisDateMidnight) == TimeUtils.getYear(otherDateMidnight)
                && TimeUtils.getMonth(thisDateMidnight) == TimeUtils.getMonth(otherDateMidnight);
    }

    public List<GoesSxrLeaf> getGoesSxrLeafs(Date minTimestamp, Date csvFileDate) {
        logger.log(Level.INFO, "getting all data from {0} which is dated after {1}",
                new Object[] { csvFileDate, minTimestamp });

        int goesNr = MAX_GOESNR;

        while (goesNr >= MIN_GOESNR) {
            try {
                URL url = createUrl(csvFileDate, goesNr);
                List<GoesSxrLeaf> leafs = downloadFitsFile(url, csvFileDate);

                logger.log(Level.INFO, String.format("Downloaded fits file %s", url.toString()));
                return leafs;
            } catch (Exception e) {
                if (goesNr == MIN_GOESNR) {
                    logger.log(Level.WARNING, String.format("Could not download data for %s",
                            DateFormat.getDateInstance().format(csvFileDate)));
                    logger.log(Level.WARNING, e.getMessage());
                }
            } finally {
                --goesNr;
            }
        }

        return null;
    }

    protected List<GoesSxrLeaf> downloadFitsFile(URL url, Date minTime) throws FitsException, IOException {
        Fits f = new Fits(url);

        BinaryTableHDU hdu = (BinaryTableHDU) f.getHDU(2);
        Object[] cols = hdu.getColumns();

        double[] times = ((double[][]) cols[0])[0];
        float[][] vals = ((float[][][]) cols[1])[0];

        List<GoesSxrLeaf> leafs = new ArrayList<>();

        int i = 0;
        for (double time : times) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(minTime);
            cal.add(Calendar.SECOND, (int) time);
            GoesSxrLeaf leaf = new GoesSxrLeaf(cal.getTime(), vals[i][0], vals[i][1]);

            leafs.add(leaf);
            ++i;
        }

        return leafs;
    }

    @Override
    public void resetGoesNr() {

    }
}
