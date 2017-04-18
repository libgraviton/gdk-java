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

    /**
     * Loads the Properties in the following order (if a property entry ia already loaded, it will be overridden with the new value).
     * 1.) Default Properties
     *     Minimal needed properties for the gdk
     *
     * 2.) Overwrite Properties
     *     Usually projects that make use of the gdk library will define these properties.
     *
     * 3.) System Properties
     *     Projects that use the gdk library could be deployed to several environments that require different property values.
     *     The easiest way at this point is to just redefine those properties as system properties.
     *
     * @return loaded Properties
     * @throws IOException whenever the properties from a given path could not be loaded
     */
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

        properties.putAll(System.getProperties());

        return properties;
    }

    public static String load(String key) {
        try {
            Properties properties = load();
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load properties files.", e);
        }
    }
}
