package de.lernspiel.level.config.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.common.code.LogFile;
import de.lernspiel.common.code.LogType;

/**
 * Erstellt erwartete ExecutionLogs für fest definierte Bootstrap-Level.
 */
public final class ExpectedExecutionLogs {

    private ExpectedExecutionLogs() {
    }

    public static ExecutionLog log(LogFile... entries) {
        ExecutionLog log = new ExecutionLog();

        for (LogFile entry : entries) {
            log.add(entry);
        }

        return log;
    }

    public static LogFile declaration(CodeType type, String name) {
        Map<String, Object> contents = new HashMap<>();

        contents.put("variableType", type);
        contents.put("variableName", name);

        return new LogFile(contents, LogType.SIMPLE_VARIABLE_DECLARATION);
    }

    public static LogFile assignment(CodeType type, String name, Object value) {
        Map<String, Object> contents = new HashMap<>();

        contents.put("variableType", type);
        contents.put("variableName", name);
        contents.put("variableValue", value);

        return new LogFile(contents, LogType.VARIABLE_DECLARATION_ASSIGNMENT);
    }

    public static LogFile valueAssignment(CodeType type, String name, Object value) {
        Map<String, Object> contents = new HashMap<>();

        contents.put("variableType", type);
        contents.put("variableName", name);
        contents.put("variableValue", value);

        return new LogFile(contents, LogType.VARIABLE_VALUE_ASSIGNMENT);
    }

    public static LogFile expressionAssignment(CodeType type, String name, Object value,
            String expressionForm, List<Map<String, Object>> operands, List<String> operators) {

        Map<String, Object> contents = new HashMap<>();

        contents.put("variableType", type);
        contents.put("variableName", name);
        contents.put("variableValue", value);
        contents.put("expressionForm", expressionForm);
        contents.put("expressionOperands", operands);
        contents.put("expressionOperators", operators);

        return new LogFile(contents, LogType.VARIABLE_DECLARATION_ASSIGNMENT);
    }

    public static LogFile expressionValueAssignment(CodeType type, String name, Object value,
            String expressionForm, List<Map<String, Object>> operands, List<String> operators) {

        Map<String, Object> contents = new HashMap<>();

        contents.put("variableType", type);
        contents.put("variableName", name);
        contents.put("variableValue", value);
        contents.put("expressionForm", expressionForm);
        contents.put("expressionOperands", operands);
        contents.put("expressionOperators", operators);

        return new LogFile(contents, LogType.VARIABLE_VALUE_ASSIGNMENT);
    }

    public static Map<String, Object> literal(Object value) {
        Map<String, Object> operand = new HashMap<>();

        operand.put("source", "LITERAL");
        operand.put("value", value);

        return operand;
    }

    public static Map<String, Object> variable(String name) {
        Map<String, Object> operand = new HashMap<>();

        operand.put("source", "VARIABLE");
        operand.put("variableName", name);

        return operand;
    }
}