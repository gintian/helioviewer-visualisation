package timelines.config;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

/**
 * Used to access configuration values specified in the config.properties file
 */
public class Config {

    private static Properties properties;
    private static String propertiesFile = "config/config.properties";

    {

        InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesFile);
        properties = new Properties();

        if (in != null) {
            try {
                properties.load(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not find the config file: '" + propertiesFile);
        }
    }

    public static String getDbPath() {
        return properties.getProperty("db_path");
    }

    public static String getCachePath() {
        return properties.getProperty("cache_path");
    }

    public static Date getStartDate() {
        return new Date(Long.valueOf(properties.getProperty("start_date")));
    }

}
