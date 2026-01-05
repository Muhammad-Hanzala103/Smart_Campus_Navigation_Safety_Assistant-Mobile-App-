package com.example.cnsmsclient.model;

import com.google.gson.annotations.SerializedName;

/**
 * Complete user profile model with all user fields.
 * Used for profile display, editing, and API communication.
 */
public class UserProfile {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("role")
    private String role;

    @SerializedName("profile_photo_url")
    private String profilePhotoUrl;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("is_verified")
    private boolean isVerified;

    @SerializedName("department")
    private String department;

    // Default constructor
    public UserProfile() {
    }

    // Constructor with essential fields
    public UserProfile(String name, String email, String phone, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Get user initials for avatar placeholder
     */
    public String getInitials() {
        if (name == null || name.isEmpty())
            return "??";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    /**
     * Get formatted role display name
     */
    public String getRoleDisplayName() {
        if (role == null)
            return "User";
        switch (role.toLowerCase()) {
            case "admin":
                return "Administrator";
            case "security":
                return "Security Officer";
            case "staff":
                return "Staff Member";
            case "student":
                return "Student";
            default:
                return role;
        }
    }

    /**
     * Check if user has admin privileges
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    /**
     * Check if user is security personnel
     */
    public boolean isSecurity() {
        return "security".equalsIgnoreCase(role) || isAdmin();
    }
}
