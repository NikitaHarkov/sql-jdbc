package com.foxminded.school.dao;

import com.foxminded.school.domain.Course;

import java.util.List;

public interface CourseDao {
    void insertCourses(List<Course> courses) throws DAOException;

    List<Course> getAllCourses() throws DAOException;

    List<Course> getByStudentId(int studentId) throws DAOException;
}
