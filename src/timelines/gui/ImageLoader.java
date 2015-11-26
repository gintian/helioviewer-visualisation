package timelines.gui;

import timelines.api.APIImageMetadata;
import timelines.utils.ImageUtils;
import timelines.utils.TimeUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 19.11.2015.
 */
public class ImageLoader{
  private static final Logger logger = Logger.getLogger(ImageLoader.class.getName());

  private String serverBaseURLStr;
  private BufferedImage bImage;
  private BufferedImage[] bImageArr;
  private Thread[] threads;
  private APIImageMetadata metadata;
  private Thread imageBuilder;
  private Image image;
  private Date startDate;
  private int imgCount = 1;

  public ImageLoader(Image image, String serverBaseURLStr){
    this.image = image;
    this.serverBaseURLStr = serverBaseURLStr;
  }

  private void setImageCount(int tileWidth){
    this.imgCount = (this.image.getWindow().getWidth() / tileWidth)+1;
  }

  public void requestImage(Date date, int zoomLevel, int tileCount){
    requestImages(date, zoomLevel, this.imgCount);
    this.imgCount = tileCount;
  }

  private void requestImages(Date date, int zoomLevel, int imgCount){
    try {
      bImageArr = new BufferedImage[imgCount];
      threads = new Thread[imgCount];
      for (int i = 0; i < imgCount; i++) {
        getImageFromURL(createURL(date, zoomLevel), i);
      }
    }catch (MalformedURLException e){
      logger.log(Level.WARNING, "URL could not be created");
    }
  }

  private void getImageFromURL(URL url, int i){

    threads[i] = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(2000);  //TODO: remove when done with testing
          bImageArr[i] = ImageIO.read(url);
          if(i+1 == imgCount){
            buildBImage();
          }
        }catch (IOException e){
          logger.log(Level.WARNING, "ImageLoader Thread could not find the image under following URL: {0}", url.toString());
        }catch (InterruptedException e){  //TODO: remove when done with testing

        }
      }
    });
    this.threads[i].start();
    logger.log(Level.INFO, "ImageLoader new Thread started with following URL: {0}", url.toString());
  }

  private URL createURL(Date date, int zoomLevel) throws MalformedURLException{
    return new URL(MessageFormat.format("{0}/api?zoomLevel={1}&dateFrom={2}", this.serverBaseURLStr, zoomLevel, TimeUtils.toString(date, "yyyy-MM-dd:HH:mm:ss"))); //TODO: replace test date with date field when done testing
  }

  private void buildBImage(){
    this.imageBuilder = new Thread(new Runnable() {
      @Override
      public void run() {
        metadata = new APIImageMetadata(bImageArr[0]);
        startDate = metadata.getDateFrom();
        int tileWidth = bImageArr[0].getWidth();
        setImageCount(tileWidth);
        int w = tileWidth * imgCount;
        int h = bImageArr[0].getHeight();
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        for(int i = 0; i < imgCount; i++) {
          g.drawImage(bImageArr[i], i*tileWidth, 0, null);
        }
        bImage = combined;
        updateImage();
      }
    });
    this.imageBuilder.start();
  }

  private void updateImage(){
    this.image.updateImage(this);
  }


  public BufferedImage getbImage() {
    return bImage;
  }

  public Date getStartDate() {
    return startDate;
  }

  public int getImgCount() {
    return imgCount;
  }
}
