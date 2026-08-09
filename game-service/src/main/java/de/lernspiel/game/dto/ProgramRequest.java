package de.lernspiel.game.dto;

import java.util.List;

public class ProgramRequest {
    private int userId;
    private int levelId;
    private int languageId;
    private List<CodeBlock> program;

    public int getUserId(){
        return userId;
    }

    public int getLevelId(){
        return levelId;
    }

    public int getLanguageId(){
        return languageId;
    }

    public List<CodeBlock> getProgram(){
        return program;
    }
}
