package de.lernspiel.level.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.lernspiel.common.code.ExecutionLog;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Speichert ExecutionLogs als JSON und stellt sie beim Laden wieder her.
 */
@Converter
public class ExecutionLogConverter implements AttributeConverter<ExecutionLog, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ExecutionLog executionLog) {
        if (executionLog == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(executionLog);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("ExecutionLog konnte nicht als JSON gespeichert werden.", e);
        }
    }

    @Override
    public ExecutionLog convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(json, ExecutionLog.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("ExecutionLog konnte nicht aus JSON gelesen werden.", e);
        }
    }
}