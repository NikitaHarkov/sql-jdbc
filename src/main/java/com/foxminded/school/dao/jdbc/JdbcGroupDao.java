package com.foxminded.school.dao.jdbc;

import com.foxminded.school.dao.DAOException;
import com.foxminded.school.dao.DataSource;
import com.foxminded.school.dao.GroupDao;
import com.foxminded.school.domain.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class JdbcGroupDao implements GroupDao {
    private static final Logger log = Logger.getLogger(JdbcGroupDao.class.getName());
    private static final String INSERT = "INSERT INTO groups (group_name) VALUES (?)";
    private static final String GET_ALL = "SELECT * FROM groups";
    private static final String GET_BY_STUDENTS_COUNT =
            "SELECT groups.group_id, groups.group_name, COUNT(students.student_id) " +
                    "FROM groups " +
                    "LEFT JOIN students " +
                    "ON students.group_id = groups.group_id " +
                    "GROUP BY groups.group_id " +
                    "HAVING COUNT(*) <= ?";

    private final DataSource dataSource;

    public JdbcGroupDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insertGroups(List<Group> groups) throws DAOException {
        if (groups == null)
            throw new IllegalArgumentException("Null is not allowed");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            for (Group group : groups) {
                statement.setString(1, group.getName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.throwing("JdbcGroupDao", "insertGroups:", e);
            throw new DAOException("Error in insertGroups", e);
        }
    }

    @Override
    public List<Group> getGroups() throws DAOException {
        List<Group> result;
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_ALL);
            ResultSet resultSet = statement.executeQuery();
            result = processResultSet(resultSet);
        } catch (SQLException e) {
            log.throwing("JdbcGroupDao", "getGroups:", e);
            throw new DAOException("Error in getGroups", e);
        }
        return result;
    }

    @Override
    public List<Group> getGroupsByStudentsCount(int count) throws DAOException {
        List<Group> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_STUDENTS_COUNT)) {
            statement.setInt(1, count);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Group group = new Group();
                    group.setId(resultSet.getInt(1));
                    group.setName(resultSet.getString(2));
                    group.setStudentsCount(resultSet.getInt(3));
                    result.add(group);
                }
            }
        } catch (SQLException e) {
            log.throwing("JdbcGroupDao", "getGroupsByStudentsCount:", e);
            throw new DAOException("Error in getGroupsByStudentsCount", e);
        }
        return result;
    }

    private List<Group> processResultSet(ResultSet resultSet) throws SQLException {
        List<Group> result = new ArrayList<>();
        while (resultSet.next()) {
            Group group = new Group();
            group.setId(resultSet.getInt(1));
            group.setName(resultSet.getString(2));
            result.add(group);
        }
        return result;
    }
}
