package timelines.importer.downloader;

import timelines.importer.csv.GoesSxrLeaf;
import timelines.utils.StringUtils;
import timelines.utils.TimeUtils;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nom.tam.fits.*;

public class FitsDownloader /*implements IDownloader*/ {
    private String urlTemplate = "ftp://umbra.nascom.nasa.gov/goes/fits/{yyyy}/go{goesnr}{yy}{MM}{dd}.fits";


    public FitsDownloader() {

    }

    protected URL createUrl(final Date currentDate, final Integer goesNr) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("goesnr", String.format("%02d", goesNr));
        params.put("yyyy", Integer.toString(TimeUtils.getYear(currentDate)));
        params.put("yy", currentDate.getYear() < 2000 ? TimeUtils.toString(currentDate, "yy") : TimeUtils.toString(currentDate, "yyyy"));
        params.put("MM", TimeUtils.toString(currentDate, "MM"));
        params.put("dd", TimeUtils.toString(currentDate, "dd"));

        final String urlString = StringUtils.format(urlTemplate, params);

        return new URL(urlString);
    }
}