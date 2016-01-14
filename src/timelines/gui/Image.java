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

  private static final String serverBaseURLStr = "http://server1125.cs.technik.fhnw.ch:8080/timelines";

  private BufferedImage bufferedImage;
  private int bufferedImageHeight;
  private int bufferedImageWidth;
  private int pixelFocus;
  private Window window;
  private int zoomLevel = 21; //TODO:set via imageloader
  private int maxZoomLevel = 1; //TODO:set via imageloader
  private int minZoomLevel = 21; //TODO:set via imageloader
  private int rulerWidth = 20;
  private ImageLoader currentImageLoader;
  private Date dateOrigin; //current images start/leftmost date
  private Date dateFocus; //date in middle of screen, date of interest focused
  private Date dateLast; //current images end/rightmost date
  int imageOffset;
  public boolean loadingNext = false;

  public Image(Window window, Date dateFocus){
    this.window = window;

    this.dateFocus = dateFocus;
    this.dateOrigin = dateFocus;

    this.pixelFocus = getWindowCenter();

    setImage();

  }

  public static int timeToPixel(long time, int zoomLevel){
    return (int)(time/(1000*Math.pow(2, zoomLevel)));
  }

  public static long pixelToTime(int pixel, int zoomLevel){
    return (long)Math.pow(2, zoomLevel)*pixel*1000;
  }

  private void setImageOffset(){
    int dtp = timeToPixel(TimeUtils.difference(this.dateFocus, this.dateOrigin), this.zoomLevel); //time difference between image start date (dateOrigin) and date in middle of screen (dazeFocus) converted to pixel
    this.imageOffset = this.pixelFocus-dtp;
  }

  private void moveDateFocusBy(int change){
    adjustImageOffset(change);
    long dt = pixelToTime(getWindowCenter() - this.imageOffset, this.zoomLevel);
    this.dateFocus = TimeUtils.addTime(this.dateOrigin, dt);
  }

  private void moveDateFocusTo(int pixelFocus){
    long dt = pixelToTime(pixelFocus - this.imageOffset, this.zoomLevel);
    this.dateFocus = TimeUtils.addTime(this.dateOrigin, dt);
  }

  private void adjustImageOffset(int change){
    this.imageOffset += change;
  }

  private Date getRequestDate(){
    System.out.println("dateFocus: "+dateFocus);
    return  TimeUtils.addTime(dateFocus, pixelToTime(-getWindowWidthHalf(), this.zoomLevel));
  }

  public void updateImage(ImageLoader il){  //TODO: fix if becomes problem with multithreading
    if(this.currentImageLoader.equals(il)){
      this.bufferedImage = il.getDiagram().getBufferedImage();
      this.bufferedImageWidth = this.bufferedImage.getWidth();
      this.bufferedImageHeight = this.bufferedImage.getHeight();
      this.dateOrigin = il.getDiagram().getStartDate();
      this.dateLast = il.getDiagram().getEndDate();

      setImageOffset();

      repaint();  //TODO: point of interest
      this.loadingNext = false;
    }
  }

  private void setImage(){
    this.currentImageLoader = ImageLoader.loadNewSet(this, this.serverBaseURLStr, getRequestDate(), this.zoomLevel);
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

  public void stretchImage(int zoomLevelChange){
    if(zoomLevelChange < 0){
      this.bufferedImageWidth *=2;
    }else{
      this.bufferedImageWidth /= 2;
    }
  }
  public void zoom(int levelChange, int pixelFocus){
    if(!(((zoomLevel+levelChange) < maxZoomLevel)||((zoomLevel+levelChange) > minZoomLevel))||(levelChange == 0)){
      this.pixelFocus = pixelFocus;
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
    checkBounds();
    repaint();
  }

  private void checkBounds(){
    long boundsDistance = pixelToTime(this.window.getContentPane().getWidth(), this.zoomLevel)/2;
    if ((this.dateFocus.getTime()-boundsDistance)<this.dateOrigin.getTime() && !this.loadingNext){
      loadingNext = true;
      this.currentImageLoader = ImageLoader.loadAdditional(this, this.bufferedImage,this.serverBaseURLStr, this.dateOrigin, this.dateLast, this.zoomLevel, boundsDistance, ImageLoader.LEFT);
    }else if ((this.dateFocus.getTime()+boundsDistance)>this.dateLast.getTime() && !this.loadingNext){
      loadingNext = true;
      this.currentImageLoader = ImageLoader.loadAdditional(this, this.bufferedImage,this.serverBaseURLStr, this.dateOrigin, this.dateLast, this.zoomLevel, boundsDistance, ImageLoader.RIGHT);
    }

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
    int j = imageOffset;
    while(j<= bufferedImageWidth + imageOffset){
      g.drawLine(j,h-sp,j,h-(rw-sp));
      j+=(10*zoomLevel);
    }
    g.setColor(rulerColor);
    g.fillRect(0, h - rw, rw, rw);
  }

  @Override
  public void paint(Graphics g){
    super.paintComponent(g);
    Color temp = g.getColor();
    g.setColor(Color.WHITE);
    g.drawRect(0,0,getWidth(),getHeight());
    g.setColor(temp);
    g.drawImage(this.bufferedImage, this.imageOffset, 0, this.bufferedImageWidth, this.bufferedImageHeight, null);
    paintRulers(g);
  }
}
