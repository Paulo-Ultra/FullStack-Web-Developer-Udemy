package com.javacorner.admin.dao;

import com.javacorner.admin.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDao extends JpaRepository<Student, Long> {

    @Query(value = "SELECT S FROM Student AS S WHERE S.firstName LIKE %:name% OR S.lastName LIKE %:name%")
    Page<Student> findStudentsByName(@Param("name") String name, PageRequest pageRequest);

    @Query(value = "SELECT S FROM Student AS S WHERE S.user.email=:email")
    Student findStudentByEmail(@Param("email") String email);
}
