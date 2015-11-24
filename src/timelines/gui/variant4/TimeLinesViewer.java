package timelines.gui.variant4;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.util.Date;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class TimeLinesViewer {

  private static final String serverBaseURLStr = "http://localhost:8080";
  private static final Date date = new Date();

  public static void main(String[] args){
    ImageLoader iL = new ImageLoader(serverBaseURLStr);
    Window tLVWindow = new Window(iL, date);
    iL.addObserver(tLVWindow);
  }
}
