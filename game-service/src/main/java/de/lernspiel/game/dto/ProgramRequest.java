package de.lernspiel.game.dto;

import java.util.List;

public class ProgramRequest {

    private int userId;
    private int levelId;
    private int languageId;
    private List<CodeBlock> program;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public List<CodeBlock> getProgram() {
        return program;
    }

    public void setProgram(List<CodeBlock> program) {
        this.program = program;
    }
}