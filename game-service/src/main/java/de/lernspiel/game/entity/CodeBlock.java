package de.lernspiel.game.entity;

import jakarta.persistence.Entity;

@Entity
public class CodeBlock {
    private CodeType type;

    public CodeType getType(){
        return type;
    }
}