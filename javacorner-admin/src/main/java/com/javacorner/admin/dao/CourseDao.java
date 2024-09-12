package com.javacorner.admin.dao;

import com.javacorner.admin.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseDao extends JpaRepository<Course, Long> {

    Page<Course> findByCourseNameContains(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM COURSES AS C WHERE C.COURSE_ID IN " +
            "(SELECT E.COURSE_ID FROM ENROLLED_IN AS E WHERE E.STUDENT_ID=:studentId)", nativeQuery = true)
    Page<Course> getCourseByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query(value = "SELECT * FROM COURSES AS C WHERE C.COURSE.ID NOT IN " +
            "(SELECT E.COURSE_ID FROM ENROLLED_IN AS E WHERE E.STUDENT_ID=:studentId)", nativeQuery = true)
    Page<Course> getNonEnrolledInCourseByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query(value = "SELECT C FROM Course AS C WHERE C.instructor.instructorId=:instructorID")
    Page<Course> getCourseByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);
}
