package com.javacorner.admin.dao;

import com.javacorner.admin.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseDao extends JpaRepository<Course, Long> {

    List<Course> findByCourseNameContains(String keyword);

    @Query(value = "SELECT * FROM COURSES AS C WHERE C.COURSE_ID IN " +
            "(SELECT E.COURSE_ID FROM ENROLLED_IN AS E WHERE E.STUDENT_ID=:studentId)", nativeQuery = true)
    List<Course> getCourseByStudentId(@Param("studentId") Long studentId);
}
