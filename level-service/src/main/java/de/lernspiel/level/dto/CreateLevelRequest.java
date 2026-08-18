package de.lernspiel.level.dto;

import java.util.List;

/**
 * Enthält alle Daten, die zum Anlegen eines Levels benötigt werden.
 */
public class CreateLevelRequest {

    private String levelName;
    private String levelDescription;
    private String category;
    private Integer categoryOrder;
    private Integer levelNumber;
    private String language;
    private List<LevelComponentRequest> components;

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

    public List<LevelComponentRequest> getComponents() {
        return components;
    }

    public void setComponents(List<LevelComponentRequest> components) {
        this.components = components;
    }
    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}