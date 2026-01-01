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
        String token = prefsManager.getToken();

        // Do not attach token to auth endpoints
        String path = originalRequest.url().encodedPath();
        if (token == null || path.contains("/api/auth/")) {
            return chain.proceed(originalRequest);
        }

        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);

        return chain.proceed(builder.build());
    }
}
