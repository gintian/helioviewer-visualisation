package timelines.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import timelines.utils.TimeUtils;


/**
 * Created by Tobi on 05.12.2015.
 */
public class ImageLoader {

  private Image image;
  private String serverBaseURLStr;
  private int tileCount;
  public DiagramBuffer tileBuffer;
  private int tileWidth;
  private int tileHeight;
  private int minZoomLevel;
  private int maxZoomLevel;
  private int zoomLevel;
  private Diagram diagram;
  private int callType;
  private static final int NEW_SET = 1;
  private static final int EXTEND_SET = 2;

  public static ImageLoader loadNewSet(Image image, String serverBaseURLStr, Date date, int zoomLevel){
    try {
      return new ImageLoader(image, serverBaseURLStr, date, zoomLevel);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (org.json.simple.parser.ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
  private ImageLoader(Image image, String serverBaseURLStr, Date startDate, int zoomLevel) throws IOException, org.json.simple.parser.ParseException {
    this.callType = NEW_SET;
    this.serverBaseURLStr = serverBaseURLStr;
    this.image = image;
    this.zoomLevel = zoomLevel;
    getApiInfo();
    setTileCount();
    getImages(startDate);
  }

  public static ImageLoader loadAdditional(Image image, Diagram diagram, String serverBaseURLStr, Date startDate, Date endDate, int zoomLevel){
    return new ImageLoader(image, diagram, serverBaseURLStr, startDate, endDate, zoomLevel);
  }
  private ImageLoader(Image image, Diagram diagram, String serverBaseURLStr, Date startDate, Date endDate, int zoomLevel){
    this.callType = EXTEND_SET;
    this.image = image;
    this.serverBaseURLStr = serverBaseURLStr;
    this.zoomLevel = zoomLevel;
    this.diagram = diagram;
  }

  private URL[] setUrls(Date fromDate) throws MalformedURLException{
    URL[] urlArray = new URL[tileCount];
    for (int i = 0; i < tileCount; i++){
      urlArray[i] = makeUrl(fromDate, i);
    }
    return urlArray;
  }
  private URL makeUrl(Date date, int i) throws MalformedURLException{
    Date tempDate = TimeUtils.addTime(date, Image.pixelToTime(this.tileWidth * i, this.zoomLevel));
    System.out.println("zl: "+zoomLevel);//TODO:remove
    System.out.println("tw: "+tileWidth);
    System.out.println("tm: "+Image.pixelToTime(this.tileWidth * i, this.zoomLevel));//TODO:remove
    return new URL(MessageFormat.format("{0}/api?zoomLevel={1}&dateFrom={2}", this.serverBaseURLStr, this.zoomLevel, TimeUtils.toString(tempDate, "yyyy-MM-dd:HH:mm:ss")));
  }

  private void getImages(Date dateFrom) throws MalformedURLException{
    URL[] urls = setUrls(dateFrom);
    Thread[] threads = new Thread[this.tileCount];
    this.tileBuffer = new DiagramBuffer(this, this.tileCount);
    for (int i =0; i < this.tileCount; i++){
      threads[i] = new Thread(new ImageRunnable(this, urls[i]));
      threads[i].start();
    }
  }

  private void getApiInfo() throws IOException, org.json.simple.parser.ParseException{
    URL url = new URL(MessageFormat.format("{0}/apiInfo", this.serverBaseURLStr));
    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    String jsonString = "";
    String inputLine;
    while ((inputLine = in.readLine()) != null)
      jsonString += inputLine;
    in.close();

    JSONObject infoJsonObject = (JSONObject) JSONValue.parseWithException(jsonString);
    this.tileHeight = (int)(long)infoJsonObject.get("height");
    this.tileWidth = (int)(long)infoJsonObject.get("width");
    this.minZoomLevel = (int)(long)infoJsonObject.get("zoomLevelTo");
    this.maxZoomLevel = (int)(long)infoJsonObject.get("zoomLevelFrom");
  }

  private void setTileCount(){
    this.tileCount = (this.image.getWindow().getWidth() / this.tileWidth)+1;
    System.out.println("set tile count to: "+this.tileCount);//TODO:remove
  }

  public void processDiagramBuffer(){
    if(this.callType == NEW_SET){
      makeNewSet();
    }else if (this.callType == EXTEND_SET){

    }
  }
  private void makeNewSet(){
    TreeMap<Long, Diagram> tm = this.tileBuffer.getMap();
    int w = this.tileCount * this.tileWidth;
    int h = this.tileHeight;
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.getGraphics();

    int counter = 0;
    for(Long key : tm.keySet())
    {
      g.drawImage(tm.get(key).getBufferedImage(), counter * this.tileWidth, 0, null);
      counter++;
      System.out.println("making: "+counter);//TODO:remove
    }
    Date startDate = tm.firstEntry().getValue().getStartDate();
    Date endDateDate = tm.lastEntry().getValue().getEndDate();

    this.diagram = new Diagram(img, startDate, endDateDate, this.zoomLevel);
    updateImage();
  }
  private void extendSet(){

  }

  private void updateImage(){
    this.image.updateImage(this);
  }

  public Diagram getDiagram(){
    return this.diagram;
  }

}
