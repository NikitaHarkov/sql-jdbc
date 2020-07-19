package com.foxminded.school.dao.jdbc;

import com.foxminded.school.dao.ConnectionProvider;
import com.foxminded.school.dao.GroupDao;
import com.foxminded.school.domain.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcGroupDao implements GroupDao {
    private static final String INSERT = "INSERT INTO groups (group_id, group_name) VALUES (?, ?)";
    private static final String GET_BY_STUDENTS_COUNT =
            "SELECT groups.group_name, COUNT(students.student_id) " +
                    "FROM groups " +
                    "LEFT JOIN students " +
                    "ON students.group_id = groups.group_id " +
                    "GROUP BY groups.group_id " +
                    "HAVING COUNT(*) <= ?";

    private final ConnectionProvider connectionProvider;

    public JdbcGroupDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void insertGroups(List<Group> groups) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {
            for (Group group : groups) {
                preparedStatement.setInt(1, group.getId());
                preparedStatement.setString(2, group.getName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Group> getGroupsByStudentsCount(int count) {
        List<Group> result = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_STUDENTS_COUNT)) {
            preparedStatement.setInt(1, count);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Group group = new Group();
                    group.setName(resultSet.getString(1));
                    group.setStudentsCount(resultSet.getInt(2));
                    result.add(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
