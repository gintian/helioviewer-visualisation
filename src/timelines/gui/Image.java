package timelines.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JComponent;

import timelines.database.TimelinesDB;
import timelines.utils.TimeUtils;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 28.10.2015.
 */
public class Image extends JComponent {

  private BufferedImage bufferedImage;
  private int bufferedImageHeight;
  private int bufferedImageWidth;
  private int pixelOrigin;
  private int pixelFocus;
  private int pixelOriginToFocus; //difference between pixelFocus and pixelOrigin
  private Window window;
  private int zoomLevel = 10; //TODO:set via ImageLoader
  private int maxZoomLevel = 1; //TODO:set via ImageLoader
  private int minZoomLevel = 10; //TODO:set via ImageLoader
  private int rulerWidth = 20;
//  private ImageLoader currentImageLoader;
  private DiagramBuffer diagramBuffer;
  private Date dateOrigin; //current images start/leftmost date
  private Date dateFocus; //date in middle of screen, date of interest focused
  private Date dateLast;
  int imageOffset;

  private static final Date DB_START_DATE = new Date(141865200000L); // 1974-07-01 TODO we should probably get this from the API

  public Image(Window window, Date dateFocus){

    this.window = window;

    this.dateFocus = dateFocus;
    this.dateOrigin = dateFocus;

    this.bufferedImage = new BufferedImage(this.window.getContentPane().getWidth(), this.window.getContentPane().getHeight(), bufferedImage.TYPE_INT_ARGB);

    this.diagramBuffer = new DiagramBuffer(this);

    update();

  }

  public static int timeToPixel(long time, int zoomLevel){
    return (int)(time/(1000*Math.pow(2, zoomLevel)));
  }

  public static long pixelToTime(int pixel, int zoomLevel){
    return (long)Math.pow(2, zoomLevel)*pixel*1000;
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

  /**
   * Request all required images from the buffer and draw them
   */
  public void update(){

    // get the actual start date of the first required image
    long dataPoints = (long) Math.pow(2, zoomLevel) * diagramBuffer.getAPIInfo().getTileWidth();
    long imageOffset = (dateOrigin.getTime() - TimelinesDB.DB_START_DATE.getTime()) / 2000 / dataPoints;
    Date actualStartDate = new Date(DB_START_DATE.getTime() + imageOffset * dataPoints * 2000);

    // Get the needed images from the buffer
    // TODO get all required images and not just the first one
    Diagram d = diagramBuffer.getDiagram(actualStartDate, zoomLevel);

    // if what we got is not null (otherwise a load has been initialized), draw what we got
    if(d != null) {
      Graphics2D g = bufferedImage.createGraphics();
      g.setBackground(new Color(255, 255, 255, 0));
      g.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      g.drawImage(d.getBufferedImage(), null, 0, 0); // TODO draw the image with the required offset based on its start time
      g.dispose();

      repaint();
    }
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
      this.bufferedImageWidth *=2;
    }else{
      this.bufferedImageWidth /= 2;
    }
  }
  public void zoom(int levelChange, int pixelFocus){
    if(!(((zoomLevel+levelChange) < maxZoomLevel)||((zoomLevel+levelChange) > minZoomLevel))||(levelChange == 0)){
      moveDateFocusTo(pixelFocus);
      stretchImage(levelChange);
      this.zoomLevel += levelChange;
      setImageOffset();
      repaint();
      update();
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
    while(j<= bufferedImage.getWidth() + pixelOrigin){
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
    g.drawImage(this.bufferedImage, this.imageOffset, 0, this.bufferedImage.getWidth(), this.bufferedImage.getHeight(), null);
    paintRulers(g);
  }
}
