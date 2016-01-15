package timelines.gui;

import java.io.IOException;
import java.net.URL;

/**
 * Project i4ds05-visualisieren-von-timelines
 * Created by Tobias Kohler on 05.12.2015.
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
            byte[] bytes = this.imageLoader.getBytes(url);
            Diagram diagram;
            if (bytes.length!=0) {
                diagram = new Diagram(bytes);
                this.imageLoader.tileBuffer.addToDiagramBuffer(diagram);
            }else{
                this.imageLoader.tileBuffer.addEmpty();
            }
        }catch (IOException e){

        }
    }
}
