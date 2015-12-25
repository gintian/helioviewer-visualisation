package timelines.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.png.PNGMetadata;

/**
 * Class containing general utility methods for handling image data
 */
public class ImageUtils {

  /**
   * Writes an image with metadata information
   * @param out the OutputStream to write the image to
   * @param buffImg the image to be written
   * @param customData a key-value mapping of the metadata information to be written
   * @throws Exception if the writing goes wrong
   */
  public static void writeWithCustomData(OutputStream out, BufferedImage buffImg, Map<String, String> customData) throws Exception {
    ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

    ImageWriteParam writeParam = writer.getDefaultWriteParam();
    ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);

    //adding metadata
    IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
    IIOMetadataNode text = new IIOMetadataNode("tEXt");

    for (Entry<String, String> entry : customData.entrySet()) {

      IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
      textEntry.setAttribute("keyword", entry.getKey());
      textEntry.setAttribute("value", entry.getValue());
      text.appendChild(textEntry);
    }

    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
    root.appendChild(text);

    metadata.mergeTree("javax_imageio_png_1.0", root);

    //writing the data
    ImageOutputStream stream = ImageIO.createImageOutputStream(out);
    writer.setOutput(stream);
    writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
    stream.close();
  }

  /**
   * Reads the metadata information from an image
   * @param iis the stream to read the image from
   * @param keys the metadata keys for which to read the values
   * @return a key-value mapping of the metadata found
   * @throws IOException if an error occurs while reading
   */
  public static Map<String, String> readCustomData(ImageInputStream iis, ArrayList<String> keys) throws IOException{
    ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();

    imageReader.setInput(iis); // ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)), true);

    // read metadata of first image
    IIOMetadata metadata = imageReader.getImageMetadata(0);

    //this cast helps getting the contents
    PNGMetadata pngmeta = (PNGMetadata) metadata;
    NodeList childNodes = pngmeta.getStandardTextNode().getChildNodes();

    Map<String, String> result = new HashMap<String, String>();

    for (int i = 0; i < childNodes.getLength(); i++) {
        Node node = childNodes.item(i);
        String keyword = node.getAttributes().getNamedItem("keyword").getNodeValue();
        String value = node.getAttributes().getNamedItem("value").getNodeValue();
        if(keys.contains(keyword)){
          result.put(keyword, value);
        }
    }
    return result;
  }

  /**
   * Convenience method that returns a scaled instance of the
   * provided {@code BufferedImage}.
   *
   * @author Chris Campbell
   * (https://web.archive.org/web/20080516181120/http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html)
   *
   * @param img the original image to be scaled
   * @param targetWidth the desired width of the scaled instance,
   *    in pixels
   * @param targetHeight the desired height of the scaled instance,
   *    in pixels
   * @param hint one of the rendering hints that corresponds to
   *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
   *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
   *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
   *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
   * @param higherQuality if true, this method will use a multi-step
   *    scaling technique that provides higher quality than the usual
   *    one-step technique (only useful in down-scaling cases, where
   *    {@code targetWidth} or {@code targetHeight} is
   *    smaller than the original dimensions, and generally only when
   *    the {@code BILINEAR} hint is specified)
   * @return a scaled version of the original {@code BufferedImage}
   */
  public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
    int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage ret = img;
    int w, h;
    if (higherQuality) {
      // Use multi-step technique: start with original size, then
      // scale down in multiple passes with drawImage()
      // until the target size is reached
      w = img.getWidth();
      h = img.getHeight();
    } else {
      // Use one-step technique: scale directly from original
      // size to target size with a single drawImage() call
      w = targetWidth;
      h = targetHeight;
    }

    do {
      if (higherQuality && w > targetWidth) {
        w /= 2;
        if (w < targetWidth) {
          w = targetWidth;
        }
      }

      if (higherQuality && h > targetHeight) {
        h /= 2;
        if (h < targetHeight) {
          h = targetHeight;
        }
      }

      BufferedImage tmp = new BufferedImage(w, h, type);
      Graphics2D g2 = tmp.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
      g2.drawImage(ret, 0, 0, w, h, null);
      g2.dispose();

      ret = tmp;
    } while (w != targetWidth || h != targetHeight);

    return ret;
  }

  /**
   * Multiplies the alpha value of an images pixels by the given value
   * @param val the value to multiply the alpha channel with
   * @param img the image to multiply the alpha channel on
   * @return the image with multiplied alpha channel values
   */
  public static BufferedImage multiplyAlpha(int val, BufferedImage img) {
    Color color;
    for(int x = 0; x < img.getWidth(); x++) {
      for(int y = 0; y < img.getHeight(); y++) {
        color = new Color(img.getRGB(x, y), true);
        if(color.getAlpha() != 0 && color.getAlpha() < 255) {
          color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(255, color.getAlpha() * val));
          img.setRGB(x, y, color.getRGB());
        }
      }
    }
    return img;
  }

}
