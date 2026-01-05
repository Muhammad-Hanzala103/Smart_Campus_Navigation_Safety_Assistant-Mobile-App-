package com.example.cnsmsclient.util;

import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Helper class for managing app theme (dark/light mode).
 * Provides methods to toggle theme and persist preference.
 */
public class ThemeHelper {

    public static final int MODE_SYSTEM = 0;
    public static final int MODE_LIGHT = 1;
    public static final int MODE_DARK = 2;

    /**
     * Apply theme based on saved preference
     */
    public static void applyTheme(Context context) {
        PrefsManager prefs = new PrefsManager(context);
        int themeMode = prefs.getThemeMode();
        applyThemeMode(themeMode);
    }

    /**
     * Apply specific theme mode
     */
    public static void applyThemeMode(int mode) {
        switch (mode) {
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case MODE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Toggle between dark and light mode
     */
    public static void toggleDarkMode(Context context) {
        PrefsManager prefs = new PrefsManager(context);
        boolean isDark = prefs.isDarkMode();
        prefs.setDarkMode(!isDark);
        applyThemeMode(isDark ? MODE_LIGHT : MODE_DARK);
    }

    /**
     * Set dark mode and save preference
     */
    public static void setDarkMode(Context context, boolean enabled) {
        PrefsManager prefs = new PrefsManager(context);
        prefs.setDarkMode(enabled);
        applyThemeMode(enabled ? MODE_DARK : MODE_LIGHT);
    }

    /**
     * Check if system is currently in dark mode
     */
    public static boolean isSystemDarkMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Check if app is currently in dark mode
     */
    public static boolean isDarkMode(Context context) {
        PrefsManager prefs = new PrefsManager(context);
        int themeMode = prefs.getThemeMode();

        if (themeMode == MODE_SYSTEM) {
            return isSystemDarkMode(context);
        }
        return themeMode == MODE_DARK;
    }

    /**
     * Get theme mode display name
     */
    public static String getThemeModeName(int mode) {
        switch (mode) {
            case MODE_LIGHT:
                return "Light";
            case MODE_DARK:
                return "Dark";
            case MODE_SYSTEM:
                return "System Default";
            default:
                return "Unknown";
        }
    }
}
