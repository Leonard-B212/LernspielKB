package de.lernspiel.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.CompletedLevel;

/**
 * Repository für abgeschlossene Level.
 *
 * Ermöglicht das Speichern und Abfragen des Fortschritts
 * eines Benutzers.
 */
public interface CompletedLevelRepository
        extends JpaRepository<CompletedLevel, Integer> {

    /**
     * Prüft, ob ein Benutzer ein bestimmtes Level bereits abgeschlossen hat.
     */
    boolean existsByUserIDAndLevelLevelID(
            Integer userID,
            Integer levelID
    );

    /**
     * Liefert alle abgeschlossenen Level eines Benutzers.
     */
    List<CompletedLevel> findByUserID(
            Integer userID
    );
}