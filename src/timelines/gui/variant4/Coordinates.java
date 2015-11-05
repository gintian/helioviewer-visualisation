package timelines.gui.variant4;

import java.awt.*;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 05.11.2015.
 */
public class Coordinates extends Point {

  public Coordinates(int x, int y){
    super(x,y);
  }
  public boolean subtract(Point subtrahend){
    this.x -= subtrahend.x;
    this.y -= subtrahend.y;
    return true;
  }
  public boolean add(Point summand){
    this.x += summand.x;
    this.y += summand.y;
    return true;
  }
  public boolean multiply(double factor){
    this.x *= factor;
    this.y *= factor;
    return true;
  }
  public Coordinates diff(Coordinates p){
    return new Coordinates(this.x-p.x,this.y-p.y);
  }
}
