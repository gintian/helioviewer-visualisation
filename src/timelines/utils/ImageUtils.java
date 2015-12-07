package timelines.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
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

public class ImageUtils {

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

  public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
    Date date = new Date();
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

    System.out.println("scaling done in " + new Date(new Date().getTime() - date.getTime()).getTime() + "ms");
    return ret;
  }

  public static BufferedImage multiplyAlpha(int val, BufferedImage img) {
    Date date = new Date();
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
    System.out.println("increased transparency in " + new Date(new Date().getTime() - date.getTime()).getTime() + "ms");
    return img;
  }

}
