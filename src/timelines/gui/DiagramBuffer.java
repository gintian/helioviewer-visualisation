package timelines.gui;

import java.io.IOException;
import java.util.*;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 05.12.2015.
 */
public class DiagramBuffer {
    private ImageLoader imageLoader;
    private TreeMap<Long, Diagram> treeMap = new TreeMap<Long, Diagram>();
    private int tileCount;
    private int currentCount = 0;

    public DiagramBuffer(ImageLoader imageLoader, int tileCount){
        this.imageLoader = imageLoader;
        this.tileCount = tileCount;
    }

    public synchronized void addToDiagramBuffer(Diagram diagram) throws IOException{
        Long date = diagram.getStartDate().getTime();
        treeMap.put(date, diagram);

        this.currentCount++;
        reportIfFull();
    }

    public synchronized void addEmpty(){
        this.currentCount++;
        reportIfFull();
    }

    private void reportIfFull(){
        if (this.currentCount == this.tileCount){
            this.imageLoader.processDiagramBuffer();
        }
    }

    public TreeMap<Long, Diagram> getMap(){
        return this.treeMap;
    }

}
