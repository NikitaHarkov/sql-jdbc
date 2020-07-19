package com.foxminded.school.service;

import com.foxminded.school.dao.ConnectionProvider;
import com.foxminded.school.dao.CourseDao;
import com.foxminded.school.dao.GroupDao;
import com.foxminded.school.dao.StudentDao;
import com.foxminded.school.dao.jdbc.JdbcCourseDao;
import com.foxminded.school.dao.jdbc.JdbcGroupDao;
import com.foxminded.school.dao.jdbc.JdbcStudentDao;
import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Group;
import com.foxminded.school.domain.Student;

import java.util.List;
import java.util.Map;

public class TestDataGenerator {
    public static void generateTestData(TestData testData, ConnectionProvider connectionProvider) {
        List<Group> groups = testData.getGroups();
        GroupDao groupDao = new JdbcGroupDao(connectionProvider);
        groupDao.insertGroups(groups);

        List<Course> courses = testData.getCourses();
        CourseDao courseDao = new JdbcCourseDao(connectionProvider);
        courseDao.insertCourses(courses);

        List<Student> students = testData.getStudents(groups);
        Map<Student, List<Course>> studentCourses = testData.getStudentsCourses(students, courses);
        StudentDao studentDao = new JdbcStudentDao(connectionProvider);
        studentDao.insertStudents(students);
        studentDao.assignToCourses(studentCourses);
    }
}
