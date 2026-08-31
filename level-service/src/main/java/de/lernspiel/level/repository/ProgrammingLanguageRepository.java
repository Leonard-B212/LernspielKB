package de.lernspiel.level.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.ProgrammingLanguage;

/**
 * Repository für die Verwaltung von Programmiersprachen.
 *
 * Ermöglicht neben den Standardoperationen das Laden
 * einer Programmiersprache anhand ihres eindeutigen Namens.
 */
public interface ProgrammingLanguageRepository extends JpaRepository<ProgrammingLanguage, Integer> {

    // Lädt eine Programmiersprache anhand ihres eindeutigen Namens.
    Optional<ProgrammingLanguage> findByLanguageName(String languageName);
}