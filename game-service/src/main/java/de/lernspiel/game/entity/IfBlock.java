package de.lernspiel.game.entity;

import java.util.List;

import jakarta.persistence.Entity;

@Entity
public class IfBlock extends CodeBlock{
    private ExpressionBlock expression;
    private List<CodeBlock> program;

    public ExpressionBlock getExpression(){
        return expression;
    }

    public List<CodeBlock> getProgram(){
        return program;
    }
}