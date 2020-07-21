package com.foxminded.school.dao;

import java.sql.SQLException;

public class DAOException extends SQLException {
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
