package timelines.gui;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
  public Window(){
    super("i4ds05 - Time Lines Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 200);
    setLocation(100, 100);

    JPanel hRulerPanel = new Ruler(Orientation.HORIZONTAL);
    JPanel vRulerPanel = new Ruler(Orientation.VERTICAL);
    Image imagePanel = new Image();

    ImageDragListener drag = new ImageDragListener();
    imagePanel.addMouseListener( drag );
    imagePanel.addMouseMotionListener( drag );

    add(hRulerPanel, BorderLayout.PAGE_END);
    add(vRulerPanel, BorderLayout.LINE_START);
    add(imagePanel, BorderLayout.CENTER);
    //pack();
    setVisible(true);
  }
  //public static void main(String[] args){
  //  Window frame = new Window();
  //}
}
