package de.lernspiel.common.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExecutionLog {
    private List<LogFile> entries = new ArrayList<>();

    public void add(LogFile logFile) {
        entries.add(logFile);
    }

    public List<LogFile> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}