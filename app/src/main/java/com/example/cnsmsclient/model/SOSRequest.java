package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for SOS emergency alerts
 */
public class SOSRequest {

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("message")
    private String message;

    @SerializedName("alert_type")
    private String alertType; // security, medical, fire, other

    @SerializedName("is_silent")
    private boolean isSilent;

    public SOSRequest() {
    }

    public SOSRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.alertType = "security";
        this.isSilent = false;
    }

    public SOSRequest(double latitude, double longitude, String message, String alertType, boolean isSilent) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.alertType = alertType;
        this.isSilent = isSilent;
    }

    public SOSRequest(double latitude, double longitude, String message, String alertType) {
        this(latitude, longitude, message, alertType, false);
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }
}
