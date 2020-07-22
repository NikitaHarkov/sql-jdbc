package com.foxminded.school.dao.impl;

import com.foxminded.school.exception.DAOException;
import com.foxminded.school.dao.DataSource;
import com.foxminded.school.dao.StudentDao;
import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StudentDaoImpl implements StudentDao {
    private static final Logger log = Logger.getLogger(StudentDaoImpl.class.getName());
    private static final String GET_ALL = "SELECT * FROM students";
    private static final String INSERT_STUDENTS_QUERY =
            "INSERT INTO students (group_id, first_name, last_name) " +
            "     VALUES (?, ?, ?)";
    private static final String DELETE = "DELETE FROM students WHERE student_id = ?";
    private static final String GET_BY_COURSE_NAME =
            "SELECT students.student_id, students.group_id, students.first_name, students.last_name " +
            "  FROM students_courses " +
            "       INNER JOIN students " +
            "       ON students_courses.student_id = students.student_id " +

            "       INNER  JOIN courses " +
            "       ON students_courses.course_id = courses.course_id " +
            " WHERE courses.course_name = ?";
    private static final String ASSIGN_TO_COURSE = "INSERT INTO students_courses (student_id, course_id) VALUES (?, ?)";
    private static final String DELETE_FROM_COURSE =
            "DELETE " +
            "  FROM students_courses " +
            " WHERE student_id = ? " +
            "   AND course_id = ?";

    private final DataSource dataSource;

    public StudentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Student> getAllStudents() throws DAOException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            return processResultSet(resultSet);
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "getAllStudents", e);
            throw new DAOException("Cannot run getAllStudents method", e);
        }
    }

    @Override
    public void insertStudents(List<Student> students) throws DAOException {
        if (students == null)
            throw new IllegalArgumentException("Null in not allowed");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_STUDENTS_QUERY)) {
            for (Student student : students) {
                statement.setInt(1, student.getGroupId());
                statement.setString(2, student.getFirstName());
                statement.setString(3, student.getLastName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "insertStudents", e);
            throw new DAOException("Error in insertStudents", e);

        }
    }

    @Override
    public void insertStudent(Student student) throws DAOException {
        if (student == null)
            throw new IllegalArgumentException("Null is not allowed");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_STUDENTS_QUERY)) {
            statement.setInt(1, student.getGroupId());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "insertStudent", e);
            throw new DAOException("Error in insertStudent", e);
        }
    }

    @Override
    public void deleteStudentById(int studentId) throws DAOException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, studentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "deleteStudentById", e);
            throw new DAOException("Error in deleteStudentById", e);
        }
    }

    @Override
    public List<Student> getStudentsByCourseName(String courseName) throws DAOException {
        if (courseName == null)
            throw new IllegalArgumentException("Null is now allowed");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_COURSE_NAME)) {
            statement.setString(1, courseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return processResultSet(resultSet);
            }
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "getStudentsByCourseName", e);
            throw new DAOException("Error in getStudentsByCourseName", e);
        }
    }

    @Override
    public void assignToCourses(Map<Student, List<Course>> map) throws DAOException {
        if (map == null)
            throw new IllegalArgumentException("Null is not allowed");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(ASSIGN_TO_COURSE)) {
            for (Map.Entry<Student, List<Course>> entry : map.entrySet()) {
                Student student = entry.getKey();
                for (Course course : entry.getValue()) {
                    statement.setInt(1, student.getId());
                    statement.setInt(2, course.getId());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "assignToCourses", e);
            throw new DAOException("Error in assignToCourses", e);
        }
    }

    @Override
    public void assignToCourse(int studentId, int courseId) throws DAOException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(ASSIGN_TO_COURSE)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "assignToCourse", e);
            throw new DAOException("Error in assignToCourse", e);
        }
    }

    @Override
    public void deleteFromCourse(int studentId, int courseId) throws DAOException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_FROM_COURSE)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.throwing("JdbcStudentDao", "deleteFromCourse", e);
            throw new DAOException("Error in deleteFromCourse", e);
        }
    }

    private List<Student> processResultSet(ResultSet resultSet) throws SQLException {
        List<Student> result = new ArrayList<>();
        while (resultSet.next()) {
            Student student = new Student();
            student.setId(resultSet.getInt(1));
            student.setGroupId(resultSet.getInt(2));
            student.setFirstName(resultSet.getString(3));
            student.setLastName(resultSet.getString(4));
            result.add(student);
        }
        return result;
    }
}
