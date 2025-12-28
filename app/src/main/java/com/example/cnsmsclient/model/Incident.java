package com.example.cnsmsclient.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "incidents")
public class Incident {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("server_id")
    public String serverId;

    @SerializedName("description")
    public String description;

    @SerializedName("category")
    public String category;

    @SerializedName("status")
    public String status;

    @SerializedName("created_at")
    public String createdAt;

    public Incident(String serverId, String description, String category, String status, String createdAt) {
        this.serverId = serverId;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
    }
}
