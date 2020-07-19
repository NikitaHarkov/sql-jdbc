package com.foxminded.school.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
    private final String url;
    private final String userName;
    private final String password;

    public ConnectionProvider(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,userName,password);
    }
}
