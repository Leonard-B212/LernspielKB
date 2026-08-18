package de.lernspiel.level.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "level_component",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_level_component",
            columnNames = {
                "level_id",
                "component_id"
            }
        )
    }
)
public class LevelComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer levelComponentID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "level_id",
        nullable = false
    )
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "component_id",
        nullable = false
    )
    private Component component;

    @Column(nullable = false)
    private Integer componentAmount;


    public Integer getLevelComponentID() {
        return levelComponentID;
    }

    public void setLevelComponentID(Integer levelComponentID) {
        this.levelComponentID = levelComponentID;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Integer getComponentAmount() {
        return componentAmount;
    }

    public void setComponentAmount(Integer componentAmount) {
        this.componentAmount = componentAmount;
    }
}