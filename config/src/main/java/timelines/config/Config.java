package timelines.config;

import java.util.Date;

/**
 * Used to access configuration values specified in the config.properties file
 */
public class Config {

    public static String getDbPath() {
        // return "/mnt/ext-storage/res_";
        return "..\\timelines\\res_";
    }

    public static Date getStartDate() {
        return new Date(Long.valueOf(981020744966L));
    }
}
