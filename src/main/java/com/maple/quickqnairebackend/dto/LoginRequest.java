package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/11/30 22:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
public class LoginRequest {
    private String username;
    private String password;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}