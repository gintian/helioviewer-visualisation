package timelines.gui.variant4;

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
  private Coordinates origin;
  private Coordinates focusPoint;
  private Coordinates originToFocus; //difference betwene focusPoint and originPoint
  private JFrame window;
  private int zoomLevel = 1;
  private int rulerWidth = 20;
  private long timeScalMin;
  private double IntensScaleMax;

  public Image(BufferedImage image){
    this.image = image;
    setWidths(image.getWidth());
    setHeights(image.getHeight());
    this.origin = new Coordinates(0,0);
    setFocusPointCenter();
  }

  public Image(BufferedImage image, JFrame jF){
    this.window = jF;
    this.image = image;
    setWidths(image.getWidth());
    setHeights(image.getHeight());

    centerImage();
    setFocusPointCenter();
  }

  public Coordinates getWindowCenter(){
    return new Coordinates(
        (this.window.getContentPane().getWidth()+this.rulerWidth)/2,
        (this.window.getContentPane().getHeight()-this.rulerWidth)/2);
  }

  private void centerImage(){
    int x = getWindowCenter().x-(this.width/2);
    int y = getWindowCenter().y-(this.height/2);
    this.origin = new Coordinates(x,y);
    setFocusPointCenter();
  }

  private void setFocusPointCenter(){
    setFocusPoint(new Coordinates(getWindowCenter().x,getWindowCenter().y));
  }

  public void setFocusPoint(Coordinates p){
    this.focusPoint = p;
    originToFocus = focusPoint.diff(origin);
  }

  private void focusImage(){
    this.origin = getWindowCenter().diff(this.originToFocus);
  }

  private void adjustFocus(int level){
    this.originToFocus.multiply((double)level/(double)zoomLevel);
  }

  public void setHeights(int height) {
    this.height = height;
    this.originalHeight = height;
  }

  public void setWidths(int width) {
    this.width = width;
    this.originalWidth = width;
  }

  public void zoom(int level, Coordinates focusPoint){
    setFocusPoint(focusPoint);
    this.width = this.originalWidth * level;
    this.height = this.originalHeight * level;
    adjustFocus(level);
    this.zoomLevel = level;
    repaint();
  }

  public void moveBy(Coordinates change){
    origin.add(change);
    setFocusPointCenter();
    repaint();
  }

  private void paintRulers(Graphics g){
    int h = this.window.getContentPane().getHeight();
    int w = this.window.getContentPane().getWidth();
    int xOffset = 0;
    int yOffset = 0;
    int rw = this.rulerWidth;
    int sp = rw/4;
    Color rulerColor = g.getColor();
    Color scaleColor = new Color(255,255,255);

    g.fillRect(0,0,rw,h-rw);
    g.setColor(scaleColor);
    int i = h;
    while(i+yOffset>0){
      g.drawLine(sp,i+yOffset,rw-sp,i+yOffset);
      i-=(10*zoomLevel);
    }

    g.setColor(rulerColor);
    g.fillRect(rw,h-rw,w-rw,rw);
    g.setColor(scaleColor);
    int j = w;
    while(j+xOffset>0){
      g.drawLine(rw+j+xOffset,h-sp,rw+j+xOffset,h-(rw-sp));
      j-=(10*zoomLevel);
    }
    g.setColor(rulerColor);
    g.fillRect(0, h - rw, rw, rw);
  }

  @Override
  public void paint(Graphics g){
    super.paintComponent(g);
    focusImage();
    g.drawImage(image, origin.x, origin.y, width, height, null);
    paintRulers(g);
  }
}
