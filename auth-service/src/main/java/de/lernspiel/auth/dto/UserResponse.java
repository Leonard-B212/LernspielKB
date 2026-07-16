package de.lernspiel.auth.dto;

public class UserResponse {

    private int userID;
    private String type;
    private Integer classID;

    public UserResponse(int userID, String type, Integer classID) {
        this.userID = userID;
        this.type = type;
        this.classID = classID;
    }

    public int getUserID() {
        return userID;
    }

    public String getType() {
        return type;
    }

    public Integer getClassID() {
        return classID;
    }
}
