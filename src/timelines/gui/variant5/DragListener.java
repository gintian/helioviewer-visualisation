package timelines.gui.variant5;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class DragListener extends MouseInputAdapter {
  private int xBase;

  public void mousePressed(MouseEvent me)
  {
    this.xBase = me.getX();
  }

  public void mouseDragged(MouseEvent me)
  {
    Image img = (Image)me.getSource();
    img.dragged(me.getX() - this.xBase);
    this.xBase = me.getX();
  }
}
