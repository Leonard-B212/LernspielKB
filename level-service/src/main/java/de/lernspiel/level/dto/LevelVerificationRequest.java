package de.lernspiel.level.dto;

import de.lernspiel.common.code.ExecutionLog;

/**
 * Enthält die Daten zur Prüfung eines ausgeführten Levels.
 * Der erwartete ExecutionLog bleibt ausschließlich im Backend.
 */
public class LevelVerificationRequest {

    private int levelID;
    private ExecutionLog actualExecutionLog;

    public LevelVerificationRequest() {
    }

    public LevelVerificationRequest(int levelID, ExecutionLog actualExecutionLog) {
        this.levelID = levelID;
        this.actualExecutionLog = actualExecutionLog;
    }

    public int getLevelID() {
        return levelID;
    }

    public void setLevelID(int levelID) {
        this.levelID = levelID;
    }

    public ExecutionLog getActualExecutionLog() {
        return actualExecutionLog;
    }

    public void setActualExecutionLog(ExecutionLog actualExecutionLog) {
        this.actualExecutionLog = actualExecutionLog;
    }
}