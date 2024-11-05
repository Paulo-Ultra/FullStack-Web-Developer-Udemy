package com.javacorner.admin.service.impl;

import com.javacorner.admin.dao.CourseDao;
import com.javacorner.admin.dao.InstructorDao;
import com.javacorner.admin.dao.StudentDao;
import com.javacorner.admin.dto.CourseDTO;
import com.javacorner.admin.entity.Course;
import com.javacorner.admin.entity.Instructor;
import com.javacorner.admin.entity.Student;
import com.javacorner.admin.mapper.CourseMapper;
import com.javacorner.admin.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;
    private final CourseMapper courseMapper;
    private final InstructorDao instructorDao;
    private final StudentDao studentDao;

    public CourseServiceImpl(CourseDao courseDao, CourseMapper courseMapper, InstructorDao instructorDao, StudentDao studentDao) {
        this.courseDao = courseDao;
        this.courseMapper = courseMapper;
        this.instructorDao = instructorDao;
        this.studentDao = studentDao;
    }

    @Override
    public Course loadCourseById(Long courseId) {
        return courseDao.findById(courseId).orElseThrow(() -> new EntityNotFoundException(
                "Course with ID " + courseId + " not found"));
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = courseMapper.fromCourseDTO(courseDTO);
        Instructor instructor = instructorDao.findById(courseDTO.getInstructor().getInstructorId())
                .orElseThrow(() -> new EntityNotFoundException("Instructor with ID " + courseDTO.getInstructor().getInstructorId() + " not found"));
        course.setInstructor(instructor);
        Course savedCourse = courseDao.save(course);
        return courseMapper.fromCourse(savedCourse);
    }

    @Override
    public CourseDTO updateCourse(CourseDTO courseDTO) {
        Course loadedCourse = loadCourseById(courseDTO.getCourseId());
        Instructor instructor = instructorDao.findById(courseDTO.getInstructor().getInstructorId())
                .orElseThrow(() -> new EntityNotFoundException("Instructor with ID " + courseDTO.getInstructor().getInstructorId() + " not found"));
        Course course = courseMapper.fromCourseDTO(courseDTO);
        course.setInstructor(instructor);
        course.setStudents(loadedCourse.getStudents());
        Course updateCourse = courseDao.save(course);
        return courseMapper.fromCourse(updateCourse);
    }

    @Override
    public Page<CourseDTO> findCoursesByCourseName(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course>  coursesPage = courseDao.findByCourseNameContains(keyword, pageRequest);
        return getPageCoursesDTO(coursesPage, pageRequest);
    }

    @Override
    public void assigStudentToCourse(Long courseId, Long studentId) {
        Student student = studentDao.findById(studentId).orElseThrow(() -> new EntityNotFoundException(
                "Student with ID " + studentId + " not found"));
        Course course = loadCourseById(courseId);
        course.assignStudentToCourse(student);
    }

    @Override
    public Page<CourseDTO> fetchCoursesFromStudent(Long studentId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course> studentsCoursesPage = courseDao.getCourseByStudentId(studentId, pageRequest);
        return getPageCoursesDTO(studentsCoursesPage, pageRequest);
    }

    @Override
    public Page<CourseDTO> fetchNonEnrolledInCoursesForStudent(Long studentId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course> nonEnrolledInCoursesPage = courseDao.getNonEnrolledInCourseByStudentId(studentId, pageRequest);
        return getPageCoursesDTO(nonEnrolledInCoursesPage, pageRequest);
    }

    @Override
    public void removeCourse(Long courseId) {
        courseDao.deleteById(courseId);
    }

    @Override
    public Page<CourseDTO> fetchCoursesForInstructor(Long instructorId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course> instructorCoursesPage = courseDao.getCourseByInstructorId(instructorId, pageRequest);
        return getPageCoursesDTO(instructorCoursesPage, pageRequest);
    }

    private PageImpl<CourseDTO> getPageCoursesDTO(Page<Course> coursePage, PageRequest pageRequest) {
        return new PageImpl<>(coursePage.getContent().stream().map(courseMapper::fromCourse)
                .collect(Collectors.toList()), pageRequest, coursePage.getTotalElements());
    }
}
