package com.foxminded.school.data;

import com.foxminded.school.dao.*;
import com.foxminded.school.dao.impl.CourseDaoImpl;
import com.foxminded.school.dao.impl.GroupDaoImpl;
import com.foxminded.school.dao.impl.StudentDaoImpl;
import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Group;
import com.foxminded.school.domain.Student;
import com.foxminded.school.exception.DAOException;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DataGenerator {
    private static final Logger log = Logger.getLogger(DataGenerator.class.getName());
    public static void generateTestData(Data data, DataSource dataSource) throws DAOException {
        try{
            List<Group> groups = data.getGroups();
            GroupDao groupDao = new GroupDaoImpl(dataSource);
            groupDao.insertMany(groups);

            List<Course> courses = data.getCourses();
            CourseDao courseDao = new CourseDaoImpl(dataSource);
            courseDao.insertMany(courses);

            List<Student> students = data.getStudents(groups);
            Map<Student, List<Course>> studentCourses = data.getStudentsCourses(students, courses);
            StudentDao studentDao = new StudentDaoImpl(dataSource);
            studentDao.insertMany(students);
            studentDao.assignToCourses(studentCourses);
        }catch (DAOException ex){
            log.throwing("DataGenerator", "generateTestData",ex);
            throw new DAOException("Cannot add data to database", ex);
        }

    }
}
