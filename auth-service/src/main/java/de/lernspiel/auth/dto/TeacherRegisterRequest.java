package de.lernspiel.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class TeacherRegisterRequest {

    @NotBlank
    private String password;

    public TeacherRegisterRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}