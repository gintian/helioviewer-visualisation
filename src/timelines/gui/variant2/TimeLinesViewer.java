package timelines.gui.variant2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class TimeLinesViewer {
  private static BufferedImage image;
  public static BufferedImage getImage(){
    return image;
  }

  public static void setImage(BufferedImage image) {
    TimeLinesViewer.image = image;
  }

  public static void main(String[] args) throws IOException {
    JFrame frame = buildFrame();

    setImage(ImageIO.read(new File("src/timelines/gui/ajrKzgp_460s.jpg")));

    JPanel pane = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
      }
    };
    frame.add(pane);
    ZoomListener zoom = new ZoomListener();
    pane.addMouseWheelListener(zoom);
    frame.setVisible(true);
  }


  private static JFrame buildFrame() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(200, 200);
    return frame;
  }

  public static void zoomChanged(int level){
    int newImageWidth = image.getWidth() * level;
    int newImageHeight = image.getHeight() * level;
    BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = resizedImage.createGraphics();
    g.drawImage(image, 0, 0, newImageWidth , newImageHeight , null);
    g.dispose();
  }


}
