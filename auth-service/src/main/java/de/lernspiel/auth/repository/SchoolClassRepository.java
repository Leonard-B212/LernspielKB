package de.lernspiel.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.auth.entity.SchoolClass;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
    boolean existsByClassName(String className);
    List<SchoolClass> findByTeacherID(Integer teacherID);
}