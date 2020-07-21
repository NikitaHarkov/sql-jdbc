package com.foxminded.school;

import com.foxminded.school.dao.DataSource;
import com.foxminded.school.data.Data;
import com.foxminded.school.data.DataGenerator;
import com.foxminded.school.service.*;
import com.foxminded.school.ui.UserInterface;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;


public class SqlApplication {
    private static final Logger log = Logger.getLogger(SqlApplication.class.getName());

    public static void main(String[] args) {
        try {
            DataSource dataSource = PropertyParser.getConnection("connection.properties");
            String createTablesScript = PathReader.getFilePath("create_tables.sql");
            try {
                Class.forName("org.postgresql.Driver");
                Connection connection = dataSource.getConnection();
                System.out.println("Connection established......");
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                scriptRunner.runScript(new BufferedReader(new FileReader(createTablesScript)));

                Data data = new Data();
                DataGenerator.generateTestData(data, dataSource);

                UserInterface userInterface = new UserInterface(dataSource);
                userInterface.runInterface();
            } catch (SQLException | ClassNotFoundException ex) {
                log.throwing("SqlApplication", "main", ex);
                System.out.println("Connection failed..." + ex);
            }
        } catch (IOException ex) {
            log.throwing("SqlApplication", "main", ex);
            System.out.println("File not found!" + ex);
        }
    }
}
