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
import java.util.Observable;
import java.util.Observer;

public class Window extends JFrame implements Observer {
  Image image;

  @Override
  public void update(Observable o, Object arg) {
    image.setImage((BufferedImage)arg);
  }

  public Window(ImageLoader il){
    super("i4ds05 - Time Lines Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 500);
    setLocationRelativeTo(null);
    setVisible(true);

    try {
      BufferedImage bImage = ImageIO.read(new File("src\\timelines\\gui\\ajrKzgp_460s.jpg"));
      image = new Image(bImage, this, il);
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
