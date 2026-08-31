package de.lernspiel.game.dto;

import java.util.Map;

public class LogFile {
    Map<String, Object> contents;
    LogType logType;

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
