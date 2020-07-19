package com.foxminded.school;

import com.foxminded.school.dao.ConnectionProvider;
import com.foxminded.school.service.TestData;
import com.foxminded.school.service.TestDataGenerator;
import com.foxminded.school.service.UserInterface;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SqlApplication {
    private final static String dbUrl = "jdbc:postgresql://localhost/school";

    public static void main(String[] args) {
        try {
            String createTablesScript = getFilePath("create_tables.sql");
            try {
                Connection connection = DriverManager.getConnection(dbUrl, "director", "school");
                System.out.println("Connection established......");
                ScriptRunner scriptRunner = new ScriptRunner(connection);
                scriptRunner.runScript(new BufferedReader(new FileReader(createTablesScript)));

                TestData testData = new TestData();
                ConnectionProvider connectionProvider = new ConnectionProvider(dbUrl, "director", "school");
                TestDataGenerator.generateTestData(testData, connectionProvider);

                UserInterface userInterface = new UserInterface(connectionProvider);
                userInterface.runInterface();
            } catch (SQLException ex) {
                System.out.println("Connection failed \n" + ex);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("There is no such file\n" + ex);
        }

    }

    private static String getFilePath(String file) throws FileNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(file);
        if (url == null) {
            throw new FileNotFoundException("File not found!");
        }
        return url.getPath();
    }
}
