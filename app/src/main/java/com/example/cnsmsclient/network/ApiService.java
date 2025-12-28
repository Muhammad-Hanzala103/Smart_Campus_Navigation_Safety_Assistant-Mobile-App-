package com.example.cnsmsclient.network;

import com.example.cnsmsclient.model.AnalyzeResponse;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.model.IncidentResponse;
import com.example.cnsmsclient.model.LoginResponse;
import com.example.cnsmsclient.model.RegisterResponse;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("api/password-reset-request")
    Call<Void> requestPasswordReset(@Field("email") String email);

    @FormUrlEncoded
    @POST("api/password-reset-confirm")
    Call<Void> confirmPasswordReset(@Field("email") String email, @Field("token") String token, @Field("new_password") String newPassword);

    @Multipart
    @POST("api/incidents")
    Call<IncidentResponse> createIncident(
        @Part("description") RequestBody description,
        @Part("category") RequestBody category,
        @Part("x") RequestBody x,
        @Part("y") RequestBody y,
        @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("api/incidents/analyze")
    Call<AnalyzeResponse> analyzeIncidentById(@Field("incident_id") String incidentId);

    @GET("api/incidents")
    Call<List<Incident>> getIncidents();
}
