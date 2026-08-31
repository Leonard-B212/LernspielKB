package de.lernspiel.level.dto;

import de.lernspiel.common.code.ExecutionLog;

public class LevelVerificationRequest {
    private int levelID;
    private ExecutionLog expectedExecutionLog;
    private ExecutionLog actualExecutionLog;

    public LevelVerificationRequest(int levelID, ExecutionLog expectedExecutionLog, ExecutionLog actualExecutionLog) {
        this.levelID = levelID;
        this.expectedExecutionLog = expectedExecutionLog;
        this.actualExecutionLog = actualExecutionLog;
    }

    public int getLevelID() {
        return levelID;
    }
    public void setLevelID(int levelID) {
        this.levelID = levelID;
    }
    public ExecutionLog getExpectedExecutionLog() {
        return expectedExecutionLog;
    }
    public void setExpectedExecutionLog(ExecutionLog expectedExecutionLog) {
        this.expectedExecutionLog = expectedExecutionLog;
    }
    public ExecutionLog getActualExecutionLog() {
        return actualExecutionLog;
    }
    public void setActualExecutionLog(ExecutionLog actualExecutionLog) {
        this.actualExecutionLog = actualExecutionLog;
    }
}