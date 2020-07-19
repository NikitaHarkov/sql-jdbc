package com.foxminded.school.dao;

import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Student;

import java.util.List;
import java.util.Map;

public interface StudentDao {
    List<Student> getAllStudents();

    void insertStudents(List<Student> students);

    void insertStudent(Student student);

    void deleteStudentById(int studentId);

    List<Student> getStudentsByCourseName(String courseName);

    void assignToCourses(Map<Student, List<Course>> map);

    void assignToCourse(int studentId, int courseId);

    void deleteFromCourse(int studentId, int courseId);
}
