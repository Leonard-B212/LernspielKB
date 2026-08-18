package de.lernspiel.level.dto;

import de.lernspiel.common.code.CodeType;

/**
 * Beschreibt einen Code-Baustein, der für ein Level verfügbar sein soll.
 */
public class LevelComponentRequest {

    private CodeType type;
    private Integer amount;

    public CodeType getType() {
        return type;
    }

    public void setType(CodeType type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}