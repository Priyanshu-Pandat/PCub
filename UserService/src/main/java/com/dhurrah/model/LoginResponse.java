package com.dhurrah.model;

public class LoginResponse {

    private String token;
    private boolean newUser;

    public LoginResponse() {
        // Default constructor needed for serialization
    }

    public LoginResponse(String token, boolean newUser) {
        this.token = token;
        this.newUser = newUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
}
