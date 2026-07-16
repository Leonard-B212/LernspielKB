package de.lernspiel.auth.dto;

//Objekt um Loginanfragen zu handeln -selbsterklärend / Boilerplate

public class LoginRequest{
    private int userID;
    private String password;

    public LoginRequest() {}
    
    public LoginRequest(int userID, String password) {
        this.userID = userID;
        this.password = password;
    }
    
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
