package com.javacorner.admin.runner;

import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.dto.InstructorDTO;
import com.javacorner.admin.dto.StudentDTO;
import com.javacorner.admin.dto.UserDTO;
import com.javacorner.admin.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyRunner implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;
    private final InstructorService instructorService;
    private final CourseService courseService;
    private final StudentService studentService;

    public MyRunner(RoleService roleService, UserService userService, InstructorService instructorService, CourseService courseService, StudentService studentService) {
        this.roleService = roleService;
        this.userService = userService;
        this.instructorService = instructorService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoles();
        createAdmin();
        createInstructor();
        createCourses();
        StudentDTO student = createStudent();
        assignCourseToStudent(student);
        createStudents();
    }

    private void createStudents() {
        for(int i = 0; i < 20; i++){
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setFirstName("studentFN"+i);
            studentDTO.setLastName("studentLN"+i);
            studentDTO.setLevel("intermidiate"+i);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail("student"+i+"@email.com");
            userDTO.setPassword("1234");
            studentDTO.setUser(userDTO);
            studentService.createStudent(studentDTO);
        }
    }

    private void assignCourseToStudent(StudentDTO student) {
        courseService.assigStudentToCourse(1L, student.getStudentId());
    }

    private StudentDTO createStudent() {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setFirstName("studentFN");
        studentDTO.setLastName("studentLN");
        studentDTO.setLevel("intermidiate");
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("student@email.com");
        userDTO.setPassword("1234");
        studentDTO.setUser(userDTO);
        return studentService.createStudent(studentDTO);
    }

    private void createCourses() {
        for(int i = 0; i < 20; i++) {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseDescription("Java" + i);
            courseDTO.setCourseDuration(i + "Hours");
            courseDTO.setCourseName("Java Course" + i);
            InstructorDTO instructorDTO = new InstructorDTO();
            instructorDTO.setInstructorId(1L);
            courseDTO.setInstructor(instructorDTO);
            courseService.createCourse(courseDTO);
        }
    }

    private void createInstructor() {
        for(int i = 0; i < 20; i++){
            InstructorDTO instructorDTO = new InstructorDTO();
            instructorDTO.setFirstName("instructor" + i + "FN");
            instructorDTO.setLastName("instructor" + i + "LN");
            instructorDTO.setSummary("master" + i);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail("instructor" + i + "@mail.com");
            userDTO.setPassword("1234");
            instructorDTO.setUser(userDTO);
            instructorService.createInstructor(instructorDTO);
        }
    }

    private void createAdmin() {
        userService.createUser("admin@mail.com", "1234");
        userService.assingRoleToUser("admin@mail.com", "Admin");
    }

    private void createRoles() {
        Arrays.asList("Admin", "Instructor", "Student").forEach(role -> roleService.createRole(role));
    }
}
