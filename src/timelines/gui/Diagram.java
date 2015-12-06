package timelines.gui;

import com.sun.istack.internal.NotNull;
import timelines.api.APIImageMetadata;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Tobi on 05.12.2015.
 */
public class Diagram {
    private BufferedImage bufferedImage;
    private APIImageMetadata metadata;

    public Diagram(BufferedImage bufferedImage, APIImageMetadata metadata){
        this.bufferedImage = bufferedImage;
        this.metadata = metadata;
    }
    public Diagram(BufferedImage bufferedImage, Date startDate, Date endDate, int zoomLevel){
        this.bufferedImage = bufferedImage;
        this.metadata = new APIImageMetadata(startDate, endDate, zoomLevel);
    }

    public Diagram(ImageInputStream imageInputStream) throws IOException{
        this.metadata = new APIImageMetadata(imageInputStream);
        imageInputStream.reset();
        this.bufferedImage = ImageIO.read(imageInputStream);
        /*try {
            this.bufferedImage = ImageIO.read(new URL("http://localhost:8080/api?dateFrom=1981-07-01:00:00:00&zoomLevel=1"));
        }catch (MalformedURLException e){}*/
    }

    @NotNull
    public BufferedImage getBufferedImage(){
        return this.bufferedImage;
    }
    @NotNull
    public APIImageMetadata getAPIImageMetadata(){
        return this.metadata;
    }
    @NotNull
    public Date getStartDate(){
        return this.metadata.getDateFrom();
    }
    @NotNull
    public Date getEndDate(){
        return this.metadata.getDateTo();
    }
    public int getZoomLevel(){
        return this.metadata.getZoomLevel();
    }
}
