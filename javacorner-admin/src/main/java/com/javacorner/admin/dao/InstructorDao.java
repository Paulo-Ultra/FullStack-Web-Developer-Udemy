package com.javacorner.admin.dao;

import com.javacorner.admin.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstructorDao extends JpaRepository<Instructor, Long> {

    @Query(value = "SELECT I FROM Instructor as I WHERE I.firstName LIKE %:name% OR I.lastName LIKE %:name%")
    List<Instructor> findInstructorByName(@Param("name") String name);

    @Query(value = "SELECT I FROM Instructor AS I WHERE I.user.email=:email")
    Instructor findInstructorByEmail(@Param("email") String email);
}
