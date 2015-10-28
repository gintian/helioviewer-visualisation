package timelines.gui.snippets;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Testimage extends JFrame{

  // *** your image path will be different *****
  private static final String IMG_PATH = "build\\classes\\timelines\\gui\\ajrKzgp_460s.jpg";
  public Testimage(){
    super("title here");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 200);
    setLocation(100, 100);
    JLabel label = null;
    try {
      BufferedImage img = ImageIO.read(new File(IMG_PATH));
      ImageIcon icon = new ImageIcon(img);
      label = new JLabel(icon);
    } catch (IOException e) {
      e.printStackTrace();
    }
    add(label);
    //pack();
    setVisible(true);
  }

  public static void main(String[] args) {
    JFrame frame = new Testimage();
  }
}