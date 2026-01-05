package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model for Emergency Contacts
 */
public class EmergencyContact {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("type")
    private String type; // security, police, ambulance, fire, hospital

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("icon")
    private String icon;

    // Default constructor
    public EmergencyContact() {
    }

    public EmergencyContact(String name, String phone, String type) {
        this.name = name;
        this.phone = phone;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Get color resource based on contact type
     */
    public int getTypeColor() {
        if (type == null)
            return 0xFF666666;
        switch (type.toLowerCase()) {
            case "security":
                return 0xFF2196F3; // Blue
            case "police":
                return 0xFF1565C0; // Dark Blue
            case "ambulance":
                return 0xFFE53935; // Red
            case "fire":
                return 0xFFFF5722; // Deep Orange
            case "hospital":
                return 0xFF4CAF50; // Green
            default:
                return 0xFF666666; // Grey
        }
    }

    /**
     * Response wrapper for emergency contacts list
     */
    public static class EmergencyContactsResponse {
        @SerializedName("contacts")
        private List<EmergencyContact> contacts;

        public List<EmergencyContact> getContacts() {
            return contacts;
        }

        public void setContacts(List<EmergencyContact> contacts) {
            this.contacts = contacts;
        }
    }
}
