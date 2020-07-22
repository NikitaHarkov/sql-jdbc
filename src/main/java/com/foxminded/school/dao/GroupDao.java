package com.foxminded.school.dao;

import com.foxminded.school.domain.Group;
import com.foxminded.school.exception.DAOException;

import java.util.List;

public interface GroupDao {
    void insertGroups(List<Group> groups) throws DAOException;

    List<Group> getGroups() throws DAOException;

    List<Group> getGroupsByStudentsCount(int count) throws DAOException;
}
