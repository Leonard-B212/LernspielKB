package de.lernspiel.level.dto;

/**
 * Kompakte Darstellung eines Levels für Übersichtsseiten.
 * Enthält alle Informationen, die Skilltree und andere Übersichten benötigen.
 */
public class LevelOverviewResponse {

    private Integer levelID;
    private String levelName;
    private String levelDescription;

    private Integer categoryID;
    private String category;
    private Integer categoryOrder;

    private Integer levelNumber;
    private Integer languageID;
    private String language;

    public LevelOverviewResponse(Integer levelID, String levelName, String levelDescription,
            Integer categoryID, String category, Integer categoryOrder,
            Integer levelNumber, Integer languageID, String language) {

        this.levelID = levelID;
        this.levelName = levelName;
        this.levelDescription = levelDescription;
        this.categoryID = categoryID;
        this.category = category;
        this.categoryOrder = categoryOrder;
        this.levelNumber = levelNumber;
        this.languageID = languageID;
        this.language = language;
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

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Integer getLanguageID() {
        return languageID;
    }

    public void setLanguageID(Integer languageID) {
        this.languageID = languageID;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}