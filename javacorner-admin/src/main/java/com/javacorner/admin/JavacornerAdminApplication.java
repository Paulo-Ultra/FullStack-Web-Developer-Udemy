package com.javacorner.admin;

import com.javacorner.admin.dao.*;
import com.javacorner.admin.utility.OperationUtility;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavacornerAdminApplication implements CommandLineRunner {

    private final UserDao userDao;

    private final CourseDao courseDao;

    private final InstructorDao instructorDao;

    private final StudentDao studentDao;

    private final RoleDao roleDao;

    public JavacornerAdminApplication(UserDao userDao, CourseDao courseDao, InstructorDao instructorDao, StudentDao studentDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.courseDao = courseDao;
        this.instructorDao = instructorDao;
        this.studentDao = studentDao;
        this.roleDao = roleDao;
    }

    public static void main(String[] args) {
        SpringApplication.run(JavacornerAdminApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        OperationUtility.usersOperations(userDao);
        OperationUtility.rolesOperations(roleDao);
        OperationUtility.assignRolesToUsers(userDao, roleDao);
        OperationUtility.instructorsOperations(userDao, instructorDao, roleDao);
        OperationUtility.studentsOperations(userDao, studentDao, roleDao);
        OperationUtility.coursesOperations(courseDao, instructorDao, studentDao);
    }
}
