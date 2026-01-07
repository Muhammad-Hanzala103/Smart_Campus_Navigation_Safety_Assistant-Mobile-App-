package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private UserProfile user;

    public String getToken() {
        return token;
    }

    public UserProfile getUser() {
        return user;
    }
}
