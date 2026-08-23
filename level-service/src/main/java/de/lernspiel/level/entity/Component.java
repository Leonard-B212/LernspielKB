package de.lernspiel.level.entity;

import de.lernspiel.common.code.CodeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Repräsentiert einen verfügbaren Code-Baustein für Level.
 *
 * Der Typ eines Bausteins basiert auf dem gemeinsamen CodeType aus dem
 * common-Modul. Dadurch verwenden Level-Service und Interpreter dieselben
 * Bezeichnungen für Code-Blöcke.
 */
@Entity
@Table(name = "component")
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer componentID;

    /**
     * Legt fest, um welchen Code-Baustein es sich handelt,
     * beispielsweise INT, VALUE, ADD oder BREAK.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CodeType componentType;

    /**
     * Optionale Beschreibung des Bausteins.
     */
    private String componentDescription;

    public Integer getComponentID() {
        return componentID;
    }

    public void setComponentID(Integer componentID) {
        this.componentID = componentID;
    }

    public CodeType getComponentType() {
        return componentType;
    }

    public void setComponentType(CodeType componentType) {
        this.componentType = componentType;
    }

    public String getComponentDescription() {
        return componentDescription;
    }

    public void setComponentDescription(String componentDescription) {
        this.componentDescription = componentDescription;
    }
}