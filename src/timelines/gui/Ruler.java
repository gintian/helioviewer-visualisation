package timelines.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class Ruler extends JPanel {

  private Orientation o;

  public Ruler(Orientation orientation){
    this.o = orientation;
    //setBackground(Color.white);
    if(o == Orientation.VERTICAL) {
      setPreferredSize(new Dimension(20, 0));
    } else{
      setPreferredSize(new Dimension(0, 20));
    }
  }

  @Override
  protected void paintComponent(Graphics g){
    super.paintComponent(g);
    g.setColor(Color.black);
    if(o == Orientation.HORIZONTAL) {
      g.drawLine(0, 10, this.getWidth(), 10);
    } else {
      g.drawLine(10, 0, 10, this.getHeight());
    }
  }
}
