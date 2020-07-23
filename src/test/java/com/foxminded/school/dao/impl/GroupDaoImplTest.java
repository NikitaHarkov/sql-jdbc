package com.foxminded.school.dao.impl;

import com.foxminded.school.exception.DAOException;
import com.foxminded.school.dao.DataSource;
import com.foxminded.school.dao.GroupDao;
import com.foxminded.school.dao.StudentDao;
import com.foxminded.school.domain.Group;
import com.foxminded.school.domain.Student;
import com.foxminded.school.service.PathReader;
import com.foxminded.school.service.PropertyParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupDaoImplTest {
    private static final String DB_PROPERTIES = "test_connection.properties";
    private static final String CREATE_TABLES_SCRIPT = "create_test_tables.sql";
    GroupDao groupDao;
    StudentDao studentDao;

    @BeforeEach
    void setConnection() {
        try {
            String createTablesScript = PathReader.getFilePath(CREATE_TABLES_SCRIPT);
            try {
                DataSource dataSource = PropertyParser.getConnectionProperties(DB_PROPERTIES);
                PropertyParser.createTablesInDatabase(dataSource, createTablesScript);
                groupDao = new GroupDaoImpl(dataSource);
                studentDao = new StudentDaoImpl(dataSource);
            } catch (DAOException ex) {
                System.out.println("Connection failed...\n" + ex);
            } catch (ClassNotFoundException ex) {
                System.out.println("Cannot load Database Driver\n" + ex);
            }
        } catch (IOException ex) {
            System.out.println("File not found!\n" + ex);
        }
    }

    @Test
    void insertGroups_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> groupDao.insert(null));
    }

    @Test
    void insert_ShouldAddNothing_WhenGivenEmptyList() throws DAOException {
        List<Group> expected = new ArrayList<>();
        groupDao.insert(expected);
        List<Group> actual = groupDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void insert_ShouldAddGroupsWithCorrectId_WhenGivenDuplicatedId() throws DAOException {
        List<Group> expected = Arrays.asList(
                new Group(1, "AS-15"),
                new Group(1, "DC-28")
        );
        groupDao.insert(expected);
        expected.get(1).setId(2);
        List<Group> actual = groupDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void insert_ShouldAddToDatabaseGroups_WhenGivenGroups() throws DAOException {
        List<Group> expected = Arrays.asList(
                new Group(1, "AS-15"),
                new Group(2, "DC-28")
        );
        groupDao.insert(expected);
        List<Group> actual = groupDao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getByStudentsCount_ShouldReturnEmptyList_WhenTableDoNotContainGroups() throws DAOException {
        List<Group> expected = new ArrayList<>();
        List<Group> actual = groupDao.getByStudentsCount(0);
        assertEquals(expected, actual);
    }

    @Test
    void getByStudentsCount_ShouldReturnGroupsWithLessOrEqualsStudentCount_WhenCountIsPassed() throws DAOException {
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
        groupDao.insert(groups);
        studentDao.insert(students);
        List<Group> expected = Arrays.asList(
                groups.get(0),
                groups.get(2)
        );
        expected.get(0).setStudentsCount(1);
        expected.get(1).setStudentsCount(1);
        List<Group> actual = groupDao.getByStudentsCount(1);
        assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
    }
}