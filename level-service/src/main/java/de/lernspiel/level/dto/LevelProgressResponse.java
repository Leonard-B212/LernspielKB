package de.lernspiel.level.dto;

import java.util.List;

/**
 * Enthält den aktuellen Level-Fortschritt eines Benutzers.
 *
 * Das DTO kann später um weitere Fortschrittsinformationen erweitert werden,
 * ohne die grundlegende API-Struktur ändern zu müssen.
 */
public class LevelProgressResponse {

    private List<Integer> completedLevelIDs;

    public LevelProgressResponse(List<Integer> completedLevelIDs) {
        this.completedLevelIDs = completedLevelIDs;
    }

    public List<Integer> getCompletedLevelIDs() {
        return completedLevelIDs;
    }

    public void setCompletedLevelIDs(List<Integer> completedLevelIDs) {
        this.completedLevelIDs = completedLevelIDs;
    }
}