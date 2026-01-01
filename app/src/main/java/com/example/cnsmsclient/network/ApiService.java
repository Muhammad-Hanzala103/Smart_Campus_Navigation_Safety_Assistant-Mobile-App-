package com.example.cnsmsclient.network;

import com.example.cnsmsclient.model.AnalyzeResponse;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.model.LoginRequest;
import com.example.cnsmsclient.model.LoginResponse;
import com.example.cnsmsclient.model.MapData;
import com.example.cnsmsclient.model.RegisterRequest;
import com.example.cnsmsclient.model.ResetPasswordRequest;
import com.example.cnsmsclient.model.ServerResponse;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @POST("api/auth/register")
    Call<ServerResponse> register(@Body RegisterRequest registerRequest);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/forgot")
    Call<ServerResponse> forgotPassword(@Body RegisterRequest.EmailOnly email);

    @POST("api/auth/reset")
    Call<ServerResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @GET("api/map")
    Call<MapData> getMapData();

    @Multipart
    @POST("api/incidents")
    Call<Incident> createIncident(
        @Part MultipartBody.Part image,
        @Part("description") RequestBody description,
        @Part("category") RequestBody category,
        @Part("x") RequestBody x,
        @Part("y") RequestBody y
    );

    @GET("api/incidents")
    Call<List<Incident>> getIncidents();

    @POST("api/incidents/analyze")
    Call<AnalyzeResponse> analyzeIncident(@Body AnalyzeResponse.AnalyzeRequest analyzeRequest);
}
