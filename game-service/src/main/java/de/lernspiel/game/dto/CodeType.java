package de.lernspiel.game.dto;

/**
 * CodeType
 */
public enum CodeType {

    BREAK(0, "Break"),
    VAR_NAME(1, "VarName"),
    STRING(2, "String"),
    INT(3, "int"),
    BOOLEAN(4, "boolean"),
    IF_STATEMENT(5, "if-statement"),
    ELSE_STATEMENT(6, "else-statement"),
    EQUALS(7, "="),
    ADD(8, "+"),
    SUBTRACT(9, "-"),
    MULTIPLY(10, "*"),
    DIVIDE(11, "/"),
    VALUE(12, "value");

    private final int id;
    private final String label;

    CodeType(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}