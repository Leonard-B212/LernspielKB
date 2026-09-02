package de.lernspiel.common.code;

import java.util.Map;

/**
 * Repräsentiert einen einzelnen Eintrag innerhalb eines ExecutionLogs.
 */
public class LogFile {

    private Map<String, Object> contents;
    private LogType logType;

    public LogFile() {
    }

    public LogFile(Map<String, Object> contents, LogType logType) {
        this.contents = contents;
        this.logType = logType;
    }

    public Map<String, Object> getContents() {
        return contents;
    }

    public void setContents(Map<String, Object> contents) {
        this.contents = contents;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }
}