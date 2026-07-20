package de.lernspiel.auth.dto;

public class SchoolClassResponse {

    private Integer classID;
    private String className;
    private Integer teacherID;

    public SchoolClassResponse(
            Integer classID,
            String className,
            Integer teacherID
    ) {
        this.classID = classID;
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
}