package de.lernspiel.level.dto;

import java.util.List;

/**
 * DTO für die vollständigen Daten eines Levels,
 * die vom Frontend zum Aufbau des Levels benötigt werden.
 */
public class LevelResponse {

    private Integer levelID;
    private String levelName;
    private String levelDescription;
    private String category;
    private Integer levelNumber;
    private String language;
    private List<LevelComponentResponse> components;

    public LevelResponse(
            Integer levelID,
            String levelName,
            String levelDescription,
            String category,
            Integer levelNumber,
            String language,
            List<LevelComponentResponse> components) {

        this.levelID = levelID;
        this.levelName = levelName;
        this.levelDescription = levelDescription;
        this.category = category;
        this.levelNumber = levelNumber;
        this.language = language;
        this.components = components;
    }

    public Integer getLevelID() {
        return levelID;
    }

    public void setLevelID(Integer levelID) {
        this.levelID = levelID;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<LevelComponentResponse> getComponents() {
        return components;
    }

    public void setComponents(List<LevelComponentResponse> components) {
        this.components = components;
    }
}