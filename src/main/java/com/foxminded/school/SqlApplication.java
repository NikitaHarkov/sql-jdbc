package com.foxminded.school;

import com.foxminded.school.dao.DataSource;
import com.foxminded.school.data.Data;
import com.foxminded.school.data.DataGenerator;
import com.foxminded.school.exception.DAOException;
import com.foxminded.school.service.*;
import com.foxminded.school.ui.UserInterface;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;


public class SqlApplication {
    private static final Logger log = Logger.getLogger(SqlApplication.class.getName());
    private static final String DB_PROPERTIES = "connection.properties";
    private static final String CREATE_TABLES_SCRIPT = "create_tables.sql";

    public static void main(String[] args) {
        try {
            String createTablesScript = PathReader.getFilePath(CREATE_TABLES_SCRIPT);
            try {
                DataSource dataSource = PropertyParser.getConnectionProperties(DB_PROPERTIES);
                PropertyParser.createTablesInDatabase(dataSource, createTablesScript);

                Data data = new Data();
                DataGenerator.generateTestData(data, dataSource);

                UserInterface userInterface = new UserInterface(dataSource);
                userInterface.runInterface();
            } catch (DAOException ex) {
                log.throwing("SqlApplication", "main", ex);
                System.out.println("Connection failed...\n" + ex);
            } catch (ClassNotFoundException ex) {
                log.throwing("SqlApplication", "main", ex);
                System.out.println("Cannot load Database Driver\n" + ex);
            }
        } catch (IOException ex) {
            log.throwing("SqlApplication", "main", ex);
            System.out.println("File not found!\n" + ex);
        }
    }
}

