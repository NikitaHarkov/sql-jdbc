package com.foxminded.school.dao;

import com.foxminded.school.domain.Group;

import java.util.List;

public interface GroupDao {
    void insertGroups(List<Group> groups);

    List<Group> getGroupsByStudentsCount(int count);
}
