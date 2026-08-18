package de.lernspiel.level.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.lernspiel.level.entity.CompletedLevel;
import de.lernspiel.level.entity.Level;
import de.lernspiel.level.repository.CompletedLevelRepository;
import de.lernspiel.level.repository.LevelRepository;

/**
 * Verwaltet den Fortschritt eines Benutzers innerhalb der Level.
 *
 * Der Service speichert abgeschlossene Level und ermöglicht
 * das spätere Abrufen des Benutzerfortschritts.
 */
@Service
public class LevelProgressService {

    private final CompletedLevelRepository completedLevelRepository;
    private final LevelRepository levelRepository;


    public LevelProgressService(
            CompletedLevelRepository completedLevelRepository,
            LevelRepository levelRepository) {

        this.completedLevelRepository =
                completedLevelRepository;

        this.levelRepository =
                levelRepository;
    }


    /**
     * Markiert ein Level für einen Benutzer als abgeschlossen.
     *
     * Bereits abgeschlossene Level werden nicht erneut gespeichert.
     */
    @Transactional
    public void completeLevel(
            Integer userID,
            Integer levelID) {

        Level level =
                levelRepository
                        .findById(levelID)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Level mit ID "
                                        + levelID
                                        + " wurde nicht gefunden."
                                )
                        );


        boolean alreadyCompleted =
                completedLevelRepository
                        .existsByUserIDAndLevelLevelID(
                                userID,
                                levelID
                        );


        if (alreadyCompleted) {
            return;
        }


        CompletedLevel completedLevel =
                new CompletedLevel();

        completedLevel.setUserID(
                userID
        );

        completedLevel.setLevel(
                level
        );


        completedLevelRepository.save(
                completedLevel
        );
    }


    /**
     * Liefert die IDs aller Level, die ein Benutzer abgeschlossen hat.
     */
    public List<Integer> getCompletedLevelIDs(
            Integer userID) {

        return completedLevelRepository
                .findByUserID(userID)
                .stream()
                .map(completedLevel ->
                        completedLevel
                                .getLevel()
                                .getLevelID()
                )
                .toList();
    }
}