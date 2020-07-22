package com.foxminded.school.dao.impl;

import com.foxminded.school.dao.CourseDao;
import com.foxminded.school.exception.DAOException;
import com.foxminded.school.dao.DataSource;
import com.foxminded.school.dao.StudentDao;
import com.foxminded.school.domain.Course;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseDaoImplTest {
    DataSource dataSource;
    CourseDao courseDao;
    StudentDao studentDao;

    @BeforeEach
    void setConnection() throws IOException, ClassNotFoundException {
        dataSource = PropertyParser.getConnectionProperties("test_connection.properties");
        String script = PathReader.getFilePath("create_test_tables.sql");
        courseDao = new CourseDaoImpl(dataSource);
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
    void insertCourses_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> courseDao.insertCourses(null));
    }

    @Test
    void insertCourses_ShouldAddCoursesWithCorrectId_WhenGivenCoursesWithDuplicatedId() throws DAOException {
        List<Course> duplicatedId = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(1, "History", "This is History")
        );
        courseDao.insertCourses(duplicatedId);
        List<Course> actual = courseDao.getAllCourses();
        duplicatedId.get(1).setId(2);
        assertEquals(duplicatedId,actual);
    }

    @Test
    void insertCourses_ShouldAddToDatabaseCourses_WhenGivenCourses() throws DAOException {
        List<Course> expected = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History")
        );
        courseDao.insertCourses(expected);
        List<Course> actual = courseDao.getAllCourses();
        assertEquals(expected, actual);
    }

    @Test
    void getAllCourses_ShouldReturnEmptyList_WhenTableIsEmpty() throws DAOException {
        List<Course> expected = new ArrayList<>();
        List<Course> actual = courseDao.getAllCourses();
        assertEquals(expected, actual);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses_WhenThereAreFewCourses() throws DAOException {
        List<Course> expected = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        courseDao.insertCourses(expected);
        List<Course> actual = courseDao.getAllCourses();
        assertEquals(expected, actual);
    }

    @Test
    void getByStudentId_ShouldReturnEmptyList_WhenTableDoNotContainPassedId() throws DAOException {
        List<Course> expected = new ArrayList<>();
        List<Course> actual = courseDao.getByStudentId(0);
        assertEquals(expected, actual);
    }

    @Test
    void getByStudentId_ShouldReturnCourses_WhenIdIsPassed() throws DAOException {
        List<Course> courses = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        List<Student> students = Arrays.asList(
                new Student(1, 1, "Test", "Test"),
                new Student(2, 2, "Name", "Lastname")

        );
        studentDao.insertStudents(students);
        courseDao.insertCourses(courses);
        studentDao.assignToCourse(1, 3);
        List<Course> expected = Arrays.asList(courses.get(2));
        List<Course> actual = courseDao.getByStudentId(1);
        assertEquals(expected, actual);
    }
}
