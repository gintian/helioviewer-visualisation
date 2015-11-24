package timelines.gui.variant4;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

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
      getImageFromURL(createURL(date, zoomLevel));

      //getImageFromURL(new URL("http://127.0.0.1/i4ds05/Chrysanthemum.jpg"));
      //getImageFromURL(new URL("http://localhost:8080/api?zoomLevel=10&dateFrom=1980-07-01:00:00:00"));
    }catch (MalformedURLException e){
      //do stuff
    }
  }

  private void getImageFromURL(URL url){

    Thread t = new Thread(new Runnable() {
      @Override
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
    //return new URL(String.format("{0}/api?zoomLevel={1}&dateFrom={2}", serverBaseURLStr, zoomLevel, TimeUtils.toString(date, "yyyy-MM-dd:HH:mm:ss")));
    return new URL(MessageFormat.format("{0}/api?zoomLevel={1}&dateFrom={2}", serverBaseURLStr, zoomLevel, "1980-07-01:00:00:00"));
  }

  private void updateImage(){
    setChanged();
    notifyObservers(img);
  }
}
