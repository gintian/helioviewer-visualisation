package timelines.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.Properties;

/**
 * Used to access configuration values specified in the config.properties file
 */
public class Config {

    private static Properties properties;
    private static String propertiesFile = "../config/src/main/resources/timelines/config/config.properties";

    static {
        try {
            InputStream in = new FileInputStream(propertiesFile);
            properties = new Properties();

            properties.load(in);
        } catch (Exception e) {
            Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
            System.out.println("Could not find the config file: '" + propertiesFile);
            System.out.println("Current path is " + path.toString());
            e.printStackTrace();
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
