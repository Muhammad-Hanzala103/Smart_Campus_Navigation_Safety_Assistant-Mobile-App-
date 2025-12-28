package com.example.cnsmsclient.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREFS_NAME = "CNSMS_CLIENT_PREFS";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_BASE_URL = "base_url";
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:5000/";

    private final SharedPreferences sharedPreferences;

    public PrefsManager(Context context) {
        // For a production app, this should be replaced with EncryptedSharedPreferences
        // to provide a higher level of security for the stored JWT.
        // Example:
        // MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
        // this.sharedPreferences = EncryptedSharedPreferences.create(...);
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void saveBaseUrl(String baseUrl) {
        sharedPreferences.edit().putString(KEY_BASE_URL, baseUrl).apply();
    }

    public String getBaseUrl() {
        return sharedPreferences.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public void clear() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }
}
