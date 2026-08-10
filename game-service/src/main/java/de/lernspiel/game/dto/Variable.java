package de.lernspiel.game.dto;

public class Variable<T> {
    private T value;
    private CodeType type;

    public Variable(T value, CodeType type) {
        this.value = value;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public CodeType getType() {
        return type;
    }

    public void setType(CodeType type) {
        this.type = type;
    }

}