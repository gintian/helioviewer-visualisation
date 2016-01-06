package timelines.gui.variant5;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseWheelEvent;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class ZoomListener extends MouseInputAdapter {

  public void mouseWheelMoved(MouseWheelEvent mwe){
    Image img = (Image)mwe.getSource();
    int change = mwe.getWheelRotation();
    img.zoom(change, mwe.getX());
  }

}
