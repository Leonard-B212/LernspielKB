package de.lernspiel.level.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.common.code.LogFile;
import de.lernspiel.common.code.LogType;
import de.lernspiel.level.entity.Level;
import de.lernspiel.level.repository.LevelRepository;

/**
 * Prüft den ExecutionLog eines ausgeführten Programms gegen die erwartete Lösung eines Levels.
 */
@Service
public class LevelVerificationService {

    private final LevelRepository levelRepository;

    public LevelVerificationService(LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    // Lädt den erwarteten Log des Levels und vergleicht ihn mit der tatsächlichen Ausführung.
    public boolean verify(int levelID, ExecutionLog actualExecutionLog) {
        Level level = levelRepository.findById(levelID)
                .orElseThrow(() -> new IllegalArgumentException("Level nicht gefunden: " + levelID));

        ExecutionLog expectedExecutionLog = level.getExpectedExecutionLog();

        if (expectedExecutionLog == null || actualExecutionLog == null) {
            return false;
        }

        return compareExecutionLogs(expectedExecutionLog, actualExecutionLog);
    }

    // Vergleicht die relevanten erwarteten Log-Einträge mit der tatsächlichen Ausführung.
    private boolean compareExecutionLogs(ExecutionLog expectedExecutionLog, ExecutionLog actualExecutionLog) {
        List<LogFile> expectedEntries = normalize(expectedExecutionLog.getEntries());
        List<LogFile> actualEntries = normalize(actualExecutionLog.getEntries());

        List<LogFile> missingExpectedEntries = new ArrayList<>();
        int actualCursor = 0;

        for (LogFile expectedEntry : expectedEntries) {
            int matchIndex = findNextMatch(expectedEntry, actualEntries, actualCursor);

            if (matchIndex == -1) {
                missingExpectedEntries.add(expectedEntry);
            } else {
                actualCursor = matchIndex + 1;
            }
        }

        boolean hadRuntimeError = actualEntries.stream()
                .anyMatch(entry -> entry.getLogType() == LogType.ERROR);

        return missingExpectedEntries.isEmpty() && !hadRuntimeError;
    }

    // Zerlegt kombinierte Deklarationen und Zuweisungen in eine einheitliche Vergleichsform.
    private List<LogFile> normalize(List<LogFile> entries) {
        List<LogFile> normalized = new ArrayList<>();

        for (LogFile entry : entries) {
            if (entry.getLogType() == LogType.VARIABLE_DECLARATION_ASSIGNMENT) {
                normalized.add(toSimpleDeclarationPart(entry));
                normalized.add(toValueAssignmentPart(entry));
            } else {
                normalized.add(entry);
            }
        }

        return normalized;
    }

    private LogFile toSimpleDeclarationPart(LogFile combined) {
        Map<String, Object> contents = new HashMap<>();

        contents.put("variableType", combined.getContents().get("variableType"));
        contents.put("variableName", combined.getContents().get("variableName"));

        return new LogFile(contents, LogType.SIMPLE_VARIABLE_DECLARATION);
    }

    private LogFile toValueAssignmentPart(LogFile combined) {
        Map<String, Object> contents = new HashMap<>(combined.getContents());

        return new LogFile(contents, LogType.VARIABLE_VALUE_ASSIGNMENT);
    }

    // Sucht ab der aktuellen Position den nächsten passenden tatsächlichen Log-Eintrag.
    private int findNextMatch(LogFile expectedEntry, List<LogFile> actualEntries, int fromIndex) {
        for (int i = fromIndex; i < actualEntries.size(); i++) {
            if (matches(expectedEntry, actualEntries.get(i))) {
                return i;
            }
        }

        return -1;
    }

    // Prüft LogType und alle im erwarteten Eintrag definierten Felder.
    private boolean matches(LogFile expected, LogFile actual) {
        if (expected.getLogType() != actual.getLogType()) {
            return false;
        }

        for (Map.Entry<String, Object> expectedField : expected.getContents().entrySet()) {
            Object actualValue = actual.getContents().get(expectedField.getKey());

            if (!Objects.equals(expectedField.getValue(), actualValue)) {
                return false;
            }
        }

        return true;
    }
}