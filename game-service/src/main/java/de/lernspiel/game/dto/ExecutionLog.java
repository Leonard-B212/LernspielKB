package de.lernspiel.game.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExecutionLog {
    private List<String> entries = new ArrayList<>();

    public void add(String message) {
        entries.add(message);
    }

    public List<String> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}