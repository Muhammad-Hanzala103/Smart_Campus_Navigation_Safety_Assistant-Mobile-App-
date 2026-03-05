package com.example.cnsmsclient.network;

import android.content.Context;
import com.example.cnsmsclient.util.PrefsManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Optimized ApiClient using a Singleton pattern to prevent resource exhaustion.
 */
public class ApiClient {

    private static Retrofit retrofit = null;
    private static String lastBaseUrl = null;

    public static synchronized ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }

    public static synchronized Retrofit getClient(Context context) {
        PrefsManager prefsManager = new PrefsManager(context.getApplicationContext());
        String baseUrl = prefsManager.getBaseUrl();

        // Recreate client only if base URL changes or if it hasn't been initialized
        if (retrofit == null || !baseUrl.equals(lastBaseUrl)) {
            lastBaseUrl = baseUrl;

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(prefsManager))
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(15, TimeUnit.SECONDS) // Slightly reduced timeout for better UX
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
