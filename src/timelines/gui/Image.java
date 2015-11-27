package timelines.gui;

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
  private int imageHeight;
  private int imageWidth;
  private int pixelOrigin;
  private int pixelFocus;
  private int pixelOriginToFocus; //difference betwene pixelFocus and pixelOrigin
  private Window window;
  private int zoomLevel = 10;
  private int maxZoomLevel = 1;
  //private int minZoomLevel = 18;
  private int minZoomLevel = 10;
  private int rulerWidth = 20;
  private ImageLoader currentImageLoader;
  private Date dateOrigin; //current images start/leftmost date
  private Date dateFocus; //date in middle of screen, date of interest focused
  private int tileCount=1;
  int imageOffset;

  public Image(Window window, Date dateFocus){
    this.window = window;

    this.dateFocus = dateFocus;
    this.dateOrigin = dateFocus;

    setImage();

  }

  public static int timeToPixel(long time, int zoomLevel){
    return (int)(time/Math.pow(2, zoomLevel));
  }

  public static long pixelToTime(int pixel, int zoomLevel){
    return (long)Math.pow(2, zoomLevel)*pixel;
  }

  private void setImageOffset(){
    int dtp = timeToPixel(TimeUtils.difference(this.dateFocus, this.dateOrigin), this.zoomLevel); //time difference between image start date (dateOrigin) and date in middle of screen (dazeFocus) converted to pixel
    int c = getWindowCenter();
    this.imageOffset = c-dtp;
  }

  private void moveDateFocusBy(int change){
    adjustImageOffset(change);
    long dt = pixelToTime(getWindowCenter() - this.imageOffset, this.zoomLevel);
    this.dateFocus = TimeUtils.addTime(this.dateOrigin, dt);
  }

  private void moveDateFocusTo(int pixelFocus){
    long dt = pixelToTime(pixelFocus - this.imageOffset, this.zoomLevel);
    this.dateFocus = TimeUtils.addTime(this.dateOrigin, dt);
    setImageOffset();
  }

  private void adjustImageOffset(int change){
    this.imageOffset += change;
  }

  private Date getRequestDate(){
    return  TimeUtils.addTime(dateFocus, pixelToTime(-getWindowWidthHalf(), this.zoomLevel));
  }



  public int getZoomLevel(){
    return  zoomLevel;
  }

  public void updateImage(ImageLoader il){  //TODO: fix if becomes problem with multithreading
    if(this.currentImageLoader.equals(il)){
      this.image = il.getbImage();
      this.imageWidth = image.getWidth();
      this.imageHeight = image.getHeight();
      this.tileCount = il.getImgCount();
      this.dateOrigin = il.getStartDate();

      repaint();  //TODO: point of interest
    }
  }

  private void setImage(){
    this.currentImageLoader = new ImageLoader(this, this.serverBaseURLStr, getRequestDate(), this.zoomLevel, this.tileCount);
  }

  public Window getWindow() {
    return window;
  }

  private int getWindowCenter(){
    return (this.window.getContentPane().getWidth()+this.rulerWidth)/2;
  }
  private int getWindowWidthHalf(){
    return (this.window.getContentPane().getWidth()/2);
  }

  private void setFocusPointCenter(){
    setFocusPoint(getWindowCenter());
  }

  public void setFocusPoint(int x){
    this.pixelFocus = x;
    pixelOriginToFocus = pixelFocus - pixelOrigin;
  }

  private void focusImage(){
    this.pixelOrigin = getWindowCenter()-this.pixelOriginToFocus;
  }

  public void stretchImage(int zoomLevelChange){
    if(zoomLevelChange < 0){
      this.imageWidth *=2;
    }else{
      this.imageWidth /= 2;
    }
  }
  public void zoom(int levelChange, int pixelFocus){
    if(!(((zoomLevel+levelChange) < maxZoomLevel)||((zoomLevel+levelChange) > minZoomLevel))||(levelChange == 0)){
      moveDateFocusTo(pixelFocus);
      stretchImage(levelChange);
      this.zoomLevel += levelChange;
      setImageOffset();
      repaint();
      setImage();
    }
  }

  public void dragged(int change) {
    moveDateFocusBy(change);
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
    while(j<= imageWidth + pixelOrigin){
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
    g.drawImage(this.image, this.imageOffset, 0, this.imageWidth, this.imageHeight, null);
    paintRulers(g);
  }
}
