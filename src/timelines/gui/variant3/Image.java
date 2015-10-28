package timelines.gui.variant3;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class Image extends JComponent {

  private BufferedImage image;
  private int originalWidth;
  private int originalHeight;
  private int width;
  private int height;
  private int xOrigin;
  private int yOrigin;
  private JFrame window;
  private Point focus;

  public Image(BufferedImage image){
    this.image = image;
    setWidths(image.getWidth());
    setHeights(image.getHeight());
    this.xOrigin = 0;
    this.yOrigin = 0;
  }

  public Image(BufferedImage image, JFrame jF){
    this.window = jF;
    this.image = image;
    setWidths(image.getWidth());
    setHeights(image.getHeight());
    System.out.println("img:"+jF.getContentPane().getWidth()+":"+jF.getContentPane().getHeight());

    centerImage();
  }

  private void centerImage(){
    int x = (window.getContentPane().getWidth()/2)-(width/2);
    int y = (window.getContentPane().getHeight()/2)-(height/2);
    this.xOrigin = x;
    this.yOrigin = y;
  }

  private void setFocus(){
    int x = (window.getContentPane().getWidth()/2)-xOrigin;
    int y = (window.getContentPane().getHeight()/2)-yOrigin;
    this.focus = new Point(x,y);
  }

  public void setHeights(int height) {
    this.height = height;
    this.originalHeight = height;
  }

  public void setWidths(int width) {
    this.width = width;
    this.originalWidth = width;
  }

  public void zoom(int level){
    this.width = this.originalWidth * level;
    this.height = this.originalHeight * level;
    //adjustLocation(level);
    repaint();
  }

  private void adjustLocation(int level){
    this.xOrigin *= level;
    this.yOrigin *= level;
  }

  public void moveBy(int xChange, int yChange){
    this.xOrigin += xChange;
    this.yOrigin += yChange;
    repaint();
  }

  @Override
  public void paint(Graphics g){
    super.paintComponent(g);
    g.drawImage(image, xOrigin, yOrigin, width , height , null);
  }
}
