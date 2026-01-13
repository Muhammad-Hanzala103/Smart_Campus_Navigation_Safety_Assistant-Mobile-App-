package com.example.cnsmsclient.network;

import com.example.cnsmsclient.model.AnalyzeResponse;
import com.example.cnsmsclient.model.ChangePasswordRequest;
import com.example.cnsmsclient.model.EmergencyContact;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.model.LoginRequest;
import com.example.cnsmsclient.model.LoginResponse;
import com.example.cnsmsclient.model.MapData;
import com.example.cnsmsclient.model.MapNode;
import com.example.cnsmsclient.model.NotificationItem;
import com.example.cnsmsclient.model.RegisterRequest;
import com.example.cnsmsclient.model.ResetPasswordRequest;
import com.example.cnsmsclient.model.SOSRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.model.UserProfile;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Comprehensive API Service interface for all backend endpoints.
 * Organized by feature modules.
 */
public interface ApiService {

        // ==================== AUTHENTICATION ====================

        @POST("api/auth/register")
        Call<ServerResponse> register(@Body RegisterRequest registerRequest);

        @POST("api/auth/login")
        Call<LoginResponse> login(@Body LoginRequest loginRequest);

        @POST("api/auth/forgot")
        Call<ServerResponse> forgotPassword(@Body RegisterRequest.EmailOnly email);

        @POST("api/auth/reset")
        Call<ServerResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

        @POST("api/auth/change-password")
        Call<ServerResponse> changePassword(@Body ChangePasswordRequest request);

        @POST("api/auth/logout")
        Call<ServerResponse> logout();

        @DELETE("api/auth/account")
        Call<ServerResponse> deleteAccount();

        // ==================== USER PROFILE ====================

        @GET("api/profile")
        Call<UserProfile> getProfile();

        @PUT("api/profile")
        Call<ServerResponse> updateProfile(@Body UserProfile profile);

        @Multipart
        @POST("api/profile/photo")
        Call<ServerResponse> uploadProfilePhoto(@Part MultipartBody.Part photo);

        @GET("api/profile/activity")
        Call<List<Map<String, Object>>> getActivityLog();

        // ==================== INCIDENTS ====================

        @GET("api/incidents")
        Call<List<Incident>> getIncidents();

        @GET("api/incidents")
        Call<List<Incident>> getIncidentsByStatus(@Query("status") String status);

        @GET("api/incidents/my")
        Call<List<Incident>> getMyIncidents();

        @GET("api/incidents/{id}")
        Call<Incident> getIncidentById(@Path("id") int id);

        @Multipart
        @POST("api/incidents")
        Call<Incident> createIncident(
                        @Part MultipartBody.Part image,
                        @Part("description") RequestBody description,
                        @Part("category") RequestBody category,
                        @Part("x") RequestBody x,
                        @Part("y") RequestBody y);

        @Multipart
        @POST("api/incidents/multi")
        Call<Incident> createIncidentMultiMedia(
                        @Part List<MultipartBody.Part> images,
                        @Part MultipartBody.Part video,
                        @Part MultipartBody.Part audio,
                        @Part("description") RequestBody description,
                        @Part("category") RequestBody category,
                        @Part("x") RequestBody x,
                        @Part("y") RequestBody y,
                        @Part("severity") RequestBody severity,
                        @Part("is_anonymous") RequestBody isAnonymous);

        @PUT("api/incidents/{id}/status")
        Call<ServerResponse> updateIncidentStatus(
                        @Path("id") int id,
                        @Body Map<String, String> status);

        @POST("api/incidents/{id}/comment")
        Call<ServerResponse> addIncidentComment(
                        @Path("id") int id,
                        @Body Map<String, String> comment);

        @GET("api/incidents/{id}/comments")
        Call<List<Map<String, Object>>> getIncidentComments(@Path("id") int id);

        @POST("api/incidents/analyze")
        Call<AnalyzeResponse> analyzeIncident(@Body AnalyzeResponse.AnalyzeRequest analyzeRequest);

        @POST("api/incidents/draft")
        Call<ServerResponse> saveDraftIncident(@Body Map<String, Object> draft);

        @GET("api/incidents/drafts")
        Call<List<Map<String, Object>>> getDraftIncidents();

        // ==================== MAP & NAVIGATION ====================

        @GET("api/map")
        Call<MapData> getMapData();

        @GET("api/map/nodes")
        Call<List<MapNode>> getMapNodes();

        @GET("api/map/nodes/{id}")
        Call<MapNode> getNodeById(@Path("id") int id);

        @GET("api/map/search")
        Call<List<MapNode>> searchLocations(@Query("q") String query);

        @Multipart
        @POST("api/incidents/analyze/image")
        Call<Map<String, Object>> analyzeIncidentImage(@Part MultipartBody.Part image);

