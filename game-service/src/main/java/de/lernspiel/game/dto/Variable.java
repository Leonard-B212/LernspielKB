package de.lernspiel.game.dto;

public class Variable<T> {
    private final T value;
    private final Class<T> type;

    public Variable(T value, Class<T> type) {
        this.value = value;
        this.type = type;
    }

    public T getValue() { 
        return value;
    }

    public Class<T> getType() { 
        return type; 
    }
}