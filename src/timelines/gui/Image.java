package timelines.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComponent;

import org.json.simple.parser.ParseException;
import timelines.utils.TimeUtils;

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
  private int maxZoomLevel;
  private int minZoomLevel;
  private int zoomLevel;
  private int rulerWidth = 20;
  private ImageLoader currentImageLoader;
  private Date dateOrigin; //current images start/leftmost date
  private Date dateFocus; //date in middle of screen, date of interest focused
  private Date dateLast; //current images end/rightmost date
  int imageOffset;
  public boolean loadingNext = false;

  public Image(Window window, Date dateFocus) throws IOException, ParseException {
    this.window = window;

    this.dateFocus = dateFocus;
    this.dateOrigin = dateFocus;

    this.pixelFocus = getWindowCenter();
    setZoomLevelBoundries();

    setImage();

  }

  private void setZoomLevelBoundries() throws IOException, ParseException {
    HashMap<String, Integer> hm = ImageLoader.getApiInfo(this.serverBaseURLStr);
    this.minZoomLevel = hm.get("zoomLevelTo");
    this.maxZoomLevel = hm.get("zoomLevelFrom");
    this.zoomLevel = this.minZoomLevel;
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
    //this.imageOffset = getWindowCenter()-dtp;
  }

  private void moveDateFocusBy(int change){
    adjustImageOffset(change);
    long dt = pixelToTime(pixelFocus - this.imageOffset, this.zoomLevel);
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

  public void updateImage(ImageLoader il){
    if(this.currentImageLoader.equals(il)){
      this.bufferedImage = il.getDiagram().getBufferedImage();
      this.bufferedImageWidth = this.bufferedImage.getWidth();
      this.bufferedImageHeight = this.bufferedImage.getHeight();
      this.dateOrigin = il.getDiagram().getStartDate();
      this.dateLast = il.getDiagram().getEndDate();

      setImageOffset();

      repaint();
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

  public void stretchImage(int zoomLevelChange, int cursorPosX){
    try {
      if(zoomLevelChange < 0){
//        this.bufferedImageWidth *=2;

        int stepCount = 10;
        int increasePerStep = bufferedImageWidth / stepCount;
        System.out.println("width before: " + bufferedImageWidth);
        System.out.println("width after: " + (bufferedImageWidth + (10* increasePerStep)));
        float percentage = bufferedImageWidth / 100;
        float percentageLeft = (cursorPosX - imageOffset) / percentage;
        System.out.println("percentage left: " + percentageLeft + "");
        System.out.println("offset: " + imageOffset);

        // test
        int testLeft = (int) (bufferedImageWidth / 100f * percentageLeft);
        System.out.println("calculated offset:" + (-(testLeft - cursorPosX)));

        for (int i = 0; i < stepCount; i++) {
          this.bufferedImageWidth += increasePerStep;
          int pixelsLeftOfFocus = (int) (bufferedImageWidth / 100f * percentageLeft);
          System.out.println("pixels left " + pixelsLeftOfFocus + " mouseX: " + cursorPosX + " image width: " + bufferedImageWidth);
          imageOffset = -(pixelsLeftOfFocus - cursorPosX);
          System.out.println("offset: " + imageOffset);
          paintImmediately(0, 0, window.getWidth(), window.getHeight());

          Thread.sleep(5);
        }

      }else{
        //this.bufferedImageWidth /= 2;

        int stepCount = 10;
        int increasePerStep = bufferedImageWidth / stepCount / 2;
        System.out.println("width before: " + bufferedImageWidth);
        System.out.println("width after: " + (bufferedImageWidth + (10* increasePerStep)));
        float percentage = bufferedImageWidth / 100;
        float percentageLeft = (cursorPosX - imageOffset) / percentage;
        System.out.println("percentage left: " + percentageLeft + "");
        System.out.println("offset: " + imageOffset);

        // test
        int testLeft = (int) (bufferedImageWidth / 100f * percentageLeft);
        System.out.println("calculated offset:" + (-(testLeft - cursorPosX)));

        for (int i = 0; i < stepCount; i++) {
          this.bufferedImageWidth -= increasePerStep;
          int pixelsLeftOfFocus = (int) (bufferedImageWidth / 100f * percentageLeft);
          System.out.println("pixels left " + pixelsLeftOfFocus + " mouseX: " + cursorPosX + " image width: " + bufferedImageWidth);
          imageOffset = -(pixelsLeftOfFocus - cursorPosX);
          System.out.println("offset: " + imageOffset);
          paintImmediately(0, 0, window.getWidth(), window.getHeight());

          Thread.sleep(5);
        }

      }
    } catch (InterruptedException e) {
//       TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  public void zoom(int levelChange, int pixelFocus){
    if(!(((zoomLevel+levelChange) < maxZoomLevel)||((zoomLevel+levelChange) > minZoomLevel))||(levelChange == 0)){
      this.pixelFocus = pixelFocus;
      moveDateFocusTo(pixelFocus);
      stretchImage(levelChange, pixelFocus);
      this.zoomLevel += levelChange;
      setImageOffset();
      repaint();
      setImage();
    }
  }

  public void dragged(int change) {
    moveDateFocusBy(change);
    checkBounds(change);
    repaint();
  }

  private void checkBounds(int change){
    if ((change > 0) && !(imageOffset < 0) && !this.loadingNext){
      loadingNext = true;
      this.currentImageLoader = ImageLoader.loadAdditional(this, this.bufferedImage,this.serverBaseURLStr, this.dateOrigin, this.dateLast, this.zoomLevel, ImageLoader.LEFT);
    }else if ((change < 0) && this.window.getContentPane().getWidth()-imageOffset>this.bufferedImage.getWidth() && !this.loadingNext){
      loadingNext = true;
      this.currentImageLoader = ImageLoader.loadAdditional(this, this.bufferedImage,this.serverBaseURLStr, this.dateOrigin, this.dateLast, this.zoomLevel, ImageLoader.RIGHT);
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
//    int i = h-rw-1;
//    while(i>=0){
//      g.drawLine(sp,i,rw-sp,i);
//      i-=(10);
//    }

    int spacing = currentImageLoader.getTileHeight() / 6;

    for (int j = 3; j < 10; j++) {
      g.drawLine(sp, (j-3) * spacing , rw - sp, (j-3) * spacing);
    }


    g.setColor(Color.BLACK);
    g.drawString("10e-3", 20, 10);
    for (int i = 1; i < 6; i++) {
      g.drawString("10e-" + (i + 3), rulerWidth, i * spacing);
    }
    g.drawString("10e-9", rulerWidth, currentImageLoader.getTileHeight());

    g.setColor(rulerColor);
    g.fillRect(rw,h-rw,w-rw,rw);
    /*g.setColor(scaleColor);
    int j = pixelOrigin;
    while(j<= bufferedImageWidth + pixelOrigin){
      g.drawLine(j,h-sp,j,h-(rw-sp));
      j+=(10*zoomLevel);
    }
    g.setColor(rulerColor);*/
    g.fillRect(0, h - rw, rw, rw);

    g.setColor(Color.BLACK);
    int dateLabelWidth = 95;
    Date leftMost = TimeUtils.addTime(dateOrigin,pixelToTime(-imageOffset, zoomLevel));
    Date rightMost = TimeUtils.addTime(leftMost,pixelToTime(window.getWidth() - rulerWidth, zoomLevel));

    g.drawString(TimeUtils.toString(leftMost, "dd-MM-yyyy"), rulerWidth, window.getHeight() - 50);

    g.setColor(scaleColor);
    g.drawLine(rulerWidth, h - rw + 3, rulerWidth, h - rw + rulerWidth - 6);
    //g.drawString(TimeUtils.toString(rightMost, "dd-MM-yyyy"), window.getWidth() - dateLabelWidth, window.getHeight() - 50);

    int labelPos = dateLabelWidth + 20;

    String dateFormat = "dd-MM-yyyy";
    if (pixelToTime(labelPos, zoomLevel) < 24 * 60 * 60 * 1000) {
      dateFormat = "dd-MM-yy HH:mm";
    }

    while (labelPos < window.getWidth() - dateLabelWidth) {
      Date current = TimeUtils.addTime(leftMost,pixelToTime(labelPos, zoomLevel));
      g.setColor(Color.BLACK);
      g.drawString(TimeUtils.toString(current, dateFormat), labelPos, window.getHeight() - 50);

      g.setColor(scaleColor);
      g.drawLine(labelPos, h - rw + 3, labelPos, h - rw + rulerWidth - 6);

      labelPos += dateLabelWidth;
    }
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
