package timelines.gui.variant5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import timelines.utils.TimeUtils;


/**
 * Created by Tobi on 05.12.2015.
 */
public class ImageLoader {

  private static final String SERVER_BASE_URL = "http://server1125.cs.technik.fhnw.ch:8080/timelines";

  private Image image;
//  private String serverBaseURLStr;
  private int tileCount;
  public DiagramBuffer diagramBuffer;
//  private int tileWidth;
//  private int tileHeight;
//  private int minZoomLevel;
//  private int maxZoomLevel;
  private APIInfo apiInfo;
  private int zoomLevel;
  private Diagram diagram;
  private int callType;
  private static final int NEW_SET = 1;
  private static final int EXTEND_SET = 2;


  public  ImageLoader(Image image, DiagramBuffer diagramBuffer) {
    this.callType = NEW_SET;
    this.image = image;
    this.diagramBuffer = diagramBuffer;
    accessApiInfo();
  }

  public void loadImage(Date from, int zoomLevel) {
    ImageRunnable runnable = new ImageRunnable(this, makeUrl(from, zoomLevel));
    runnable.run();
  }

//  private URL[] setUrls(Date fromDate) throws MalformedURLException{
//    URL[] urlArray = new URL[tileCount];
//    for (int i = 0; i < tileCount; i++){
//      urlArray[i] = makeUrl(fromDate, i);
//    }
//    return urlArray;
//  }

  private URL makeUrl(Date date, int zoomLevel) {
    try {
      return new URL(MessageFormat.format("{0}/api?zoomLevel={1}&dateFrom={2}", ImageLoader.SERVER_BASE_URL, zoomLevel, TimeUtils.toString(date, "yyyy-MM-dd:HH:mm:ss")));
    } catch (MalformedURLException e) {
      // Should never happen unless the url is specified wrong
      e.printStackTrace();
    }
    return null;
  }

//  private void
//  getImages(Date dateFrom) throws MalformedURLException{
//    URL[] urls = setUrls(dateFrom);
//    Thread[] threads = new Thread[this.tileCount];
//    this.tileBuffer = new DiagramBuffer(this, this.tileCount);
//    for (int i =0; i < this.tileCount; i++){
//      threads[i] = new Thread(new ImageRunnable(this, urls[i]));
//      threads[i].start();
//    }
//  }

  private void accessApiInfo() {

    apiInfo = new APIInfo();

    String jsonString = "";
    try {

      URL url = new URL(MessageFormat.format("{0}/apiInfo", ImageLoader.SERVER_BASE_URL));

      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String inputLine;
      while ((inputLine = in.readLine()) != null)
        jsonString += inputLine;
      in.close();

    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    try {

      JSONObject infoJsonObject = (JSONObject) JSONValue.parseWithException(jsonString);

      apiInfo.setTileHeight((int)(long)infoJsonObject.get("height"));
      apiInfo.setTileWidth((int)(long)infoJsonObject.get("width"));
      apiInfo.setMaxZoomLevel((int)(long)infoJsonObject.get("zoomLevelFrom"));
      apiInfo.setMinZoomLevel((int)(long)infoJsonObject.get("zoomLevelTo"));

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public APIInfo getApiInfo() {
    return apiInfo;
  }

//  private void setTileCount(){
//    this.tileCount = (this.image.getWindow().getWidth() / this.tileWidth)+1;
//    System.out.println("set tile count to: "+this.tileCount);//TODO:remove
//  }
//
//  public void processDiagramBuffer(){
//    if(this.callType == NEW_SET){
//      makeNewSet();
//    }else if (this.callType == EXTEND_SET){
//
//    }
//  }
//  private void makeNewSet(){
//    TreeMap<Long, Diagram> tm = this.tileBuffer.getMap();
//    int w = this.tileCount * this.tileWidth;
//    int h = this.tileHeight;
//    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//    Graphics g = img.getGraphics();
//
//    int counter = 0;
//    for(Long key : tm.keySet())
//    {
//      g.drawImage(tm.get(key).getBufferedImage(), counter * this.tileWidth, 0, null);
//      counter++;
//      System.out.println("making: "+counter);//TODO:remove
//    }
//    Date startDate = tm.firstEntry().getValue().getStartDate();
//    Date endDateDate = tm.lastEntry().getValue().getEndDate();
//
//    this.diagram = new Diagram(img, startDate, endDateDate, this.zoomLevel);
//    updateImage();
//  }
//  private void extendSet(){
//
//  }
//
//  private void updateImage(){
//    this.image.updateImage(this);
//  }
//
//  public Diagram getDiagram(){
//    return this.diagram;
//  }

}
