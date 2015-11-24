package timelines.utils;

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

  public static Map<String, String> readCustomData(BufferedImage img, ArrayList<String> keys) throws IOException{
    ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();

    imageReader.setInput(img); // ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)), true);

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

}
