package de.lernspiel.game.dto;

public class ValueBlock extends CodeBlock {
    private Variable<?> value;

    public Variable<?> getValue() {
        return value;
    }
    public void setValue(Variable<?> value) {
        this.value = value;
    }
}