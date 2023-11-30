package pl.beda.reactive.rest.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

    private final Properties properties;

    private ConfigUtils() {
        this.properties = readProperties();
    }

    public static ConfigUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ConfigUtils INSTANCE = new ConfigUtils();
    }

    public Properties getProperties() {
        return properties;
    }

    private Properties readProperties() {
        Properties properties = new Properties();
        try {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                try {
                    properties.load(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
