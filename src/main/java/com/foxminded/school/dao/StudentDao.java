package com.foxminded.school.dao;

import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Student;
import com.foxminded.school.exception.DAOException;

import java.util.List;
import java.util.Map;

public interface StudentDao {
    List<Student> getAll() throws DAOException;

    void insert(List<Student> students) throws DAOException;

    void insert(Student student) throws DAOException;

    void deleteById(int studentId) throws DAOException;

    List<Student> getByCourseName(String courseName) throws DAOException;

    void assignToCourses(Map<Student, List<Course>> map) throws DAOException;

    void assignToCourse(int studentId, int courseId) throws DAOException;

    void deleteFromCourse(int studentId, int courseId) throws DAOException;
}
