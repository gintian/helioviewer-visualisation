package timelines.gui;

import timelines.utils.TimeUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 12.10.2015.
 */
public class TimeLinesViewer {

  //private static final Date date = new Date(); //TODO: ... when done testing
  private static Date date = new Date();

  public static void main(String[] args){
    try {
      date = TimeUtils.fromString("1981-07-01:00:00:00", "yyyy-MM-dd:HH:mm:ss");
    }catch (ParseException e){
      //TODO: remove this try catch when done testing
    }
    Window tLVWindow = new Window(date);
  }
}
