package de.lernspiel.level.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.Level;
import de.lernspiel.level.entity.LevelCategory;
import de.lernspiel.level.entity.ProgrammingLanguage;

/**
 * Repository für die Verwaltung von Leveln.
 *
 * Ermöglicht neben den Standardoperationen die Prüfung, ob ein Level
 * für eine bestimmte Kategorie, Levelnummer und Programmiersprache existiert.
 */
public interface LevelRepository extends JpaRepository<Level, Integer> {

    // Prüft, ob ein entsprechendes Level bereits existiert.
    boolean existsByCategoryAndLevelNumberAndLanguage(
            LevelCategory category,
            Integer levelNumber,
            ProgrammingLanguage language
    );
}