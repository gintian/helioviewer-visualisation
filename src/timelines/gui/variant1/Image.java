package timelines.gui.variant1;

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
public class Image extends JPanel {

  private BufferedImage image;
  private int x = 0, y = 0;

  @Override
  public int getX(){ return this.x; }
  @Override
  public int getY(){ return this.y; }

  public void setX(int x){ this.x = x;}
  public void setY(int y){ this.y = y;}

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }

  public Image(){
    try {
      BufferedImage image = ImageIO.read(new File("src/timelines/gui/ajrKzgp_460s.jpg"));
    } catch (IOException ex) {
      System.out.println("error");
      // handle exception...
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(image, this.x, this.y, null); // see javadoc for more info on the parameters
    System.out.println("painted");
    System.out.println("x : "+ getX());
    System.out.println("y : "+ getY());
  }
}
