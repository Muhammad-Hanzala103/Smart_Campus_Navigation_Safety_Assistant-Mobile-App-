package com.example.cnsmsclient.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "incidents")
public class Incident {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public int id;

    @SerializedName("description")
    public String description;

    @SerializedName("category")
    public String category;

    @SerializedName("status")
    public String status;

    @SerializedName("severity")
    public String severity;
}
