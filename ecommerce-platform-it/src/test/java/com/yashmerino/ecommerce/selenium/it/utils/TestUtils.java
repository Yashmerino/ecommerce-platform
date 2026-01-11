package com.yashmerino.ecommerce.selenium.it.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static com.yashmerino.ecommerce.selenium.it.utils.TestProperties.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test utils.
 */
@Slf4j
public class TestUtils {
    /**
     * SQL Script file that cleans the database.
     */
    public final static String SQL_CLEAN_SCRIPT_FILE = "src/test/resources/sql/clean.sql";

    /**
     * The pattern used to assemble the URL for the application database.
     */
    private static final String DB_URL_PATTERN = "%s?user=%s&password=%s";

    /**
     * @return The database URL to connect to.
     */
    private static String getDatabaseURL() {
        return String.format(DB_URL_PATTERN, getProperty(DB_URL), getProperty(DB_USERNAME), getProperty(DB_PASSWORD));
    }

    /**
     * Executes a SQL Script.
     *
     * @param scriptFile is the SQL script's path.
     */
    public static void executeSQLScript(final String scriptFile) {
        final String databaseURL = getDatabaseURL();

        final File file = new File(scriptFile);
        assertTrue(file.exists(), "SQL file should exist: " + scriptFile);

        try (Connection connection = DriverManager.getConnection(databaseURL);) {
            ScriptUtils.executeSqlScript(connection, new FileSystemResource(file));
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("SQL Script couldn't be executed: ", e);
            }
        }
    }
}
