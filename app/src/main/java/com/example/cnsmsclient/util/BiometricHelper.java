package com.example.cnsmsclient.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

/**
 * Helper class for biometric authentication (fingerprint/face recognition).
 * Handles availability checking, prompt display, and authentication callbacks.
 */
public class BiometricHelper {

    private static final String TAG = "BiometricHelper";
    private final Context context;

    public interface BiometricCallback {
        void onSuccess();

        void onError(String errorMessage);

        void onFailed();
    }

    public BiometricHelper(Context context) {
        this.context = context;
    }

    /**
     * Check if biometric authentication is available on this device
     */
    public boolean isBiometricAvailable() {
        return isBiometricAvailable(context);
    }

    /**
     * Static version for utility usage
     */
    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK |
                        BiometricManager.Authenticators.BIOMETRIC_STRONG);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * Get detailed biometric availability status
     */
    public static BiometricStatus getBiometricStatus(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK |
                        BiometricManager.Authenticators.BIOMETRIC_STRONG);

        switch (canAuthenticate) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return BiometricStatus.AVAILABLE;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return BiometricStatus.NO_HARDWARE;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return BiometricStatus.HARDWARE_UNAVAILABLE;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return BiometricStatus.NOT_ENROLLED;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                return BiometricStatus.SECURITY_UPDATE_REQUIRED;
            default:
                return BiometricStatus.UNKNOWN;
        }
    }

    /**
     * Show biometric authentication prompt
     */
    public void showBiometricPrompt(
            String title,
            String subtitle,
            BiometricCallback callback) {

        if (context instanceof FragmentActivity) {
            authenticate((FragmentActivity) context, title, subtitle, "Cancel", callback);
        } else {
            callback.onError("Context must be an Activity to show biometric prompt");
        }
    }

    /**
     * Show biometric authentication prompt (Static)
     */
    public static void authenticate(
            FragmentActivity activity,
            String title,
            String subtitle,
            String negativeButtonText,
            BiometricCallback callback) {
        if (!isBiometricAvailable(activity)) {
            callback.onError("Biometric authentication not available on this device");
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(activity);

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Log.d(TAG, "Biometric authentication succeeded");
                        callback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Log.e(TAG, "Biometric authentication error: " + errString);
                        callback.onError(errString.toString());
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Log.w(TAG, "Biometric authentication failed");
                        callback.onFailed();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_WEAK |
                                BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    /**
     * Show default login biometric prompt
     */
    public static void showLoginPrompt(FragmentActivity activity, BiometricCallback callback) {
        authenticate(
                activity,
                "Login with Biometric",
                "Use your fingerprint or face to login",
                "Use Password",
                callback);
    }

    /**
     * Enum for biometric availability status
     */
    public enum BiometricStatus {
        AVAILABLE("Biometric authentication is available"),
        NO_HARDWARE("No biometric hardware on this device"),
        HARDWARE_UNAVAILABLE("Biometric hardware is currently unavailable"),
        NOT_ENROLLED("No biometric credentials enrolled. Please set up fingerprint or face in device settings."),
        SECURITY_UPDATE_REQUIRED("Security update required for biometric"),
        UNKNOWN("Unknown biometric status");

        private final String message;

        BiometricStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
