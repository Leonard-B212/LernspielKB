package de.lernspiel.auth.dto;

import de.lernspiel.auth.entity.UserType;

public class UserResponse {

    private int userID;
    private UserType type;
    private Integer classID;

    public UserResponse(int userID, UserType type, Integer classID) {
        this.userID = userID;
        this.type = type;
        this.classID = classID;
    }

    public int getUserID() {
        return userID;
    }

    public UserType getType() {
        return type;
    }

    public Integer getClassID() {
        return classID;
    }
}
