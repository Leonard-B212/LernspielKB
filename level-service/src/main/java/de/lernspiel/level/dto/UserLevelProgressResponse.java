package de.lernspiel.level.dto;

/**
 * Enthält den Level-Fortschritt eines Benutzers.
 *
 * Wird verwendet, um Lehrern den Fortschritt ihrer
 * zugewiesenen Schüler kompakt bereitzustellen.
 */
public class UserLevelProgressResponse {

    private Integer userID;
    private long completedLevels;
    private long totalLevels;

    public UserLevelProgressResponse(Integer userID, long completedLevels, long totalLevels) {
        this.userID = userID;
        this.completedLevels = completedLevels;
        this.totalLevels = totalLevels;
    }

    public Integer getUserID() {
        return userID;
    }

    public long getCompletedLevels() {
        return completedLevels;
    }

    public long getTotalLevels() {
        return totalLevels;
    }
}