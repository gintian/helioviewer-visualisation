package timelines.performanceTest;

import timelines.api.APIImageMetadata;
import timelines.gui.Diagram;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Tobi on 06.01.2016.
 */
public class DownloadTest {
    public static void main(String[] args) {
        try {
            URL url = new URL(
                    "http://server1125.cs.technik.fhnw.ch:8080/timelines/api?zoomLevel=7&dateFrom=1991-06-29:12:43:44");
            long start = System.nanoTime();
            BufferedImage img = ImageIO.read(url);
            System.out.println("buffered image created: " + (System.nanoTime() - start) + " ns");

            start = System.nanoTime();
            InputStream is = url.openStream();
            System.out.println("InputStream created: " + (System.nanoTime() - start) + " ns");

            start = System.nanoTime();

            ImageInputStream iis = ImageIO.createImageInputStream(is);
            System.out.println("ImageInputStream: " + (System.nanoTime() - start) + " ns");

            start = System.nanoTime();
            APIImageMetadata metadata = new APIImageMetadata(iis);
            System.out.println("metadata generated: " + (System.nanoTime() - start) + " ns");

            start = System.nanoTime();
            new Diagram(img, metadata);
            System.out.println("diagram generated: " + (System.nanoTime() - start) + " ns");

        } catch (IOException e) {
            System.out.println("hello");
        }
    }
}
