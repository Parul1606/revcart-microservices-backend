package com.revature.userservice.dto;

public class AuthResponse {
    private String token;
    private Long userId;
    private String mobile;
    private String role;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId, String mobile, String role, String message) {
        this.token = token;
        this.userId = userId;
        this.mobile = mobile;
        this.role = role;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
