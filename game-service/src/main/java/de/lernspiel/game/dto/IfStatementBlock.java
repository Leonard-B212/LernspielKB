package de.lernspiel.game.dto;

import java.util.List;

public class IfStatementBlock extends CodeBlock {
    private List<CodeBlock> expression;
    private List<CodeBlock> program;

    public List<CodeBlock> getExpression() {
        return expression;
    }

    public List<CodeBlock> getProgram() {
        return program;
    }
}