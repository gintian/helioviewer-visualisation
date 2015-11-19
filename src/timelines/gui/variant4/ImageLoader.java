package timelines.gui.variant4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Observable;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 19.11.2015.
 */
public class ImageLoader extends Observable{
  private String serverBaseURLStr;
  private BufferedImage img;

  public ImageLoader(String serverBaseURLStr){
    this.serverBaseURLStr = serverBaseURLStr;
  }

  public void requestImage(Date date, int zoomLevel){
    try {
      getImageFromURL(createURL(date, zoomLevel));
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
          //do stuff
        }
      }
    });
  }
  private URL createURL(Date date, int zoomLevel) throws MalformedURLException{
    return new URL(String.format("serverBaseURLStr/{0}/{1}", zoomLevel, date));
  }

  private void updateImage(){
    setChanged();
    notifyObservers(img);
  }
}
