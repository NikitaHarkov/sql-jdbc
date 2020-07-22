package com.foxminded.school.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class PathReader {
    public static String getFilePath(String file) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(file);
        if (url == null) {
            throw new FileNotFoundException("File not found: '" + file + "'");
        }
        return url.getPath();
    }
}
