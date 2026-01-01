package com.example.cnsmsclient.network;

import android.content.Context;
import com.example.cnsmsclient.BuildConfig;
import com.example.cnsmsclient.util.PrefsManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Default BASE_URL. Can be changed in Settings.
    public static final String DEFAULT_BASE_URL = "https://your-server-domain.com/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService(Context context) {
        // Re-create the client each time to get the latest settings
        return getClient(context).create(ApiService.class);
    }

    private static Retrofit getClient(Context context) {
        PrefsManager prefsManager = new PrefsManager(context);
        String baseUrl = prefsManager.getBaseUrl();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // Only show logs in debug builds to prevent leaking info in production
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(prefsManager))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
