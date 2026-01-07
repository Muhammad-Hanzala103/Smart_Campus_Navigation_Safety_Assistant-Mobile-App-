package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for map nodes/locations on campus.
 */
public class MapNode {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("node_type")
    private String nodeType;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("x")
    private int x;

    @SerializedName("y")
    private int y;

    @SerializedName("building")
    private String building;

    @SerializedName("floor")
    private String floor;

    @SerializedName("description")
    private String description;

    @SerializedName("is_accessible")
    private boolean isAccessible;

    @SerializedName("is_emergency_exit")
    private boolean isEmergencyExit;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getBuilding() {
        return building;
    }

    public String getFloor() {
        return floor;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public boolean isEmergencyExit() {
        return isEmergencyExit;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    public void setEmergencyExit(boolean emergencyExit) {
        isEmergencyExit = emergencyExit;
    }

    @Override
    public String toString() {
        return name + " (" + nodeType + ")";
    }
}
