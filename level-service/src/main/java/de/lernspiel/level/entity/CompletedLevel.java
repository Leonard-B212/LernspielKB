package de.lernspiel.level.entity;

import jakarta.persistence.*;

/**
 * Speichert den Abschluss eines Levels durch einen Benutzer.
 *
 * Die UserID wird bewusst direkt gespeichert, damit der level-service
 * keine Abhängigkeit zum auth-service benötigt.
 *
 * Die Programmiersprache muss nicht zusätzlich gespeichert werden,
 * da sie bereits über das zugehörige Level eindeutig festgelegt ist.
 */
@Entity
@Table(
    name = "completed_level",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_completed_level_user_level",
            columnNames = {
                "user_id",
                "level_id"
            }
        )
    }
)
public class CompletedLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer completedLevelID;

    /**
     * ID des Benutzers, der das Level abgeschlossen hat.
     * Der Benutzer selbst wird im auth-service verwaltet.
     */
    @Column(
        name = "user_id",
        nullable = false
    )
    private Integer userID;

    /**
     * Das abgeschlossene Level.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "level_id",
        nullable = false
    )
    private Level level;


    public Integer getCompletedLevelID() {
        return completedLevelID;
    }

    public void setCompletedLevelID(Integer completedLevelID) {
        this.completedLevelID = completedLevelID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}