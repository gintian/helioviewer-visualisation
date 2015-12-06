package timelines.gui;

import timelines.api.APIImageMetadata;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

/**
 * Created by Tobi on 05.12.2015.
 */
public class ImageRunnable implements Runnable {
    private URL url;
    private ImageLoader imageLoader;

    public ImageRunnable(ImageLoader imageLoader, URL url){
        this.imageLoader = imageLoader;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            InputStream is = this.url.openStream();
            ImageInputStream iis = ImageIO.createImageInputStream(is);
            Diagram diagram = new Diagram(iis);
            this.imageLoader.tileBuffer.addToDiagramBuffer(diagram);
        }catch (IOException e){

        }
    }
}
