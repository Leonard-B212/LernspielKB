package de.lernspiel.game.dto;

public class Variable<T> {
    private final T value;
    private final CodeType type;

    public Variable(T value, CodeType type) {
        this.value = value;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public CodeType getType() {
        return type;
    }
}