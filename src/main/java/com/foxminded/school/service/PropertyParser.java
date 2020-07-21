package com.foxminded.school.service;

import com.foxminded.school.dao.DataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyParser {
    public static DataSource getConnection(String propertiesFile) throws IOException {
        FileInputStream fileStream = new FileInputStream(PathReader.getFilePath(propertiesFile));
        Properties properties = new Properties();
        properties.load(fileStream);
        String url = (String) properties.get("db.conn.url");
        String username = (String) properties.get("db.username");
        String password = (String) properties.get("db.password");
        return new DataSource(url, username, password);
    }
}
