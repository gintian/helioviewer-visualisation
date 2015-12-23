package timelines.performanceTest;

import java.util.Date;

import timelines.database.TimelinesDB;
import timelines.renderer.CacheRenderer;
import timelines.renderer.DiagramRenderer;

public class RenderTest {

  private static DiagramRenderer renderer;

  public static void main(String[] args) throws Exception {

    renderer = new DiagramRenderer();

    //
    // Run these tests individually (comment all but one)
    // to ensure that they all start with no data mapped into the RAM yet
    //

    System.out.println("zoom level 1: " + render(CacheRenderer.getTimePerImage(1)) + "ms");
    System.out.println("zoom level 1: " + render(CacheRenderer.getTimePerImage(1)) + "ms");

//    System.out.println("zoom level 2: " + render(CacheRenderer.getTimePerImage(2)) + "ms");
//    System.out.println("zoom level 2: " + render(CacheRenderer.getTimePerImage(2)) + "ms");
//
//    System.out.println("zoom level 3: " + render(CacheRenderer.getTimePerImage(3)) + "ms");
//    System.out.println("zoom level 3: " + render(CacheRenderer.getTimePerImage(3)) + "ms");
//
//    System.out.println("zoom level 4: " + render(CacheRenderer.getTimePerImage(4)) + "ms");
//    System.out.println("zoom level 4: " + render(CacheRenderer.getTimePerImage(4)) + "ms");
//
//    System.out.println("zoom level 5: " + render(CacheRenderer.getTimePerImage(5)) + "ms");
//    System.out.println("zoom level 5: " + render(CacheRenderer.getTimePerImage(5)) + "ms");
//
//    System.out.println("zoom level 6: " + render(CacheRenderer.getTimePerImage(6)) + "ms");
//    System.out.println("zoom level 6: " + render(CacheRenderer.getTimePerImage(6)) + "ms");
//
//    System.out.println("zoom level 7: " + render(CacheRenderer.getTimePerImage(7)) + "ms");
//    System.out.println("zoom level 7: " + render(CacheRenderer.getTimePerImage(7)) + "ms");
//
//    System.out.println("zoom level 8: " + render(CacheRenderer.getTimePerImage(8)) + "ms");
//    System.out.println("zoom level 8: " + render(CacheRenderer.getTimePerImage(8)) + "ms");
//
//    System.out.println("zoom level 9: " + render(CacheRenderer.getTimePerImage(9)) + "ms");
//    System.out.println("zoom level 9: " + render(CacheRenderer.getTimePerImage(9)) + "ms");
//
//    System.out.println("zoom level 10: " + render(CacheRenderer.getTimePerImage(10)) + "ms");
//    System.out.println("zoom level 10: " + render(CacheRenderer.getTimePerImage(10)) + "ms");
//
//    System.out.println("zoom level 11: " + render(CacheRenderer.getTimePerImage(11)) + "ms");
//    System.out.println("zoom level 11: " + render(CacheRenderer.getTimePerImage(11)) + "ms");
//
//    System.out.println("zoom level 12: " + render(CacheRenderer.getTimePerImage(12)) + "ms");
//    System.out.println("zoom level 12: " + render(CacheRenderer.getTimePerImage(12)) + "ms");
//
//    System.out.println("zoom level 13: " + render(CacheRenderer.getTimePerImage(13)) + "ms");
//    System.out.println("zoom level 13: " + render(CacheRenderer.getTimePerImage(13)) + "ms");
//
//    System.out.println("zoom level 14: " + render(CacheRenderer.getTimePerImage(14)) + "ms");
//    System.out.println("zoom level 14: " + render(CacheRenderer.getTimePerImage(14)) + "ms");
//
//    System.out.println("zoom level 15: " + render(CacheRenderer.getTimePerImage(15)) + "ms");
//    System.out.println("zoom level 15: " + render(CacheRenderer.getTimePerImage(15)) + "ms");
//
//    System.out.println("zoom level 16: " + render(CacheRenderer.getTimePerImage(16)) + "ms");
//    System.out.println("zoom level 16: " + render(CacheRenderer.getTimePerImage(16)) + "ms");
//
//    System.out.println("zoom level 17: " + render(CacheRenderer.getTimePerImage(17)) + "ms");
//    System.out.println("zoom level 17: " + render(CacheRenderer.getTimePerImage(17)) + "ms");
//
//    System.out.println("zoom level 18: " + render(CacheRenderer.getTimePerImage(18)) + "ms");
//    System.out.println("zoom level 18: " + render(CacheRenderer.getTimePerImage(18)) + "ms");

  }

  private static long render(long timespan) throws Exception {

    Date start = new Date();
    renderer.getDiagramForTimespan(TimelinesDB.DB_START_DATE, new Date(TimelinesDB.DB_START_DATE.getTime() + timespan));
    return new Date().getTime() - start.getTime();
  }

}
