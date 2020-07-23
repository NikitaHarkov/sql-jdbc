package com.foxminded.school.dao;

import com.foxminded.school.domain.Group;
import com.foxminded.school.exception.DAOException;

import java.util.List;

public interface GroupDao {
    void insert(List<Group> groups) throws DAOException;

    List<Group> getAll() throws DAOException;

    List<Group> getByStudentsCount(int count) throws DAOException;
}
