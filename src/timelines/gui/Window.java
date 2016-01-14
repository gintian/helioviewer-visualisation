package timelines.gui;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */


import java.util.Date;

import javax.swing.JFrame;

public class Window extends JFrame {
  private Image image;

  public Window(Date date){
    super("i4ds05 - Time Lines Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 370);
    setLocationRelativeTo(null);
    setVisible(true);

    image = new Image(this, date);
    add(image);

    ZoomListener zoom = new ZoomListener();
    image.addMouseWheelListener(zoom);
    DragListener drag = new DragListener();
    image.addMouseMotionListener(drag);
    image.addMouseListener(drag);
    revalidate();
    repaint();
  }

  }
