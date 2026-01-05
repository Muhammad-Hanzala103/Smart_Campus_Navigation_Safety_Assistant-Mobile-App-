package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model for push notifications
 */
public class NotificationItem {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("type")
    private String type; // incident, booking, emergency, announcement, general

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("data")
    private NotificationData data;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    /**
     * Get icon resource based on notification type
     */
    public int getIconResource() {
        if (type == null)
            return android.R.drawable.ic_dialog_info;
        switch (type.toLowerCase()) {
            case "incident":
                return android.R.drawable.ic_dialog_alert;
            case "emergency":
                return android.R.drawable.ic_delete;
            case "booking":
                return android.R.drawable.ic_menu_agenda;
            case "announcement":
                return android.R.drawable.ic_dialog_info;
            default:
                return android.R.drawable.ic_dialog_info;
        }
    }

    /**
     * Nested class for notification data payload
     */
    public static class NotificationData {
        @SerializedName("incident_id")
        private Integer incidentId;

        @SerializedName("booking_id")
        private Integer bookingId;

        @SerializedName("action")
        private String action;

        public Integer getIncidentId() {
            return incidentId;
        }

        public void setIncidentId(Integer incidentId) {
            this.incidentId = incidentId;
        }

        public Integer getBookingId() {
            return bookingId;
        }

        public void setBookingId(Integer bookingId) {
            this.bookingId = bookingId;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
