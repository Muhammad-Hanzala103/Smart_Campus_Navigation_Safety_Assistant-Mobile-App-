package com.example.cnsmsclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class PrefsManager {

    private static final String PREFS_FILE_NAME = "cnsms_secure_prefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_BASE_URL = "base_url";
    private static final String KEY_DEMO_MODE = "demo_mode";
    public static final String DEFAULT_BASE_URL = "http://192.168.0.109:5000/";

    private SharedPreferences sharedPreferences;

    public PrefsManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            sharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME + "_unsecured", Context.MODE_PRIVATE);
        }
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void saveBaseUrl(String url) {
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

    public void clear() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply();
    }
}
