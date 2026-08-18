package de.lernspiel.level.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "level",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_level_category_number_language",
            columnNames = {
                "category",
                "level_number",
                "language_id"
            }
        )
    }
)
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer levelID;

    @Column(nullable = false)
    private String levelName;

    @Column(nullable = false, length = 1000)
    private String levelDescription;

    @Column(nullable = false)
    private String category;

    @Column(name = "level_number", nullable = false)
    private Integer levelNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "language_id",
        nullable = false
    )
    private ProgrammingLanguage language;


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

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }
}