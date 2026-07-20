package de.lernspiel.auth.entity;

import jakarta.persistence.*;

/*
 * Entityklasse für Schulklassen.
 * Eine Schulklasse verbindet einen Lehrer mit mehreren Schülern.
 */
@Entity
@Table(name = "school_class")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classID;

    @Column(nullable = false, unique = true)
    private String className;

    @Column(nullable = false)
    private Integer teacherID;

    public SchoolClass() {
    }

    public SchoolClass(String className, Integer teacherID) {
        this.className = className;
        this.teacherID = teacherID;
    }

    public Integer getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public Integer getTeacherID() {
        return teacherID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTeacherID(Integer teacherID) {
        this.teacherID = teacherID;
    }
}