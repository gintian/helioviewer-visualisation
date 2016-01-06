package timelines.gui;

import timelines.api.APIImageMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Tobi on 05.12.2015.
 */
public class test2Runnable implements Runnable {
    private URL[] urls;
    private ImageLoader imageLoader;

    public test2Runnable(ImageLoader imageLoader, URL[] urls){
        this.imageLoader = imageLoader;
        this.urls = urls;
    }

    @Override
    public void run() {
        for(URL url : urls) {
            try {
                System.out.println("thread url: " + url);//TODO:remove
                long start = System.nanoTime();//TODO:remove
                BufferedImage img = ImageIO.read(url);
                System.out.println("thread url: " + url + "buff image: " + (System.nanoTime()-start));//TODO:remove
                start = System.nanoTime();
                APIImageMetadata metadata = new APIImageMetadata(ImageIO.createImageInputStream(url.openStream()));
                System.out.println("thread url: " + url + "metadata: " + (System.nanoTime()-start));//TODO:remove
                start = System.nanoTime();
                Diagram diagram = new Diagram(img,metadata);
                System.out.println("thread url: " + url + "make diagram: " + (System.nanoTime()-start));//TODO:remove
                System.out.println("thread url: " + url + " ## thread diagram: " + diagram.getStartDate() + " | " + diagram.getEndDate() + " | " + diagram.getZoomLevel());//TODO:remove
                this.imageLoader.tileBuffer.addToDiagramBuffer(diagram);
            } catch (IOException e) {

            }
        }
    }
}
