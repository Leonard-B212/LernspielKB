package de.lernspiel.common.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enthält die während einer Programmausführung erzeugten Log-Einträge.
 */
public class ExecutionLog {

    private List<LogFile> entries = new ArrayList<>();

    public void add(LogFile logFile) {
        entries.add(logFile);
    }

    public List<LogFile> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void setEntries(List<LogFile> entries) {
        this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
    }
}