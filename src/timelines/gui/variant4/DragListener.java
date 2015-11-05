package timelines.gui.variant4;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class DragListener extends MouseInputAdapter {
  private Coordinates base;

  public void mousePressed(MouseEvent me)
  {
    this.base = new Coordinates(me.getX(), me.getY());
  }

  public void mouseDragged(MouseEvent me)
  {
    Image img = (Image)me.getSource();
    img.moveBy(new Coordinates(me.getX() - this.base.x,me.getY() - this.base.y));
    this.base = new Coordinates(me.getX(),me.getY());
  }
}
