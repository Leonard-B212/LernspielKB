package de.lernspiel.auth.dto;

import jakarta.validation.constraints.NotNull;
import de.lernspiel.auth.entity.UserType;

public class RegisterRequest {

    @NotNull
    private UserType type;

    private Integer classID;

    @NotBlank
    private String password;

    public RegisterRequest() {}

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public Integer getClassID() {
        return classID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
