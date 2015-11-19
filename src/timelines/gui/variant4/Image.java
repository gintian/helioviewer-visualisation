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
  private int xOrigin;
  private int xFocus;
  private int originToFocus; //difference betwene xFocus and xOrigin
  private JFrame window;
  private int zoomLevel = 1;
  private int rulerWidth = 20;
  private long timeScalMin;
  private double IntensScaleMax;

  public Image(BufferedImage image){
    this.image = image;
    setWidths(image.getWidth());
    this.originalHeight = image.getHeight();
    this.xOrigin = 0;
    setFocusPointCenter();
  }

  public Image(BufferedImage image, JFrame jF){
    this.window = jF;
    this.image = image;
    setWidths(image.getWidth());
    this.originalHeight = image.getHeight();

    centerImage();
    setFocusPointCenter();
  }

  public int getWindowCenter(){
    return (this.window.getContentPane().getWidth()+this.rulerWidth)/2;
  }

  private void centerImage(){
    this.xOrigin = getWindowCenter()-(this.width/2);
    setFocusPointCenter();
  }

  private void setFocusPointCenter(){
    setFocusPoint(getWindowCenter());
  }

  public void setFocusPoint(int x){
    this.xFocus = x;
    originToFocus = xFocus-xOrigin;
  }

  private void focusImage(){
    this.xOrigin = getWindowCenter()-this.originToFocus;
  }

  private void adjustFocus(int level){
    this.originToFocus *= ((double)level/(double)zoomLevel);
  }

  public void setWidths(int width) {
    this.width = width;
    this.originalWidth = width;
  }

  public void getNewImage(int zoomLevel, int xFocus){

  }
  public void strechImage(int zoomLevel){
    this.width = this.originalWidth * zoomLevel;
    //this.height = this.originalHeight * level;
    adjustFocus(zoomLevel);
    this.zoomLevel = zoomLevel;
    repaint();
  }
  public void zoom(int level, int xFocus){
    setFocusPoint(xFocus);
    strechImage(level);
    getNewImage(level, xFocus);
  }

  public void moveBy(int change){
    xOrigin += change;
    setFocusPointCenter();
    repaint();
  }

  private void paintRulers(Graphics g){
    int h = this.window.getContentPane().getHeight();
    int w = this.window.getContentPane().getWidth();
    int rw = this.rulerWidth;
    int sp = rw/4;
    Color rulerColor = g.getColor();
    Color scaleColor = new Color(255,255,255);

    g.fillRect(0,0,rw,h-rw);
    g.setColor(scaleColor);
    int i = h-rw-1;
    while(i>=0){
      g.drawLine(sp,i,rw-sp,i);
      i-=(10);
    }

    g.setColor(rulerColor);
    g.fillRect(rw,h-rw,w-rw,rw);
    g.setColor(scaleColor);
    int j = xOrigin;
    while(j<=width+xOrigin){
      g.drawLine(j,h-sp,j,h-(rw-sp));
      j+=(10*zoomLevel);
    }
    System.out.println(width);
    g.setColor(rulerColor);
    g.fillRect(0, h - rw, rw, rw);
  }

  @Override
  public void paint(Graphics g){
    super.paintComponent(g);
    focusImage();
    g.drawImage(image, xOrigin, 0, width, originalHeight, null);
    System.out.println(image.getType());
    paintRulers(g);
  }
}
