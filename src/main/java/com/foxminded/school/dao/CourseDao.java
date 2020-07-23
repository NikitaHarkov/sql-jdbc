package com.foxminded.school.dao;

import com.foxminded.school.domain.Course;
import com.foxminded.school.exception.DAOException;

import java.util.List;

public interface CourseDao {
    void insertMany(List<Course> courses) throws DAOException;

    List<Course> getAll() throws DAOException;

    List<Course> getByStudentId(int studentId) throws DAOException;
}
