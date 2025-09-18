package com.realestate.dto;

import com.realestate.entity.User;

public class LoginResponse {
    
    private String token;
    private User user;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}