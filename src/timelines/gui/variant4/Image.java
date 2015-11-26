package timelines.gui.variant4;

import timelines.utils.TimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class Image extends JComponent {

  private static final String serverBaseURLStr = "http://localhost:8080";

  private BufferedImage image;
  private int originalWidth;
  private int originalHeight;
  private int width;
  private int pixelOrigin;
  private int pixelFocus;
  private int originToFocusPixel; //difference between pixelFocus and pixelOrigin
  private long originToFocusDate; //difference between dateFocus and dateOrigin
  private Window window;
  private int zoomLevel = 1;
  private int rulerWidth = 20;
  private long timeScalMin;
  private double IntensScaleMax;
  private ImageLoader imageLoader;
  private Date dateFocus; //TODO: calculate date and origin coordinates
  private Date dateOrigin; //TODO: calculate date and origin coordinates



  public Image(Window window, Date date){
    this.window = window;
    this.dateFocus = date;
    this.dateOrigin = date;
    this.imageLoader = new ImageLoader(this, serverBaseURLStr, date);

    leftAlignImage();
    focusCenter();
  }



  public void setWidths(int width) {
    this.width = width;
    this.originalWidth = width;
  }

  private void setImage(BufferedImage img){
    this.image = img;
    setWidths(image.getWidth());
    this.originalHeight = image.getHeight();
    repaint();
  }

  public void updateImage(BufferedImage img){
    this.image = img;
    setWidths(image.getWidth());
    this.originalHeight = image.getHeight();
    repaint();
  }



  private int timeToPixel(long time, int zoomLevel){
    return (int)(time/Math.pow(2, zoomLevel));
  }
  private long pixelToTime(int pixel, int zoomLevel){
    return (long)Math.pow(2, zoomLevel)*pixel;
  }



  public Window getWindow() {
    return window;
  }

  public int getWindowCenter(){
    return (this.window.getContentPane().getWidth()+this.rulerWidth)/2;
  }



  private void focusCenter(){
    setFocus(getWindowCenter());
  }

  private void leftAlignImage(){
    this.pixelOrigin = 0;
    focusCenter();
  }



  private void setFocus(int x){
    setFocusPixel(x);
    setFocusDate();
  }
  private void setFocusDate(){
    this.originToFocusDate = pixelToTime(this.originToFocusPixel, this.zoomLevel);
    this.dateFocus = TimeUtils.addTime(this.dateOrigin, this.originToFocusDate);
  }
  public void setFocusPixel(int x){
    this.pixelFocus = x;
    originToFocusPixel = pixelFocus - pixelOrigin;
  }



  private void focusImage(){
    focusImagePixel();
    focusImageDate();
  }
  private void focusImagePixel(){
    this.pixelOrigin = getWindowCenter()-this.originToFocusPixel;
  }
  private void focusImageDate(){

  }



  private void adjustFocus(int level){
    adjustFocusPixel(level);
    adjustFocusDate(level);
  }
  private void adjustFocusPixel(int level){
    this.originToFocusPixel *= ((double)level/(double)zoomLevel);
  }
  private void adjustFocusDate(int level){

  }



  public void strechImage(int zoomLevel){
    this.width = this.originalWidth * zoomLevel;
    adjustFocus(zoomLevel);
    this.zoomLevel = zoomLevel;
    repaint();
  }
  public void zoom(int level, int xFocus){
    setFocus(xFocus);
    strechImage(level);
    imageLoader.requestImage(this.dateFocus, level);
  }

  public void moveBy(int change){
    pixelOrigin += change;
    focusCenter();
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
    int j = pixelOrigin;
    while(j<=width+ pixelOrigin){
      g.drawLine(j,h-sp,j,h-(rw-sp));
      j+=(10*zoomLevel);
    }
    g.setColor(rulerColor);
    g.fillRect(0, h - rw, rw, rw);
  }

  @Override
  public void paint(Graphics g){
    super.paintComponent(g);
    focusImage();
    g.drawImage(image, pixelOrigin, 0, width, originalHeight, null);
    paintRulers(g);
  }
}