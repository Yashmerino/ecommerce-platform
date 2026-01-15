package com.yashmerino.ecommerce.selenium.it.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Class for test properties.
 */
@Slf4j
public class TestProperties {

    /**
     * File that stores properties.
     */
    private static final String PROPERTIES_FILE = "src/test/resources/it-test.properties";

    /**
     * Properties.
     */
    private static final Properties properties = new Properties();

    /**
     * Database's url property name.
     */
    public static final String DB_URL = "db.url";

    /**
     * Database's username property name.
     */
    public static final String DB_USERNAME = "db.username";

    /**
     * Database's password property name.
     */
    public static final String DB_PASSWORD = "db.password";

    static {
        try {
            properties.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Test properties couldn't be read: ", e);
            }
        }
    }

    /**
     * Returns the property.
     *
     * @param propertyName is the property's name.
     * @return The property value.
     */
    public static String getProperty(final String propertyName) {
        return properties.getProperty(propertyName);
    }
}
