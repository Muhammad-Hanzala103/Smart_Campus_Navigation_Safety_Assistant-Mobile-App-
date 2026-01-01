package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
