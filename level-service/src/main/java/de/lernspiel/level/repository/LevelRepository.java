package de.lernspiel.level.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.Level;
import de.lernspiel.level.entity.ProgrammingLanguage;

public interface LevelRepository extends JpaRepository<Level, Integer> {

    boolean existsByCategoryAndLevelNumberAndLanguage(
            String category,
            Integer levelNumber,
            ProgrammingLanguage language
    );
}