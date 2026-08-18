package de.lernspiel.level.dto;

import de.lernspiel.common.code.CodeType;

/**
 * DTO für einen in einem Level verfügbaren Code-Baustein.
 */
public class LevelComponentResponse {

    private CodeType type;
    private Integer amount;

    public LevelComponentResponse(CodeType type, Integer amount) {
        this.type = type;
        this.amount = amount;
    }

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