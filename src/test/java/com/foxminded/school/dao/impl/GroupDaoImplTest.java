package com.foxminded.school.dao.impl;

import com.foxminded.school.exception.DAOException;
import com.foxminded.school.dao.DataSource;
import com.foxminded.school.dao.GroupDao;
import com.foxminded.school.dao.StudentDao;
import com.foxminded.school.domain.Group;
import com.foxminded.school.domain.Student;
import com.foxminded.school.service.PathReader;
import com.foxminded.school.service.PropertyParser;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupDaoImplTest {
    DataSource dataSource;
    GroupDao groupDao;
    StudentDao studentDao;

    @BeforeEach
    void setConnection() throws IOException, ClassNotFoundException {
        dataSource = PropertyParser.getConnectionProperties("test_connection.properties");
        String script = PathReader.getFilePath("create_test_tables.sql");
        groupDao = new GroupDaoImpl(dataSource);
        studentDao = new StudentDaoImpl(dataSource);
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = dataSource.getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader(script)));
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Test connection failed: " + e.getMessage());
        }
    }

    @Test
    void insertGroups_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> groupDao.insertGroups(null));
    }

    @Test
    void insertGroups_ShouldAddNothing_WhenGivenEmptyList() throws DAOException {
        List<Group> expected = new ArrayList<>();
        groupDao.insertGroups(expected);
        List<Group> actual = groupDao.getGroups();
        assertEquals(expected, actual);
    }

    @Test
    void insertGroups_ShouldAddGroupsWithCorrectId_WhenGivenDuplicatedId() throws DAOException {
        List<Group> expected = Arrays.asList(
                new Group(1, "AS-15"),
                new Group(1, "DC-28")
        );
        groupDao.insertGroups(expected);
        expected.get(1).setId(2);
        List<Group> actual = groupDao.getGroups();
        assertEquals(expected, actual);
    }

    @Test
    void insertGroups_ShouldAddToDatabaseGroups_WhenGivenGroups() throws DAOException {
        List<Group> expected = Arrays.asList(
                new Group(1, "AS-15"),
                new Group(2, "DC-28")
        );
        groupDao.insertGroups(expected);
        List<Group> actual = groupDao.getGroups();
        assertEquals(expected, actual);
    }

    @Test
    void getGroupsByStudentsCount_ShouldReturnEmptyList_WhenTableDoNotContainGroups() throws DAOException {
        List<Group> expected = new ArrayList<>();
        List<Group> actual = groupDao.getGroupsByStudentsCount(0);
        assertEquals(expected, actual);
    }

    @Test
    void getGroupsByStudentsCount_ShouldReturnGroupsWithLessOrEqualsStudentCount_WhenCountIsPassed() throws DAOException {
        List<Group> groups = Arrays.asList(
                new Group(1, "AS-15"),
                new Group(2, "DC-28"),
                new Group(3, "SW-11")
        );
        List<Student> students = Arrays.asList(
                new Student(1, 1, "Name", "Somebody"),
                new Student(2, 2, "My", "Last"),
                new Student(3, 2, "Position", "Hello"),
                new Student(4, 3, "Sick", "Liquir")
        );
        groupDao.insertGroups(groups);
        studentDao.insertStudents(students);
        List<Group> expected = Arrays.asList(
                groups.get(0),
                groups.get(2)
        );
        expected.get(0).setStudentsCount(1);
        expected.get(1).setStudentsCount(1);
        List<Group> actual = groupDao.getGroupsByStudentsCount(1);
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
    }
}