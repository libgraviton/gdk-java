package com.github.libgraviton.gdk.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loading properties (default as fallback, with the ability to overwrite values).
 */
public class PropertiesLoader {

    private static final String DEFAULT_PROPERTIES_PATH = "default-gdk.properties";

    private static final String OVERWRITE_PROPERTIES_PATH = "app.properties";

    public static Properties load() throws IOException {
        Properties properties = new Properties();

        try (InputStream defaultProperties = PropertiesLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_PATH)) {
            properties.load(defaultProperties);
        }

        try (InputStream overwriteProperties = PropertiesLoader.class.getClassLoader().getResourceAsStream(OVERWRITE_PROPERTIES_PATH)) {
            if (overwriteProperties != null) {
                properties.load(overwriteProperties);
            }
        }

        return properties;
    }
}
