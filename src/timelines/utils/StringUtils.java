package timelines.utils;

import java.util.Map;

/**
 * Class containing general utility methods for handling Strings
 */
public class StringUtils {
  /**
   * Replace the keys (written as: "{key}") with the value in format string with params map.
   *
   * @param format string to format
   * @param params map with the replacements.
   * @return formatted string.
   */
  public static String format(String format, Map<String, String> params) {
    String formatted = new String(format);
    for (Map.Entry<String, String> e : params.entrySet()) {
      formatted = formatted.replaceAll("\\{" + e.getKey() + "\\}", e.getValue());
    }
    return formatted;
  }
}
