package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

public class IncidentResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("image_url")
    private String imageUrl;

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
