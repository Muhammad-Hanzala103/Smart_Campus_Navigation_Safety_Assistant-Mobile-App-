package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

public class Incident {
    @SerializedName("id")
    private int id;
    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private String category;
    @SerializedName("status")
    private String status;
    // Add other fields from your API as needed

    // AI Analysis fields (can be null)
    @SerializedName("severity")
    private String severity;

    public int getId() { return id; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getSeverity() { return severity; }
}
