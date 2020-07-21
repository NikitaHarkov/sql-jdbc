package com.foxminded.school.dao;

import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Student;

import java.util.List;
import java.util.Map;

public interface StudentDao {
    List<Student> getAllStudents() throws DAOException;

    void insertStudents(List<Student> students) throws DAOException;

    void insertStudent(Student student) throws DAOException;

    void deleteStudentById(int studentId) throws DAOException;

    List<Student> getStudentsByCourseName(String courseName) throws DAOException;

    void assignToCourses(Map<Student, List<Course>> map) throws DAOException;

    void assignToCourse(int studentId, int courseId) throws DAOException;

    void deleteFromCourse(int studentId, int courseId) throws DAOException;
}
