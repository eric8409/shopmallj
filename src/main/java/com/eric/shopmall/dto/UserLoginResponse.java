package com.eric.shopmall.dto;


public class UserLoginResponse {
    private String token;
    private Integer userId;
    private String email;
    // 您可以根據需要加入其他資訊，例如角色列表

    public UserLoginResponse(String token, Integer userId, String email) {
        this.token = token;
        this.userId = userId;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}