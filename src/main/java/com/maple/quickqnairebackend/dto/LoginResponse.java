package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/11/30 22:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
public class LoginResponse {
    private String token;
    private String message;

    public LoginResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
