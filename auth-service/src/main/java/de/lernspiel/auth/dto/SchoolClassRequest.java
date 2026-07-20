package de.lernspiel.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SchoolClassRequest {

    @NotBlank(message = "Der Klassenname darf nicht leer sein.")
    private String className;

    @NotNull(message = "Die Teacher-ID darf nicht fehlen.")
    private Integer teacherID;

    public SchoolClassRequest() {
    }

    public String getClassName() {
        return className;
    }

    public Integer getTeacherID() {
        return teacherID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTeacherID(Integer teacherID) {
        this.teacherID = teacherID;
    }
}