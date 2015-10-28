package timelines.gui;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class ZoomListener extends MouseInputAdapter {

  MouseEvent scrolled;

  public void mouseWheelMoved(MouseWheelEvent mwe){
    System.out.println(mwe.getWheelRotation());
  }

}
