package timelines.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
            System.out.println("thread url: "+ url);//TODO:remove
            Diagram diagram = new Diagram(this.imageLoader.getBytes(url));
            System.out.println("thread url: "+ url + " ## thread diagram: " + diagram.getStartDate() + " | " + diagram.getEndDate() + " | " + diagram.getZoomLevel());//TODO:remove
            this.imageLoader.tileBuffer.addToDiagramBuffer(diagram);
        }catch (IOException e){

        }
    }
}
