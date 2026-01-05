package com.example.cnsmsclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.example.cnsmsclient.model.UserProfile;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Enhanced Preferences Manager for secure storage of user data and app
 * settings.
 * Uses EncryptedSharedPreferences for sensitive data.
 */
public class PrefsManager {

    private static final String PREFS_FILE_NAME = "cnsms_secure_prefs";

    // Authentication Keys
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_BASE_URL = "base_url";
    private static final String KEY_DEMO_MODE = "demo_mode";
    private static final String KEY_REMEMBER_ME = "remember_me";

    // User Profile Keys
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_PROFILE_PHOTO_URL = "profile_photo_url";

    // Settings Keys
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_THEME_MODE = "theme_mode"; // 0=system, 1=light, 2=dark
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_FONT_SCALE = "font_scale";

    // Notification Keys
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_EMERGENCY_ALERTS = "emergency_alerts";
    private static final String KEY_INCIDENT_UPDATES = "incident_updates";
    private static final String KEY_BOOKING_REMINDERS = "booking_reminders";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    // Session Keys
    private static final String KEY_LAST_ACTIVITY = "last_activity";
    private static final String KEY_SESSION_TIMEOUT_MINUTES = "session_timeout";

    // First Run
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    // Default Values
    public static final String DEFAULT_BASE_URL = "http://192.168.0.109:5000/";
    public static final int DEFAULT_SESSION_TIMEOUT = 30; // minutes
    public static final String DEFAULT_LANGUAGE = "en";

    private SharedPreferences sharedPreferences;
    private final Gson gson;

    public PrefsManager(Context context) {
        gson = new Gson();
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME + "_unsecured", Context.MODE_PRIVATE);
        }
    }

    // ==================== AUTHENTICATION ====================

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
        // Set token expiry to 24 hours from now
        long expiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        sharedPreferences.edit().putLong(KEY_TOKEN_EXPIRY, expiry).apply();
    }

    public String getToken() {
        // Check if token is expired
        if (isTokenExpired()) {
            clear();
            return null;
        }
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public boolean isTokenExpired() {
        long expiry = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0);
        return expiry > 0 && System.currentTimeMillis() > expiry;
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public void saveBaseUrl(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        sharedPreferences.edit().putString(KEY_BASE_URL, url).apply();
    }

    public String getBaseUrl() {
        return sharedPreferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public void setDemoMode(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DEMO_MODE, enabled).apply();
    }

    public boolean isDemoMode() {
        return sharedPreferences.getBoolean(KEY_DEMO_MODE, false);
    }

    public void setRememberMe(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, enabled).apply();
    }

    public boolean isRememberMe() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    // ==================== USER PROFILE ====================

    public void saveUserProfile(UserProfile profile) {
        String json = gson.toJson(profile);
        sharedPreferences.edit().putString(KEY_USER_PROFILE, json).apply();

        // Also save individual fields for quick access
        if (profile != null) {
            sharedPreferences.edit()
                    .putInt(KEY_USER_ID, profile.getId())
                    .putString(KEY_USER_NAME, profile.getName())
                    .putString(KEY_USER_EMAIL, profile.getEmail())
                    .putString(KEY_USER_PHONE, profile.getPhone())
                    .putString(KEY_USER_ROLE, profile.getRole())
                    .putString(KEY_PROFILE_PHOTO_URL, profile.getProfilePhotoUrl())
                    .apply();
        }
    }

    public UserProfile getUserProfile() {
        String json = sharedPreferences.getString(KEY_USER_PROFILE, null);
        if (json != null) {
            return gson.fromJson(json, UserProfile.class);
        }
        return null;
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "student");
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(getUserRole());
    }

    public boolean isSecurity() {
        String role = getUserRole();
        return "security".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
    }

    public String getProfilePhotoUrl() {
        return sharedPreferences.getString(KEY_PROFILE_PHOTO_URL, null);
    }

    // ==================== THEME SETTINGS ====================

    public void setDarkMode(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        setThemeMode(enabled ? ThemeHelper.MODE_DARK : ThemeHelper.MODE_LIGHT);
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setThemeMode(int mode) {
        sharedPreferences.edit().putInt(KEY_THEME_MODE, mode).apply();
    }

    public int getThemeMode() {
        return sharedPreferences.getInt(KEY_THEME_MODE, ThemeHelper.MODE_SYSTEM);
    }

    // ==================== BIOMETRIC SETTINGS ====================

    public void setBiometricEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }

    public boolean isBiometricEnabled() {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    // ==================== LANGUAGE SETTINGS ====================

    public void setLanguage(String languageCode) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    public void setFontScale(float scale) {
        sharedPreferences.edit().putFloat(KEY_FONT_SCALE, scale).apply();
    }

    public float getFontScale() {
        return sharedPreferences.getFloat(KEY_FONT_SCALE, 1.0f);
    }

    // ==================== NOTIFICATION SETTINGS ====================

    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setEmergencyAlertsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_EMERGENCY_ALERTS, enabled).apply();
    }

    public boolean isEmergencyAlertsEnabled() {
        return sharedPreferences.getBoolean(KEY_EMERGENCY_ALERTS, true);
    }

    public void setIncidentUpdatesEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_INCIDENT_UPDATES, enabled).apply();
    }

    public boolean isIncidentUpdatesEnabled() {
        return sharedPreferences.getBoolean(KEY_INCIDENT_UPDATES, true);
    }

    public void setBookingRemindersEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_BOOKING_REMINDERS, enabled).apply();
    }

    public boolean isBookingRemindersEnabled() {
        return sharedPreferences.getBoolean(KEY_BOOKING_REMINDERS, true);
    }

    public void saveFCMToken(String token) {
        sharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public String getFCMToken() {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null);
    }

    // ==================== SESSION MANAGEMENT ====================

    public void updateLastActivity() {
        sharedPreferences.edit().putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis()).apply();
    }

    public long getLastActivity() {
        return sharedPreferences.getLong(KEY_LAST_ACTIVITY, 0);
    }

    public void setSessionTimeout(int minutes) {
        sharedPreferences.edit().putInt(KEY_SESSION_TIMEOUT_MINUTES, minutes).apply();
    }

    public int getSessionTimeout() {
        return sharedPreferences.getInt(KEY_SESSION_TIMEOUT_MINUTES, DEFAULT_SESSION_TIMEOUT);
    }

    public boolean isSessionExpired() {
        long lastActivity = getLastActivity();
        if (lastActivity == 0)
            return false;

        long timeoutMillis = getSessionTimeout() * 60 * 1000L;
        return System.currentTimeMillis() - lastActivity > timeoutMillis;
    }

    // ==================== FIRST RUN ====================

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(KEY_FIRST_RUN, true);
    }

    public void setFirstRunComplete() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
    }

    public boolean isOnboardingComplete() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    public void setOnboardingComplete() {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply();
    }

    // ==================== CLEAR DATA ====================

    public void clear() {
        // Keep certain settings even after logout
        String baseUrl = getBaseUrl();
        boolean darkMode = isDarkMode();
        int themeMode = getThemeMode();
        String language = getLanguage();
        boolean onboardingComplete = isOnboardingComplete();

        sharedPreferences.edit().clear().apply();

        // Restore non-user settings
        saveBaseUrl(baseUrl);
        setDarkMode(darkMode);
        setThemeMode(themeMode);
        setLanguage(language);
        if (onboardingComplete)
            setOnboardingComplete();
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}
