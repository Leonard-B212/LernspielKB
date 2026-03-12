package de.lernspiel.auth.dto;

public class UserResponse {

    private int id;
    private String username;
    private String email;
    private String profilePicture;

    public UserResponse(int id, String username, String email, String profilePicture) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}