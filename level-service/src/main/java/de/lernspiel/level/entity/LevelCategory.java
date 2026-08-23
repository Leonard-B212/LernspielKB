package de.lernspiel.level.entity;

import jakarta.persistence.*;

/**
 * Repräsentiert eine fachliche Kategorie von Leveln.
 *
 * Kategorien bilden eine gemeinsame Grundstruktur für alle Programmiersprachen.
 * Eine Sprache kann einzelne Kategorien auslassen, die Reihenfolge der vorhandenen
 * Kategorien ist jedoch sprachübergreifend identisch.
 *
 * Falls zukünftig unterschiedliche Kategorie-Reihenfolgen pro Programmiersprache
 * benötigt werden, muss die Reihenfolge sprachabhängig modelliert werden.
 */
@Entity
@Table(
        name = "level_category",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_level_category_name",
                        columnNames = "category_name"
                )
        }
)
public class LevelCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_order", nullable = false)
    private Integer categoryOrder;

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryOrder() {
        return categoryOrder;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}