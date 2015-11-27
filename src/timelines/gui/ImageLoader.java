package timelines.gui;

import timelines.api.APIImageMetadata;
import timelines.utils.TimeUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
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
  private APIImageMetadata[] metadatas;
  private Thread imageBuilder;
  private Image image;
  private Date startDate;
  private int imgCount = 1;

  public ImageLoader(Image image, String serverBaseURLStr, Date date, int zoomLevel, int tileCount){
    this.image = image;
    this.serverBaseURLStr = serverBaseURLStr;

    this.imgCount = tileCount;
    requestImages(date, zoomLevel, this.imgCount);
  }

  private void setImageCount(int tileWidth){
    System.out.println(this.imgCount); //TODO: remove after testing
    this.imgCount = (this.image.getWindow().getWidth() / tileWidth)+1;
    System.out.println(this.imgCount); //TODO: remove after testing
  }

  private void requestImages(Date date, int zoomLevel, int imgCount){
    try {
      bImageArr = new BufferedImage[imgCount];
      threads = new Thread[imgCount];
      metadatas = new APIImageMetadata[imgCount];
      for (int i = 0; i < imgCount; i++) {
        //TODO: change date for next request
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
          ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();

          InputStream is = new URL(url.toString()).openStream();
          ImageInputStream iis = ImageIO.createImageInputStream(is);

          cacheMetadata(i, new APIImageMetadata(iis));

          BufferedImage bi = ImageIO.read(url); //TODO: make mor efficient

          cacheBufferedImage(i, bi);
          if(i+1 == imgCount){
            buildBImage();
          }
        }catch (IOException e){
          logger.log(Level.WARNING, "ImageLoader Thread could not find the image under following URL: {0}", url.toString());
        }
      }
    });
    this.threads[i].start();
    logger.log(Level.INFO, "ImageLoader new Thread started with following URL: {0}", url.toString());
  }

  private URL createURL(Date date, int zoomLevel) throws MalformedURLException{
    return new URL(MessageFormat.format("{0}/api?zoomLevel={1}&dateFrom={2}", this.serverBaseURLStr, zoomLevel, TimeUtils.toString(date, "yyyy-MM-dd:HH:mm:ss")));
  }

  private void buildBImage(){
    this.imageBuilder = new Thread(new Runnable() {
      @Override
      public void run() {
        //metadata = new APIImageMetadata(bImageArr[0]);
        startDate = metadatas[0].getDateFrom();
        int tileWidth = bImageArr[0].getWidth();
        int w = tileWidth * imgCount;
        int h = bImageArr[0].getHeight();
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        for(int i = 0; i < imgCount; i++) {
          g.drawImage(bImageArr[i], i*tileWidth, 0, null);
        }
        bImage = combined;
        setImageCount(tileWidth);
        updateImage();
      }
    });
    this.imageBuilder.start();
  }

  private void cacheBufferedImage(int index, BufferedImage bImage){
    bImageArr[index] = bImage;
  }
  private void cacheMetadata(int index, APIImageMetadata metadata){
    metadatas[index] = metadata;
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
