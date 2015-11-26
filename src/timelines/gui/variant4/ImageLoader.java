package timelines.gui.variant4;

import timelines.utils.TimeUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 19.11.2015.
 */
public class ImageLoader{
  private static final Logger logger = Logger.getLogger(ImageLoader.class.getName());
  private String serverBaseURLStr;
  private BufferedImage bImage;
  private Image image;
  private int imgCount = 1;
  private BufferedImage[] imgArr;

  public ImageLoader(Image image, String serverBaseURLStr, Date date){
    this.image = image;
    this.serverBaseURLStr = serverBaseURLStr;
    requestImage(date, 1);
  }

  public void setImageCount(){
    this.imgCount = (this.image.getWindow().getWidth() / this.bImage.getWidth())+1;
  }

  public void requestImage(Date date, int zoomLevel){
    try {
      getImageFromURL(createURL(date, zoomLevel));
    }catch (MalformedURLException e){
      logger.log(Level.WARNING, "URL could not be created");
    }
  }

  private void getImageFromURL(URL url){

    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          bImage = ImageIO.read(url);
          updateImage();
        }catch (IOException e){
          logger.log(Level.WARNING, "ImageLoader Thread could not find the image under following URL: {0}", url.toString());
        }
      }
    });
    t.start();
    logger.log(Level.INFO, "ImageLoader new Thread started with following URL: {0}", url.toString());
  }
  private URL createURL(Date date, int zoomLevel) throws MalformedURLException{
    return new URL(MessageFormat.format("{0}/api?zoomLevel={1}&dateFrom={2}", serverBaseURLStr, zoomLevel, TimeUtils.toString(date, "yyyy-MM-dd:HH:mm:ss"))); //TODO: replace test date with date field when done testing
  }

  private void updateImage(){
    image.updateImage(bImage);
  }
}