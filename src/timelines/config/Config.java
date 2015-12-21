package timelines.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

  private Properties properties;
  String propertiesFile = "config/config.properties";

  public Config() throws IOException {

    InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesFile);
    properties = new Properties();

    if (in != null) {
      properties.load(in);
    } else {
      throw new FileNotFoundException("Could not find the config file: '" + propertiesFile);
    }
  }

  public String getDbPath() {
    return properties.getProperty("db_path");
  }

  public String getCachePath() {
    return properties.getProperty("cache_path");
  }



}
