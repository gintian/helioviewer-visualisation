package timelines.gui.variant4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 19.11.2015.
 */
public class ImageLoader extends Observable{
  private static final Logger logger = Logger.getLogger(ImageLoader.class.getName());
  private String serverBaseURLStr;
  private BufferedImage img;

  public ImageLoader(String serverBaseURLStr){
    this.serverBaseURLStr = serverBaseURLStr;
  }

  public void requestImage(Date date, int zoomLevel){
    try {
      //getImageFromURL(createURL(date, zoomLevel));
      //getImageFromURL(new URL("http://127.0.0.1/i4ds05/Chrysanthemum.jpg"));
      System.out.println("get image");
      getImageFromURL(new URL("localhost:8080/api?zoomLevel=6&dateFrom=1980-07-01:00:00:00"));
      System.out.println("got image");
    }catch (MalformedURLException e){
      //do stuff
    }
  }

  private void getImageFromURL(URL url){

    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          img = ImageIO.read(url);
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
    return new URL(String.format("{0}/{1}/{2}", serverBaseURLStr, zoomLevel, date));
  }

  private void updateImage(){
    setChanged();
    notifyObservers(img);
  }
}
