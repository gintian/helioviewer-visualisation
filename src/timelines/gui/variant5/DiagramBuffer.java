package timelines.gui.variant5;

import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Tobi on 05.12.2015.
 */
public class DiagramBuffer {
    private Image image;
    private ImageLoader imageLoader;
    private TreeMap<DiagramKey, Diagram> treeMap = new TreeMap<DiagramKey, Diagram>();
    private int tileCount;
    private int currentCount = 0;

    public DiagramBuffer(Image image){
      this.image = image;
      this.imageLoader = new ImageLoader(image, this);
    }

//    public DiagramBuffer(ImageLoader imageLoader, int tileCount){
//        this.imageLoader = imageLoader;
//        this.tileCount = tileCount;
//    }

    public synchronized void addToDiagramBuffer(Diagram diagram) throws IOException{
        System.out.println(diagram.getStartDate());
        if(diagram.getStartDate() == null) {
          throw new IOException("no metadata found");
        }
//        Long date = diagram.getStartDate().getTime();
        DiagramKey key = new DiagramKey(diagram.getStartDate(), diagram.getZoomLevel());
        System.out.println("adding the diagram. key: " + key);
        treeMap.put(key, diagram);
        System.out.println("diagram map size: " + treeMap.size());

        this.currentCount++;
        image.update();
        //reportIfFull();
    }


    public synchronized Diagram getDiagram(Date date, int zoomLevel) {

      Diagram d = treeMap.get(new DiagramKey(date, zoomLevel));
      System.out.println("trying to get " + new DiagramKey(date, zoomLevel));
      if(d == null) {
        System.out.println("starting load");
        imageLoader.loadImage(date, zoomLevel);
      }
      return d;

    }

//    private void reportIfFull(){
//        if (this.currentCount == this.tileCount){
//            this.imageLoader.processDiagramBuffer();
//        }
//    }

//    public TreeMap<Long, Diagram> getMap(){
//        return this.treeMap;
//    }

//    public BufferedImage[] getSortedBufferedImageArray(){
//        BufferedImage[] buffImgArr = new BufferedImage[this.tileCount];
//        int i = 0;
//        for(Long key : this.treeMap.keySet())
//        {
//            buffImgArr[i] = this.treeMap.get(key).getBufferedImage();
//            i++;
//        }
//        return buffImgArr;
//    }

    public APIInfo getAPIInfo() {
      return imageLoader.getApiInfo();
    }

    private class DiagramKey implements Comparable<DiagramKey> {

      private Date from;
      private int zoomLevel;

      public DiagramKey(Date from, int zoomLevel) {
        this.from = from;
        this.zoomLevel = zoomLevel;
      }

      @Override
      public boolean equals(Object other) {

        return zoomLevel == ((DiagramKey)other).zoomLevel
            && from.equals(((DiagramKey)other).from);
      }

      @Override
      public String toString() {
        return "zoom: " + zoomLevel + ", date: " + from;
      }

      @Override
      public int compareTo(DiagramKey o) {

        // TODO make more meaningful return values
        if(this.equals(o)) {
          return 0;
        }

        if (this.zoomLevel < o.zoomLevel || this.from.before(o.from)) {
          return -1;
        } else {
          return 1;
        }
      }
    }



}
