import java.util.Calendar;
import java.util.Set;

import timelines.importer.csv.GoesSxrLeaf;
import timelines.importer.downloader.GoesNewFullDownloader;


public class Test {



  public static void main(String[] args) {


    // found working URL: http://satdat.ngdc.noaa.gov/sem/goes/data/new_full/2015/09/goes13/csv/g13_xrs_2s_20150927_20150927.csv


    GoesNewFullDownloader loader = new GoesNewFullDownloader(1, 20);

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
    cal.set(Calendar.DAY_OF_MONTH, 27);

    Set<GoesSxrLeaf> asd = loader.getGoesSxrLeafs(cal.getTime(), cal.getTime());
    System.out.println(asd);


  }

}
