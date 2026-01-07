package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityNotificationsBinding;
import com.example.cnsmsclient.model.NotificationItem;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.ui.adapters.NotificationsAdapter;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to display all notifications.
 * Features: List view, mark as read, swipe to delete.
 */
public class NotificationsActivity extends AppCompatActivity implements NotificationsAdapter.NotificationClickListener {

    private ActivityNotificationsBinding binding;
    private ApiService apiService;
    private NotificationsAdapter adapter;
    private List<NotificationItem> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService(this);

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        loadNotifications();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.markAllReadButton.setOnClickListener(v -> markAllAsRead());
    }

    private void setupRecyclerView() {
        adapter = new NotificationsAdapter(notifications, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.md_theme_light_primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadNotifications);
    }

    private void loadNotifications() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);

        apiService.getNotifications().enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    notifications.clear();
                    notifications.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    binding.emptyView.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.recyclerView.setVisibility(notifications.isEmpty() ? View.GONE : View.VISIBLE);

                    updateUnreadCount();
                } else {
                    showError("Failed to load notifications");
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void updateUnreadCount() {
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        if (unreadCount > 0) {
            binding.markAllReadButton.setVisibility(View.VISIBLE);
            binding.unreadBadge.setVisibility(View.VISIBLE);
            binding.unreadBadge.setText(String.valueOf(unreadCount));
        } else {
            binding.markAllReadButton.setVisibility(View.GONE);
            binding.unreadBadge.setVisibility(View.GONE);
        }
    }

    private void markAllAsRead() {
        apiService.markAllNotificationsRead().enqueue(new Callback<com.example.cnsmsclient.model.ServerResponse>() {
            @Override
            public void onResponse(Call<com.example.cnsmsclient.model.ServerResponse> call,
                    Response<com.example.cnsmsclient.model.ServerResponse> response) {
                if (response.isSuccessful()) {
                    for (NotificationItem n : notifications) {
                        n.setRead(true);
                    }
                    adapter.notifyDataSetChanged();
                    updateUnreadCount();
                    Snackbar.make(binding.getRoot(), "All notifications marked as read", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.success_green))
                            .show();
                }
            }

            @Override
            public void onFailure(Call<com.example.cnsmsclient.model.ServerResponse> call, Throwable t) {
                showError("Failed to mark as read");
            }
        });
    }

    @Override
    public void onNotificationClick(NotificationItem notification, int position) {
        if (!notification.isRead()) {
            markAsRead(notification, position);
        }
        // Handle notification action based on type
        handleNotificationAction(notification);
    }

    @Override
    public void onDeleteClick(NotificationItem notification, int position) {
        deleteNotification(notification, position);
    }

    private void markAsRead(NotificationItem notification, int position) {
        apiService.markNotificationRead(notification.getId())
                .enqueue(new Callback<com.example.cnsmsclient.model.ServerResponse>() {
                    @Override
                    public void onResponse(Call<com.example.cnsmsclient.model.ServerResponse> call,
                            Response<com.example.cnsmsclient.model.ServerResponse> response) {
                        if (response.isSuccessful()) {
                            notification.setRead(true);
                            adapter.notifyItemChanged(position);
                            updateUnreadCount();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.cnsmsclient.model.ServerResponse> call, Throwable t) {
                        // Silent fail
                    }
                });
    }

    private void deleteNotification(NotificationItem notification, int position) {
        apiService.deleteNotification(notification.getId())
                .enqueue(new Callback<com.example.cnsmsclient.model.ServerResponse>() {
                    @Override
                    public void onResponse(Call<com.example.cnsmsclient.model.ServerResponse> call,
                            Response<com.example.cnsmsclient.model.ServerResponse> response) {
                        if (response.isSuccessful()) {
                            notifications.remove(position);
                            adapter.notifyItemRemoved(position);
                            updateUnreadCount();
                            Snackbar.make(binding.getRoot(), "Notification deleted", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.example.cnsmsclient.model.ServerResponse> call, Throwable t) {
                        showError("Failed to delete");
                    }
                });
    }

    private void handleNotificationAction(NotificationItem notification) {
        // Navigate based on notification type
        String type = notification.getType();
        if (type == null)
            return;

        switch (type) {
            case "incident_update":
                // Navigate to incident details
                break;
            case "emergency":
                // Navigate to SOS
                break;
            case "booking_reminder":
                // Navigate to bookings
                break;
            default:
                // Just show notification is read
                break;
        }
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.md_theme_light_error))
                .show();
    }
}
