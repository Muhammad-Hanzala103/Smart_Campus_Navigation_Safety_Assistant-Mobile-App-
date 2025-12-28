package com.example.cnsmsclient.network;

import com.example.cnsmsclient.util.PrefsManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final PrefsManager prefsManager;

    public AuthInterceptor(PrefsManager prefsManager) {
        this.prefsManager = prefsManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        // Do not attach token for auth-related endpoints
        if (path.contains("/api/login") || path.contains("/api/register") || path.contains("/api/password-reset")) {
            return chain.proceed(originalRequest);
        }

        String token = prefsManager.getToken();
        if (token == null) {
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}
