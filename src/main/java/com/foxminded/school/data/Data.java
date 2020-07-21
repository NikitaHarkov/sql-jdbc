package com.foxminded.school.data;

import com.foxminded.school.domain.Course;
import com.foxminded.school.domain.Group;
import com.foxminded.school.domain.Student;

import java.util.*;
import java.util.stream.Collectors;

public class Data {
    private final Random random = new Random();
    private final String[] firstNames = {"James", "John", "Mike", "Jeremy", "Ivan",
            "Ron", "Anthony", "Jack", "Harry", "Jacob",
            "Kyle", "William", "David", "Richard", "Joseph",
            "Thomas", "Alexander", "Daniel", "Oscar", "Charlie"};

    final String[] lastNames = {"Ainsley", "Appleton", "Clare", "Clifford", "Benson",
            "Bentley", "Deighton", "Darlington", "Digby", "Kimberley",
            "Kirby", "Langley", "Elton", "Everleigh", "Garrick",
            "Milton", "Brixton", "Hallewell", "Oakes", "Perry"};
    final int[] studentsInGroup = {0, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30};

    public List<Group> getGroups() {
        List<Group> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Group group = new Group();
            group.setId(i + 1);
            StringBuilder course = new StringBuilder();
            String letters = random.ints(2, 65, 90)
                    .mapToObj(upperLetter -> String.valueOf((char) upperLetter))
                    .collect(Collectors.joining());
            String numbers = random.ints(2, 0, 9)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining());
            group.setName(course.append(letters + "-" + numbers).toString());
            list.add(group);
        }
        return list;
    }

    public List<Course> getCourses() {
        return Arrays.asList(
                new Course(1, "Archaeology", "Archaeology is the study of human and prehistorconducted through the act of excavation and analysis."),
                new Course(2, "Architecture", "Buildings and other physical structures, but athe art and science of designing buildings, and the design aethod of construction."),
                new Course(3, "Chemistry", "Chemistry is one of three central branches educational science. It is a physical science that studihe composition, structure, properties and change of matter."),
                new Course(4, "Computer Science", "How we use computers and computer programmhas utterly defined the world we live in today and its computcientists whom connect the abstract with concrete creating troducts we use every day."),
                new Course(5, "Criminology", "Criminology is the scientific study of criminal behavi, on individual, social and natural levels, and how it can be manageontrolled and prevented."),
                new Course(6, "Economics", "Social science of which factors determine the production adistribution goods and services in a consumer, capitalist society."),
                new Course(7, "History", "Historians use evidence to try to understand why people believwhat they believed and why they did what they did."),
                new Course(8, "Mathematics", "There are three main areas of study under the umbrella Mathematics – mathematics itself, statistics, and operational research"),
                new Course(9, "Robotics", "Robotics is a branch of mechanical engineering, electricengineering, electronic engineering and computer science."),
                new Course(10, "Sociology", "Sociology is the scientific study of behaviour by people in" +
                        " the society in which they live, how it came about, is organised and " +
                        "developed, and what it may become in the future.")
        );
    }

    public List<Student> getStudents(List<Group> groups) {
        List<Student> students = getStudentsWithNames();
        return assignStudentsToGroups(students, groups);
    }

    private List<Student> getStudentsWithNames() {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Student student = new Student();
            student.setId(i + 1);
            student.setFirstName(getRandomFirstName());
            student.setLastName(getRandomLastName());
            list.add(student);
        }
        return list;
    }

    public Map<Student, List<Course>> getStudentsCourses(List<Student> students, List<Course> courses) {
        Map<Student, List<Course>> result = new HashMap<>();
        for (Student student : students) {
            int coursesCount = getRandomCoursesCount();
            List<Course> coursesTmp = new ArrayList<>(courses);
            List<Course> studentCourses = new ArrayList<>();
            for (int i = 0; i < coursesCount; i++) {
                int randomCourseIndex = getRandomCourseIndex(coursesTmp);
                Course studentCourse = coursesTmp.get(randomCourseIndex);
                studentCourses.add(studentCourse);
                coursesTmp.remove(randomCourseIndex);
            }
            result.put(student, studentCourses);
        }
        return result;
    }

    private List<Student> assignStudentsToGroups(List<Student> students, List<Group> groups) {
        List<Student> studentsTempList = new ArrayList<>(students);
        for (Group group : groups) {
            int studentsCount = getRandomStudentsCount();
            for (int i = 0; i < studentsCount; i++) {
                if (studentsCount > students.size() || !studentsTempList.isEmpty()) {
                    int randomStudentIndex = getRandomStudentIndex(studentsTempList);
                    int studentId = studentsTempList.get(randomStudentIndex).getId();
                    studentsTempList.remove(randomStudentIndex);
                    assignGroupIdToStudent(students, studentId, group.getId());
                }
            }
        }
        return students;
    }

    private void assignGroupIdToStudent(List<Student> students, int studentId, int groupId) {
        for (Student student : students) {
            if (student.getId() == studentId) {
                student.setGroupId(groupId);
            }
        }
    }

    private String getRandomFirstName() {
        return firstNames[random.nextInt(firstNames.length - 1)];
    }

    private String getRandomLastName() {
        return lastNames[random.nextInt(lastNames.length - 1)];
    }

    private int getRandomStudentsCount() {
        return studentsInGroup[random.nextInt(studentsInGroup.length - 1)];
    }

    private int getRandomStudentIndex(List<Student> students) {
        if (students.size() > 0) {
            return random.nextInt(students.size());
        } else return 0;
    }

    private int getRandomCoursesCount() {
        return random.nextInt(3) + 1;
    }

    private int getRandomCourseIndex(List<Course> courses) {
        return random.nextInt(courses.size());
    }
}
