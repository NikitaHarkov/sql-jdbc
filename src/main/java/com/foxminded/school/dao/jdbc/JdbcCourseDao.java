package com.foxminded.school.dao.jdbc;

import com.foxminded.school.dao.ConnectionProvider;
import com.foxminded.school.dao.CourseDao;
import com.foxminded.school.domain.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCourseDao implements CourseDao {
    private static final String INSERT = "INSERT INTO courses (course_id, course_name, course_description) VALUES (?, ?, ?)";
    private static final String GET_ALL = "SELECT * FROM courses";
    private static final String GET_BY_STUDENT_ID =
            "SELECT courses.course_id, courses.course_name, courses.course_description " +
                    "FROM students_courses " +
                    "INNER JOIN courses " +
                    "ON students_courses.course_id = courses.course_id " +
                    "WHERE students_courses.student_id = ?";

    private final ConnectionProvider connectionProvider;

    public JdbcCourseDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void insertCourses(List<Course> courses) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {
            for (Course course : courses) {
                preparedStatement.setInt(1, course.getId());
                preparedStatement.setString(2, course.getName());
                preparedStatement.setString(3, course.getDescription());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> result = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            result = processResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Course> getByStudentId(int studentId) {
        List<Course> result = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_STUDENT_ID)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                result = processResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Course> processResultSet(ResultSet resultSet) throws SQLException {
        List<Course> result = new ArrayList<>();
        while (resultSet.next()) {
            Course course = new Course();
            course.setId(resultSet.getInt(1));
            course.setName(resultSet.getString(2));
            course.setDescription(resultSet.getString(3));
            result.add(course);
        }
        return result;
    }
}
