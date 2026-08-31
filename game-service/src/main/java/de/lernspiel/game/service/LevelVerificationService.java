package de.lernspiel.game.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import de.lernspiel.game.dto.ExecutionLog;
import de.lernspiel.game.dto.LogFile;
import de.lernspiel.game.dto.LogType;

@Service
public class LevelVerificationService {

    public boolean verify(ExecutionLog expectedExecutionLog, ExecutionLog actualExecutionLog) {
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

    /**
     * Bringt SIMPLE_VARIABLE_DECLARATION+VARIABLE_VALUE_ASSIGNMENT und die kombinierte
     * VARIABLE_DECLARATION_ASSIGNMENT auf eine gemeinsame kanonische Form: Jede
     * VARIABLE_DECLARATION_ASSIGNMENT wird in ihre zwei äquivalenten Teilschritte zerlegt
     * ("int x = 5;" -> "int x;" + "x = 5;"). Dadurch ist es für den nachfolgenden Vergleich
     * egal, ob eine Deklaration mit Zuweisung im Log kombiniert oder aufgeteilt vorliegt -
     * auf beiden Seiten (erwartet wie tatsächlich).
     */
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
        // Enthält bereits alle Felder, die auch ein "echtes" VARIABLE_VALUE_ASSIGNMENT hat
        // (variableType, variableName, variableValue, expressionForm/-Operands/-Operators).
        Map<String, Object> contents = new HashMap<>(combined.getContents());
        return new LogFile(contents, LogType.VARIABLE_VALUE_ASSIGNMENT);
    }

    /** Sucht ab fromIndex den nächsten actual-Eintrag, der zum erwarteten Eintrag passt. */
    private int findNextMatch(LogFile expectedEntry, List<LogFile> actualEntries, int fromIndex) {
        for (int i = fromIndex; i < actualEntries.size(); i++) {
            if (matches(expectedEntry, actualEntries.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Ein Eintrag passt, wenn der LogType übereinstimmt und alle im erwarteten Eintrag gesetzten Felder gleich sind. */
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