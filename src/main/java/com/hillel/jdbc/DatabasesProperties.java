package com.hillel.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabasesProperties {

    public static Properties load() {
        InputStream inputStream =
                DatabasesProperties.class.getResourceAsStream("/db.properties");

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
