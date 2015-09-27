import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.Instant;

import ch.fhnw.i4ds.timelineviz.domain.GoesSxrLeaf;
import ch.fhnw.i4ds.timelineviz.importer.downloader.GoesNewFullDownloader;


public class Test {



  public static void main(String[] args) {

    GoesNewFullDownloader loader = new GoesNewFullDownloader(1, 20);
    Set<GoesSxrLeaf> asd = loader.getGoesSxrLeafs(new Instant().minus(10), new DateMidnight());
    System.out.println(asd);


  }

}
