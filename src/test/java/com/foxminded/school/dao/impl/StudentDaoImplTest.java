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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentDaoImplTest {
    private static final String DB_PROPERTIES = "test_connection.properties";
    private static final String CREATE_TABLES_SCRIPT = "create_test_tables.sql";
    CourseDao courseDao;
    StudentDao studentDao;

    @BeforeEach
    void setConnection() {
        try {
            String createTablesScript = PathReader.getFilePath(CREATE_TABLES_SCRIPT);
            try {
                DataSource dataSource = PropertyParser.getConnectionProperties(DB_PROPERTIES);
                PropertyParser.createTablesInDatabase(dataSource, createTablesScript);
                courseDao = new CourseDaoImpl(dataSource);
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
    void getAllStudents_ShouldReturnEmptyListOfStudents_WhenTableIsEmpty() throws DAOException {
        List<Student> expected = new ArrayList<>();
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents_WhenTableIsNotEmpty() throws DAOException {
        List<Student> expected = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        studentDao.insertMany(expected);
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void insertStudents_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> studentDao.insertMany(null));
    }

    @Test
    void insertStudents_ShouldAddStudentsWithCorrectId_WhenGivenStudentsWithDuplicatedId() throws DAOException {
        List<Student> expected = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(1, 2, "Henri", "Koljik"),
                new Student(1, 1, "Bob", "Sudon"),
                new Student(1, 3, "Tim", "Maraket")
        );
        studentDao.insertMany(expected);
        expected.get(1).setId(2);
        expected.get(2).setId(3);
        expected.get(3).setId(4);
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void insertStudents_ShouldAddStudentsToTable_WhenGivenStudentsList() throws DAOException {
        List<Student> expected = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        studentDao.insertMany(expected);
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void insertStudent_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> studentDao.insertOne(null));
    }

    @Test
    void insertStudent_ShouldAddStudentWithCorrectId_WhenGivenStudentWithSameId() throws DAOException {
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        Student duplicatedStudent = new Student(1, 1, "Merlin", "Monto");
        studentDao.insertOne(student);
        studentDao.insertOne(duplicatedStudent);
        List<Student> actual = studentDao.getAllStudents();
        List<Student> expected = Arrays.asList(student, duplicatedStudent);
        expected.get(1).setId(2);
        assertEquals(expected, actual);
    }

    @Test
    void insertStudent_ShouldAddStudentToTable_WhenGivenStudent() throws DAOException {
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        studentDao.insertOne(student);
        List<Student> actual = studentDao.getAllStudents();
        List<Student> expected = Arrays.asList(student);
        assertEquals(expected, actual);
    }

    @Test
    void deleteStudentById_ShouldNotRemoveAnyStudent_WhenGivenIdDoesNotExist() throws DAOException {
        List<Student> expected = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        studentDao.insertMany(expected);
        studentDao.deleteById(8);
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void deleteStudentById_ShouldRemoveStudentFromTable_WhenGivenStudentId() throws DAOException {
        List<Student> students = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        studentDao.insertMany(students);
        studentDao.deleteById(2);
        List<Student> expected = Arrays.asList(students.get(0), students.get(2), students.get(3));
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void getStudentsByCourseName_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> studentDao.getByCourseName(null));
    }

    @Test
    void getStudentsByCourseName_ShouldReturnEmptyList_WhenGivenEmptyString() throws DAOException {
        List<Course> courses = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        List<Student> students = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        courseDao.insertMany(courses);
        studentDao.insertMany(students);
        List<Student> expected = new ArrayList<>();
        List<Student> actual = studentDao.getByCourseName("");
        assertEquals(expected, actual);
    }

    @Test
    void getStudentsByCourseName_ShouldReturnStudentsWithCourse_WhenGivenNameOfTheCourse() throws DAOException {
        List<Course> courses = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        List<Student> students = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        courseDao.insertMany(courses);
        studentDao.insertMany(students);
        studentDao.assignToCourse(1, 2);
        studentDao.assignToCourse(2, 2);
        studentDao.assignToCourse(3, 1);
        List<Student> expected = Arrays.asList(students.get(0), students.get(1));
        List<Student> actual = studentDao.getByCourseName("History");
        assertEquals(expected, actual);
    }

    @Test
    void assignToCourses_ShouldThrowException_WhenGivenNull() {
        assertThrows(IllegalArgumentException.class, () -> studentDao.assignToCourses(null));
    }

    @Test
    void assignToCourses_ShouldNotAssignAnything_WhenGivenEmptyMap() throws DAOException {
        Map<Student, List<Course>> map = new HashMap<>();
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        studentDao.insertOne(student);
        studentDao.assignToCourses(map);
        List<Course> expected = new ArrayList<>();
        List<Course> actual = courseDao.getByStudentId(student.getId());
        assertEquals(expected, actual);
    }

    @Test
    void assignToCourses_ShouldAssignStudentToCourses_WhenGivenMapOfStudentAndListOfCourses() throws DAOException {
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        List<Course> expected = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        studentDao.insertOne(student);
        courseDao.insertMany(expected);
        Map<Student, List<Course>> map = Map.of(student, expected);
        studentDao.assignToCourses(map);
        List<Course> actual = courseDao.getByStudentId(student.getId());
        assertEquals(expected, actual);
    }

    @Test
    void assignToCourse_ShouldThrowException_WhenNoStudentWithPassedId() {
        assertThrows(DAOException.class, () -> studentDao.assignToCourse(1, 1));
    }

    @Test
    void assignToCourse_ShouldThrowException_WhenNoCourseWithPassedId() throws DAOException {
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        studentDao.insertOne(student);
        assertThrows(DAOException.class, () -> studentDao.assignToCourse(1, 1));
    }

    @Test
    void assignToCourse_ShouldThrowException_WhenNoStudentWithPassedIdEvenWhenCourseExists() throws DAOException {
        List<Course> courses = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        courseDao.insertMany(courses);
        assertThrows(DAOException.class, () -> studentDao.assignToCourse(1, 2));
    }

    @Test
    void assignToCourse_ShouldAssignStudentToCourse_WhenStudentsAndCoursesExists() throws DAOException {
        List<Course> courses = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        courseDao.insertMany(courses);
        studentDao.insertOne(student);
        studentDao.assignToCourse(1, 2);
        List<Course> expected = Arrays.asList(courses.get(1));
        List<Course> actual = courseDao.getByStudentId(student.getId());
        assertEquals(expected, actual);
    }

    @Test
    void deleteFromCourse_ShouldDoNothing_WhenStudentAndCourseWithPassedIdNotExists() throws DAOException {
        List<Course> expected = courseDao.getByStudentId(1);
        studentDao.deleteFromCourse(1, 1);
        List<Course> actual = courseDao.getByStudentId(1);
        assertEquals(expected, actual);
    }

    @Test
    void deleteFromCourse_ShouldDoNothing_WhenCourseWithPassedIdNotExists() throws DAOException {
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        studentDao.insertOne(student);
        List<Course> expected = courseDao.getByStudentId(student.getId());
        studentDao.deleteFromCourse(1, 1);
        List<Course> actual = courseDao.getByStudentId(student.getId());
        assertEquals(expected, actual);
    }

    @Test
    void deleteFromCourse_ShouldDeleteStudentFromCourse_WhenGivenStudentAndCourseId() throws DAOException {
        List<Course> courses = Arrays.asList(
                new Course(1, "Math", "This is mathematics"),
                new Course(2, "History", "This is History"),
                new Course(3, "Geography", "This is Geography")
        );
        Student student = new Student(1, 1, "Kolju", "Sudarshan");
        courseDao.insertMany(courses);
        studentDao.insertOne(student);
        Map<Student, List<Course>> map = Map.of(student, courses);
        studentDao.assignToCourses(map);
        studentDao.deleteFromCourse(student.getId(), 2);

        List<Course> expected = Arrays.asList(courses.get(0), courses.get(2));
        List<Course> actual = courseDao.getByStudentId(student.getId());
        assertEquals(expected, actual);
    }

    @Test
    void deleteStudentById_ShouldDoNothing_WhenNoStudentByPassedId() throws DAOException {
        List<Student> expected = studentDao.getAllStudents();
        studentDao.deleteById(1);
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }

    @Test
    void deleteStudentById_ShouldDeleteStudent_WhenGivenStudentId() throws DAOException {
        List<Student> students = Arrays.asList(
                new Student(1, 1, "Somebody", "Sudarshan"),
                new Student(2, 2, "Henri", "Koljik"),
                new Student(3, 1, "Bob", "Sudon"),
                new Student(4, 3, "Tim", "Maraket")
        );
        studentDao.insertMany(students);
        List<Student> expected = Arrays.asList(students.get(0), students.get(2), students.get(3));
        studentDao.deleteById(2);
        List<Student> actual = studentDao.getAllStudents();
        assertEquals(expected, actual);
    }
}
