package com.example.cnsmsclient.data;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;

import com.example.cnsmsclient.model.LoginRequest;
import com.example.cnsmsclient.model.LoginResponse;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.network.NetworkModule;
import com.example.cnsmsclient.util.PrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {

    private final ApiService apiService;
    private final PrefsManager prefsManager;

    public LoginRepository(Application application) {
        this.apiService = NetworkModule.getApiService(application);
        this.prefsManager = new PrefsManager(application);
    }

    public void login(String email, String password, MutableLiveData<Boolean> success, MutableLiveData<String> error) {
        // Create the request object that the ApiService now expects
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    prefsManager.saveToken(response.body().getToken());
                    success.postValue(true);
                } else {
                    error.postValue("Login failed. Invalid credentials or server error.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }
}
