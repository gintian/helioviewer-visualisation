package timelines.gui.variant4;

import javax.swing.*;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class TimeLinesViewer {

  private static final String serverBaseURLStr = "http://www.myserver.ch";

  public static void main(String[] args){
    ImageLoader iL = new ImageLoader(serverBaseURLStr);
    Window tLVWindow = new Window(iL);
    iL.addObserver(tLVWindow);
  }
}
