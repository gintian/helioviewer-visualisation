package timelines.gui.variant3;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class DragListener extends MouseInputAdapter {
  private int baseX;
  private int baseY;

  public void mousePressed(MouseEvent me)
  {
    this.baseX = me.getX();
    this.baseY = me.getY();
  }

  public void mouseDragged(MouseEvent me)
  {
    Image img = (Image)me.getSource();
    int xChange = me.getX() - this.baseX;
    int yChange = me.getY() - this.baseY;
    this.baseX = me.getX();
    this.baseY = me.getY();
    img.moveBy(xChange,yChange);
  }
}
