package timelines.gui.variant1;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */

import javax.swing.*;

public class Window extends JFrame {
  public Window(){
    super("i4ds05 - Time Lines Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 200);
    setLocation(100, 100);

    Image imagePanel = new Image();

    //ZoomListener zoom = new ZoomListener();
    ImageDragListener drag = new ImageDragListener();

    //imagePanel.addMouseWheelListener(zoom);

    imagePanel.addMouseListener( drag );
    imagePanel.addMouseMotionListener( drag );

    add(imagePanel);
    //pack();
    setVisible(true);
  }
  //public static void main(String[] args){
  //  Window frame = new Window();
  //}
}
