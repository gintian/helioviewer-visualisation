package timelines.gui;

//import com.sun.istack.internal.NotNull;
import timelines.api.APIImageMetadata;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 05.12.2015.
 */
public class Diagram {
    private BufferedImage bufferedImage;
    private APIImageMetadata metadata;

    public Diagram(BufferedImage bufferedImage, APIImageMetadata metadata) {
        this.bufferedImage = bufferedImage;
        this.metadata = metadata;
    }

    public Diagram(BufferedImage bufferedImage, Date startDate, Date endDate, int zoomLevel) {
        this.bufferedImage = bufferedImage;
        this.metadata = new APIImageMetadata(startDate, endDate, zoomLevel);
    }

    public Diagram(ImageInputStream imageInputStream) throws IOException {
        this.metadata = new APIImageMetadata(imageInputStream);
        this.bufferedImage = ImageIO.read(imageInputStream);
    }

    public Diagram(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ImageInputStream iis = ImageIO.createImageInputStream(bais);
        this.metadata = new APIImageMetadata(iis);

        bais = new ByteArrayInputStream(bytes);
        this.bufferedImage = ImageIO.read(bais);
    }

    //    @NotNull
    public BufferedImage getBufferedImage() {
        return this.bufferedImage;
    }

    //    @NotNull
    public APIImageMetadata getAPIImageMetadata() {
        return this.metadata;
    }

    //    @NotNull
    public Date getStartDate() {
        return this.metadata.getDateFrom();
    }

    //    @NotNull
    public Date getEndDate() {
        return this.metadata.getDateTo();
    }

    public int getZoomLevel() {
        return this.metadata.getZoomLevel();
    }
}
