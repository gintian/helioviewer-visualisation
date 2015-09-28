import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.Instant;

import ch.fhnw.i4ds.timelineviz.domain.GoesSxrLeaf;
import ch.fhnw.i4ds.timelineviz.importer.downloader.GoesNewFullDownloader;


public class Test {



  public static void main(String[] args) {


    // found working URL: http://satdat.ngdc.noaa.gov/sem/goes/data/new_full/2015/09/goes13/csv/g13_xrs_2s_20150927_20150927.csv


    GoesNewFullDownloader loader = new GoesNewFullDownloader(1, 20);
    Set<GoesSxrLeaf> asd = loader.getGoesSxrLeafs(new Instant().minus(100000000), new DateMidnight().minus(100000));
    System.out.println(asd);


  }

}
