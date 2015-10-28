package timelines.gui.variant3;

import timelines.gui.variant2.TimeLinesViewer;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class ZoomListener extends MouseInputAdapter {
  private int zoomLevel = 1;
  private int minLevel = 1;
  private int maxLevel;

  public ZoomListener(){
    maxLevel = 10;
  }

  public ZoomListener(int maxLevel){
    this.maxLevel = maxLevel;
  }

  public void mouseWheelMoved(MouseWheelEvent mwe){
    int change = mwe.getWheelRotation();
    System.out.println(change);
    if (!((zoomLevel-change)<minLevel||(zoomLevel-change)>maxLevel)) {
      zoomLevel -= mwe.getWheelRotation();
    }
    System.out.println(zoomLevel);
    Image img = (Image)mwe.getSource();
    img.zoom(zoomLevel);
  }

}
