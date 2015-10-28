package timelines.gui.variant1;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class ImageDragListener extends MouseInputAdapter
{
  //Point location;
  MouseEvent pressed;

  public void mousePressed(MouseEvent me)
  {
    pressed = me;
  }

  public void mouseDragged(MouseEvent me)
  {
    timelines.gui.variant1.Image image = (Image)me.getComponent();
    //location = image.getLocation(location);
    int x = image.getX() - pressed.getX() + me.getX();
    int y = image.getY() - pressed.getY() + me.getY();
    image.setX(x);
    image.setY(y);
    image.repaint();
  }
}