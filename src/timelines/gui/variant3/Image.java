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
    //setFocus();
  }

  public Image(BufferedImage image, JFrame jF){
    this.window = jF;
    this.image = image;
    setWidths(image.getWidth());
    setHeights(image.getHeight());
    System.out.println("img:"+jF.getContentPane().getWidth()+":"+jF.getContentPane().getHeight());

    centerImage();
    //setFocus();
  }

  private void centerImage(){
    int x = (this.window.getContentPane().getWidth()/2)-(this.width/2);
    int y = (this.window.getContentPane().getHeight()/2)-(this.height/2);
    this.xOrigin = x;
    this.yOrigin = y;
  }

  private void setFocus(){
    int x = (this.window.getContentPane().getWidth()/2)-this.xOrigin;
    int y = (this.window.getContentPane().getHeight()/2)-this.yOrigin;
    this.focus = new Point(x,y);
  }

  private void focusImage(){
    this.xOrigin = (this.window.getContentPane().getWidth()/2)-this.focus.x;
    this.yOrigin = (this.window.getContentPane().getHeight()/2)-this.focus.y;
  }

  private void adjustFocus(int zoomLevel){
    this.focus.x *= zoomLevel;
    this.focus.y *= zoomLevel;
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
    //adjustFocus(level);
    repaint();
  }

  public void moveBy(int xChange, int yChange){
    this.xOrigin += xChange;
    this.yOrigin += yChange;
    //setFocus();
    repaint();
  }

  private void paintRulers(Graphics g){
    int h = this.window.getContentPane().getHeight();
    int w = this.window.getContentPane().getWidth();
    Color rulerColor = g.getColor();
    Color scaleColor = new Color(255,255,255);

    g.fillRect(0,0,20,h);
    g.setColor(scaleColor);
    int i = 0;
    while(i<h){
      g.drawLine(5,i*10,15,i*10);
      i++;
    }

    g.setColor(rulerColor);
    g.fillRect(0,h-20,w,20);
    g.setColor(scaleColor);
    int j = 0;
    while(j<w){
      g.drawLine(20+j*10,h-5,20+j*10,h-15);
      j++;
    }
    g.setColor(rulerColor);
  }

  @Override
  public void paint(Graphics g){
    super.paintComponent(g);
    //focusImage();
    g.drawImage(image, xOrigin, yOrigin, width , height , null);
    paintRulers(g);
  }
}
