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
import java.util.Random;
import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private final CourseDao courseDao;
    private final GroupDao groupDao;
    private final StudentDao studentDao;

    public UserInterface(ConnectionProvider connectionProvider) {
        scanner = new Scanner(System.in);
        courseDao = new JdbcCourseDao(connectionProvider);
        groupDao = new JdbcGroupDao(connectionProvider);
        studentDao = new JdbcStudentDao(connectionProvider);
    }

    public void runInterface() {
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            System.out.println("Enter a letter from '1' to '6' to make queries or 'q' to exit!");
            switch (scanner.next()) {
                case "1":
                    findGroups();
                    break;
                case "2":
                    findStudentsByCourseName();
                    break;
                case "3":
                    addNewStudent();
                    break;
                case "4":
                    deleteStudentById();
                    break;
                case "5":
                    addStudentToCourse();
                    break;
                case "6":
                    removeStudentCourse();
                    break;
                case "q":
                    System.out.println("Exiting....");
                    exit = true;
                    break;
            }
        }
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("*** MAIN MENU ***");
        System.out.println("1. Find all groups with less or equals student count");
        System.out.println("2. Find all students related to course with given name");
        System.out.println("3. Add new student");
        System.out.println("4. Delete student by STUDENT_ID");
        System.out.println("5. Add a student to the course (from a list)");
        System.out.println("6. Remove the student from one of his or her courses");
        System.out.println("q. Exit program");
        System.out.print("Enter menu-letter >>> ");
    }

    private void findGroups() {
        System.out.println("Find groups by max. students count: ");
        System.out.println("Enter students count >>> ");
        int studentsCount = getNumber();
        List<Group> groups = groupDao.getGroupsByStudentsCount(studentsCount);
        printGroups(groups);
    }

    private void findStudentsByCourseName() {
        System.out.println("Find students by course name: ");
        System.out.print("Enter course name >>> ");
        String courseName = scanner.next();

        List<Student> students = studentDao.getStudentsByCourseName(courseName);

        System.out.println("Students from course \"" + courseName + "\":");
        System.out.println();
        printStudents(students);
    }

    private void addNewStudent() {
        System.out.println("Add new Student: ");
        System.out.println("Enter first name >>> ");
        String firstName = scanner.next();

        System.out.println("Enter last name >>> ");
        String lastName = scanner.next();

        Student newStudent = new Student();
        newStudent.setGroupId(new Random().nextInt(10)+1);
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);

        studentDao.insertStudent(newStudent);
        System.out.println("Student " + newStudent.getFirstName() + " " + newStudent.getLastName() + " inserted");
    }

    private void deleteStudentById() {
        System.out.println("Delete student by ID: ");
        int studentId = getNumber();
        studentDao.deleteStudentById(studentId);
        System.out.println("Student not deleted");
    }

    private void addStudentToCourse() {
        System.out.println("Add student to course: ");
        System.out.println("List of students: ");
        printStudents(studentDao.getAllStudents());

        System.out.println("Enter student id >>> ");
        int studentId = getNumber();

        System.out.println("List of courses: ");
        printCourses(courseDao.getAllCourses());

        System.out.println("Enter course id >>> ");
        int courseId = getNumber();

        if (studentId > 0 && courseId > 0) {
            studentDao.assignToCourse(studentId, courseId);
            System.out.println("Course added");
        } else {
            System.out.println("Error, wrong IDs entered");
        }
    }

    private void removeStudentCourse() {
        System.out.println("Remove student course: ");
        System.out.println("List of students: ");
        printStudents(studentDao.getAllStudents());

        System.out.println("Enter student id >>> ");
        int studentId = getNumber();

        System.out.println("List of student courses: ");
        printCourses(courseDao.getByStudentId(studentId));

        System.out.println("Enter course id >>> ");
        int courseId = getNumber();

        if (studentId > 0 && courseId > 0) {
            studentDao.deleteFromCourse(studentId, courseId);
            System.out.println("Relation student-course deleted");
        } else {
            System.out.println("Error, wrong IDs entered");
        }

    }

    private int getNumber() {
        int number = 0;
        while (number == 0) {
            try {
                number = scanner.nextInt();
                System.out.println("Number entered: " + number);
            } catch (Exception e) {
                System.out.println("Error! Please enter number!");
            }
        }
        return number;
    }

    private void printGroups(List<Group> groups) {
        for (Group group : groups) {
            System.out.println("Group name: " + group.getName());
            System.out.println("Students: " + group.getStudentsCount());
            System.out.println();
        }
    }

    private void printStudents(List<Student> students) {
        for (Student student : students) {
            System.out.println("Student ID: " + student.getId());
            System.out.println("Name: " + student.getFirstName() + " " + student.getLastName());
            System.out.println();
        }
    }

    private void printCourses(List<Course> courses) {
        for (Course course : courses) {
            System.out.println("Course ID: " + course.getId());
            System.out.println("Name: " + course.getName());
            System.out.println();
        }
    }
}