        @GET("api/map/nearby")
        Call<List<MapData.MapNode>> getNearbyLocations(
                        @Query("lat") double latitude,
                        @Query("lng") double longitude,
                        @Query("type") String type,
                        @Query("radius") int radiusMeters);

        @GET("api/map/route")
        Call<MapData.RouteResponse> getRoute(
                        @Query("from") int fromNodeId,
                        @Query("to") int toNodeId);

        @GET("api/map/buildings/{id}/floors")
        Call<List<Map<String, Object>>> getBuildingFloors(@Path("id") int buildingId);

        @GET("api/map/parking/availability")
        Call<Map<String, Object>> getParkingAvailability();

        // ==================== EMERGENCY ====================

        @POST("api/emergency/sos")
        Call<ServerResponse> sendSOSAlert(@Body SOSRequest request);

        @GET("api/emergency/contacts")
        Call<EmergencyContact.EmergencyContactsResponse> getEmergencyContacts();

        @GET("api/emergency/evacuation-routes")
        Call<List<Map<String, Object>>> getEvacuationRoutes(@Query("lat") double lat, @Query("lng") double lng);

        @GET("api/emergency/safe-zones")
        Call<List<Map<String, Object>>> getSafeZones();

        @PUT("api/emergency/sos/{id}/resolve")
        Call<ServerResponse> resolveSOSAlert(@Path("id") int sosId);

        @POST("api/emergency/share-location")
        Call<ServerResponse> shareLocation(@Body Map<String, Object> locationData);

        // ==================== NOTIFICATIONS ====================

        @POST("api/notifications/register-token")
        Call<ServerResponse> registerFCMToken(@Body Map<String, String> tokenData);

        @GET("api/notifications")
        Call<List<NotificationItem>> getNotifications();

        @GET("api/notifications/unread-count")
        Call<Map<String, Integer>> getUnreadNotificationCount();

        @PUT("api/notifications/{id}/read")
        Call<ServerResponse> markNotificationRead(@Path("id") int id);

        @PUT("api/notifications/read-all")
        Call<ServerResponse> markAllNotificationsRead();

        @DELETE("api/notifications/{id}")
        Call<ServerResponse> deleteNotification(@Path("id") int id);

        // ==================== SHUTTLE & TRANSPORT ====================

        @GET("api/shuttle/live")
        Call<List<Map<String, Object>>> getShuttles();

        // ==================== ROOM BOOKING ====================

        @GET("api/rooms")
        Call<List<Map<String, Object>>> getRooms();

        @GET("api/rooms/{id}")
        Call<Map<String, Object>> getRoomById(@Path("id") int id);

        @GET("api/rooms/available")
        Call<List<Map<String, Object>>> getAvailableRooms(
                        @Query("date") String date,
                        @Query("start_time") String startTime,
                        @Query("end_time") String endTime,
                        @Query("capacity") int minCapacity);

        @GET("api/bookings")
        Call<List<Map<String, Object>>> getBookings();

        @GET("api/bookings/my")
        Call<List<Map<String, Object>>> getMyBookings();

        @POST("api/bookings")
        Call<ServerResponse> createBooking(@Body Map<String, Object> bookingData);

        @PUT("api/bookings/{id}")
        Call<ServerResponse> updateBooking(@Path("id") int id, @Body Map<String, Object> bookingData);

        @DELETE("api/bookings/{id}")
        Call<ServerResponse> cancelBooking(@Path("id") int id);

        @POST("api/bookings/{id}/checkin")
        Call<ServerResponse> checkInBooking(@Path("id") int id);

        // ==================== ANNOUNCEMENTS ====================

        @GET("api/announcements")
        Call<List<Map<String, Object>>> getAnnouncements();

        @GET("api/announcements/latest")
        Call<List<Map<String, Object>>> getLatestAnnouncements(@Query("limit") int limit);

        // ==================== ANALYTICS (Admin/Security) ====================

        @GET("api/analytics/stats")
        Call<Map<String, Object>> getDashboardStats();

        @GET("api/analytics/incidents/trend")
        Call<Map<String, Object>> getIncidentTrend(@Query("days") int days);

        @GET("api/analytics/heatmap")
        Call<List<Map<String, Object>>> getHeatmapData();

        // ==================== CHAT ====================

        @GET("api/chat/conversations")
        Call<List<Map<String, Object>>> getConversations();

        @GET("api/chat/messages/{conversation_id}")
        Call<List<Map<String, Object>>> getChatMessages(@Path("conversation_id") int conversationId);

        @POST("api/chat/send")
        Call<ServerResponse> sendChatMessage(@Body Map<String, Object> message);

        // ==================== SETTINGS ====================

        @GET("api/settings/preferences")
        Call<Map<String, Object>> getUserPreferences();

        @PUT("api/settings/preferences")
        Call<ServerResponse> updateUserPreferences(@Body Map<String, Object> preferences);
}
