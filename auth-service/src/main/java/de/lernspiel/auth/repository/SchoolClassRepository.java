package de.lernspiel.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.auth.entity.SchoolClass;

public interface SchoolClassRepository
        extends JpaRepository<SchoolClass, Integer> {

    boolean existsByClassName(String className);
}