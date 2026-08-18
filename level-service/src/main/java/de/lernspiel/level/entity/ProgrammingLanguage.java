package de.lernspiel.level.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "programming_language",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_programming_language_name",
            columnNames = "language_name"
        )
    }
)
public class ProgrammingLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer languageID;

    @Column(
        name = "language_name",
        nullable = false
    )
    private String languageName;


    public Integer getLanguageID() {
        return languageID;
    }

    public void setLanguageID(Integer languageID) {
        this.languageID = languageID;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}