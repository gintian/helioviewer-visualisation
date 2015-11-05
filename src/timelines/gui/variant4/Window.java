package timelines.gui.variant4;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Window extends JFrame {
  Image image;

  public Window(){
    super("i4ds05 - Time Lines Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 500);
    setLocationRelativeTo(null);
    setVisible(true);

    try {
      BufferedImage bImage = ImageIO.read(new File("src\\timelines\\gui\\ajrKzgp_460s.jpg"));
      image = new Image(bImage, this);
      add(image);

      ZoomListener zoom = new ZoomListener();
      image.addMouseWheelListener(zoom);
      DragListener drag = new DragListener();
      image.addMouseMotionListener(drag);
      image.addMouseListener(drag);
      revalidate();
      repaint();
    }catch (IOException e){
      //do something
    }
  }

  }
