package timelines.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Tobi on 05.12.2015.
 */
public class testRunnable implements Runnable {
    private URL[] urls;
    private ImageLoader imageLoader;

    public testRunnable(ImageLoader imageLoader, URL[] urls){
        this.imageLoader = imageLoader;
        this.urls = urls;
    }

    @Override
    public void run() {
        for(URL url : urls) {
            try {
                System.out.println("thread url: " + url);//TODO:remove
                InputStream is = url.openStream();
                byte[] bytes = sun.misc.IOUtils.readFully(is, -1, true);
                Diagram diagram = new Diagram(bytes);
                System.out.println("thread url: " + url + " ## thread diagram: " + diagram.getStartDate() + " | " + diagram.getEndDate() + " | " + diagram.getZoomLevel());//TODO:remove
                this.imageLoader.tileBuffer.addToDiagramBuffer(diagram);
            } catch (IOException e) {

            }
        }
    }
}
