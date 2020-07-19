package com.foxminded.school.dao;

import com.foxminded.school.domain.Course;

import java.util.List;

public interface CourseDao {
    void insertCourses(List<Course> courses);

    List<Course> getAllCourses();

    List<Course> getByStudentId(int studentId);
}
