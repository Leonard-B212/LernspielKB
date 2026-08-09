package de.lernspiel.game.entity;

import jakarta.persistence.Entity;

@Entity
public class CodeBlock {
    private int type;

    public int getType(){
        return type;
    }
}