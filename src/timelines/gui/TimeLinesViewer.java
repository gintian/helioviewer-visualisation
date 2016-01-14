package timelines.gui;

import timelines.utils.TimeUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class TimeLinesViewer {

  private static Date date = new Date();

  public static void main(String[] args){
    try {
      date = TimeUtils.fromString("1991-07-01:00:00:00", "yyyy-MM-dd:HH:mm:ss");
    }catch (ParseException e){
      e.printStackTrace();
    }
    Window tLVWindow = new Window(date);
    tLVWindow.setResizable(false);
  }
}
