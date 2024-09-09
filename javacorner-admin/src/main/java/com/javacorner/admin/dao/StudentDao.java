package com.javacorner.admin.dao;

import com.javacorner.admin.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentDao extends JpaRepository<Student, Long> {

    @Query(value = "SELECT S FROM Student AS S WHERE S.firstName LIKE %:name% OR S.lastName LIKE %:name%")
    List<Student> findStudentByName(@Param("name") String name);

    @Query(value = "SELECT S FROM Student AS S WHERE S.user.email=:email")
    Student findStudentByEmail(@Param("email") String email);
}
