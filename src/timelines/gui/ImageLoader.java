package timelines.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import timelines.api.APIImageMetadata;
import timelines.utils.TimeUtils;


/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 05.12.2015.
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
  private int sideToExpand;
  public static final int NEW_SET = 1;
  public static final int EXTEND_SET = 2;
  public static final int LEFT = 3;
  public static final int RIGHT = 4;

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
    setApiInfo(getApiInfo(serverBaseURLStr));
    setTileCount();
    getImages(startDate);
  }

  public static ImageLoader loadAdditional(Image image, BufferedImage bufferedImage, String serverBaseURLStr, Date startDate, Date endDate, int zoomLevel, int sideToExpand){
    try {
      return new ImageLoader(image, bufferedImage, serverBaseURLStr, startDate, endDate, zoomLevel, sideToExpand);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (org.json.simple.parser.ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
  private ImageLoader(Image image, BufferedImage bufferedImage, String serverBaseURLStr, Date startDate, Date endDate, int zoomLevel, int sideToExpand) throws IOException, org.json.simple.parser.ParseException {
    this.callType = EXTEND_SET;
    this.image = image;
    this.serverBaseURLStr = serverBaseURLStr;
    this.zoomLevel = zoomLevel;
    this.diagram = new Diagram(bufferedImage, new APIImageMetadata(startDate, endDate, zoomLevel));
    this.sideToExpand = sideToExpand;
    setApiInfo(getApiInfo(serverBaseURLStr));
    setTileCount(1);
    if (sideToExpand == LEFT) {
      getImages(TimeUtils.addTime(startDate, -Image.pixelToTime(this.tileWidth/2, zoomLevel)));
    } else if (sideToExpand == RIGHT){
      getImages(endDate);
    }

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

  public static HashMap<String, Integer> getApiInfo(String serverBaseURLStr) throws IOException, org.json.simple.parser.ParseException{
    URL url = new URL(MessageFormat.format("{0}/apiInfo", serverBaseURLStr));
    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    String jsonString = "";
    String inputLine;
    while ((inputLine = in.readLine()) != null)
      jsonString += inputLine;
    in.close();

    HashMap<String, Integer> infos = new HashMap<>();
    JSONObject infoJsonObject = (JSONObject) JSONValue.parseWithException(jsonString);
    infos.put("height",(int)(long)infoJsonObject.get("height"));
    infos.put("width",(int)(long)infoJsonObject.get("width"));
    infos.put("zoomLevelTo",(int)(long)infoJsonObject.get("zoomLevelTo"));
    infos.put("zoomLevelFrom",(int)(long)infoJsonObject.get("zoomLevelFrom"));

    return infos;
  }

  private void setApiInfo(HashMap<String,Integer> infos){
    this.tileHeight = infos.get("height");
    this.tileWidth = infos.get("width");
    this.minZoomLevel = infos.get("zoomLevelTo");
    this.maxZoomLevel = infos.get("zoomLevelFrom");
  }

  private void setTileCount(){
    this.tileCount = (this.image.getWindow().getWidth() / this.tileWidth)+2;
  }

  private void setTileCount(int tileCount){
    this.tileCount = tileCount;
  }

  public void processDiagramBuffer(){
    if(this.callType == NEW_SET){
      makeNewSet();
    }else if (this.callType == EXTEND_SET){
      extendSet();
    }
  }
  private void makeNewSet(){
    TreeMap<Long, Diagram> tm = this.tileBuffer.getMap();
    if (!tm.isEmpty()) {
      tm.size();
      int w = tm.size() * this.tileWidth;
      int h = this.tileHeight;
      BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      Graphics g = img.getGraphics();

      int counter = 0;
      for (Long key : tm.keySet()) {
        g.drawImage(tm.get(key).getBufferedImage(), counter * this.tileWidth, 0, null);
        counter++;
      }
      Date startDate = tm.firstEntry().getValue().getStartDate();
      Date endDate = tm.lastEntry().getValue().getEndDate();

      this.diagram = new Diagram(img, startDate, endDate, this.zoomLevel);
      updateImage();
    }
  }

  private void extendSet(){

    if (!this.tileBuffer.getMap().isEmpty()) {
      Map.Entry<Long, Diagram> te = this.tileBuffer.getMap().firstEntry();
      if (te.getValue().getStartDate().getTime() < this.diagram.getStartDate().getTime() || te.getValue().getEndDate().getTime() > this.diagram.getEndDate().getTime()) {
        int w = this.diagram.getBufferedImage().getWidth();
        int h = this.tileHeight;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        Date startDate;
        Date endDate;

        if (this.sideToExpand == LEFT) {
          startDate = te.getValue().getStartDate();
          endDate = TimeUtils.addTime(this.diagram.getEndDate(), -Image.pixelToTime(this.tileWidth, this.zoomLevel));
          g.drawImage(te.getValue().getBufferedImage(), 0, 0, null);
          g.drawImage(this.diagram.getBufferedImage(), this.tileWidth, 0, null);
        } else {
          startDate = TimeUtils.addTime(this.diagram.getStartDate(), Image.pixelToTime(this.tileWidth, this.zoomLevel));
          endDate = te.getValue().getEndDate();

          g.drawImage(this.diagram.getBufferedImage(), -this.tileWidth, 0, null);
          g.drawImage(te.getValue().getBufferedImage(), w - this.tileWidth, 0, null);
        }


        this.diagram = new Diagram(img, startDate, endDate, this.zoomLevel);
        updateImage();
      }
    }
    this.image.loadingNext = false;
  }

  private void updateImage(){
    this.image.updateImage(this);
  }

  public Diagram getDiagram(){
    return this.diagram;
  }

  public int getCallType(){
    return this.callType;
  }

  public int getTileWidth(){
    return this.tileWidth;
  }

  public int getTileHeight(){
    return this.tileHeight;
  }

  public int getSideToExpand(){
    return this.sideToExpand;
  }

  public synchronized byte[] getBytes(URL url){
    byte[] bytes = new byte[0];
    try {
      InputStream is = url.openStream();
      bytes = sun.misc.IOUtils.readFully(is, -1, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;
  }

}
