package com.foxminded.school.data;

import com.foxminded.school.dao.*;
import com.foxminded.school.dao.jdbc.JdbcCourseDao;
import com.foxminded.school.dao.jdbc.JdbcGroupDao;
import com.foxminded.school.dao.jdbc.JdbcStudentDao;
import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Group;
import com.foxminded.school.domain.Student;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DataGenerator {
    private static final Logger log = Logger.getLogger(DataGenerator.class.getName());
    public static void generateTestData(Data data, DataSource dataSource) {
        try{
            List<Group> groups = data.getGroups();
            GroupDao groupDao = new JdbcGroupDao(dataSource);
            groupDao.insertGroups(groups);

            List<Course> courses = data.getCourses();
            CourseDao courseDao = new JdbcCourseDao(dataSource);
            courseDao.insertCourses(courses);

            List<Student> students = data.getStudents(groups);
            Map<Student, List<Course>> studentCourses = data.getStudentsCourses(students, courses);
            StudentDao studentDao = new JdbcStudentDao(dataSource);
            studentDao.insertStudents(students);
            studentDao.assignToCourses(studentCourses);
        }catch (DAOException ex){
            log.throwing("DataGenerator", "generateTestData",ex);
            System.out.println("Cannot add data to database: " + ex);
        }

    }
}
