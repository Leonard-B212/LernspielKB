package de.lernspiel.level.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.lernspiel.level.entity.LevelCategory;

/**
 * Repository für die Verwaltung von Level-Kategorien.
 *
 * Ermöglicht neben den Standardoperationen insbesondere das Laden
 * einer Kategorie anhand ihres eindeutigen Namens.
 */
public interface LevelCategoryRepository extends JpaRepository<LevelCategory, Integer> {

    // Lädt eine Level-Kategorie anhand ihres eindeutigen Namens.
    Optional<LevelCategory> findByCategoryName(String categoryName);

    // Prüft, ob eine Level-Kategorie mit dem angegebenen Namen existiert.
    boolean existsByCategoryName(String categoryName);
}