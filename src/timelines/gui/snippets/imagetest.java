package timelines.gui.snippets;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class imagetest extends JPanel{

  private BufferedImage image;

  public imagetest() {
    try {
      image = ImageIO.read(new File("C:\\Users\\Admin\\Pictures\\ajrKzgp_460s.jpg"));
    } catch (IOException ex) {
      System.out.println("error");
      // handle exception...
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
  }

}