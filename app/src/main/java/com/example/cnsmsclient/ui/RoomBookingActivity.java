package com.example.cnsmsclient.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityRoomBookingBinding;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.ui.adapters.RoomsAdapter;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Room Booking Activity for reserving campus rooms.
 * Features: Browse rooms, check availability, create bookings.
 */
public class RoomBookingActivity extends AppCompatActivity implements RoomsAdapter.RoomClickListener {

    private ActivityRoomBookingBinding binding;
    private ApiService apiService;
    private RoomsAdapter adapter;
    private List<Map<String, Object>> rooms = new ArrayList<>();

    private Calendar selectedDate = Calendar.getInstance();
    private String startTime = "";
    private String endTime = "";
    private int selectedRoomId = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService(this);

        setupToolbar();
        setupRecyclerView();
        setupDateTimePickers();
        setupPurposeDropdown();
        loadRooms();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Room Booking");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new RoomsAdapter(rooms, this);
        binding.roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.roomsRecyclerView.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        // Set default to today
        binding.dateInput.setText(displayDateFormat.format(selectedDate.getTime()));

        binding.dateInput.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        binding.dateInput.setText(displayDateFormat.format(selectedDate.getTime()));
                        checkAvailability();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });

        binding.startTimeInput.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);
                        startTime = timeFormat.format(time.getTime());
                        binding.startTimeInput
                                .setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                        checkAvailability();
                    }, 9, 0, true);
            dialog.show();
        });

        binding.endTimeInput.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);
                        endTime = timeFormat.format(time.getTime());
                        binding.endTimeInput
                                .setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                        checkAvailability();
                    }, 10, 0, true);
            dialog.show();
        });
    }

    private void setupPurposeDropdown() {
        String[] purposes = { "Study Session", "Meeting", "Presentation", "Workshop", "Group Project", "Other" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, purposes);
        binding.purposeDropdown.setAdapter(adapter);
    }

    private void loadRooms() {
        showLoading(true);
        apiService.getRooms().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    rooms.clear();
                    rooms.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    binding.roomsRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyView.setVisibility(rooms.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    showError("Failed to load rooms");
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                showLoading(false);
                showError("Network error");
            }
        });
    }

    private void checkAvailability() {
        if (startTime.isEmpty() || endTime.isEmpty())
            return;

        String date = dateFormat.format(selectedDate.getTime());

        binding.checkingAvailability.setVisibility(View.VISIBLE);
        apiService.getAvailableRooms(date, startTime, endTime, 1).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                binding.checkingAvailability.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // Update adapter to show available rooms
                    adapter.setAvailableRooms(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                binding.checkingAvailability.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRoomClick(Map<String, Object> room) {
        selectedRoomId = ((Double) room.get("id")).intValue();
        binding.selectedRoomCard.setVisibility(View.VISIBLE);
        binding.selectedRoomName.setText((String) room.get("name"));
        binding.selectedRoomCapacity.setText("Capacity: " + ((Double) room.get("capacity")).intValue());

        binding.bookButton.setOnClickListener(v -> createBooking());
    }

    private void createBooking() {
        String purpose = binding.purposeDropdown.getText().toString();
        if (purpose.isEmpty()) {
            binding.purposeLayout.setError("Select a purpose");
            return;
        }
        binding.purposeLayout.setError(null);

        if (startTime.isEmpty() || endTime.isEmpty()) {
            showError("Please select start and end time");
            return;
        }

        if (selectedRoomId == -1) {
            showError("Please select a room");
            return;
        }

        showLoading(true);

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("room_id", selectedRoomId);
        bookingData.put("date", dateFormat.format(selectedDate.getTime()));
        bookingData.put("start_time", startTime);
        bookingData.put("end_time", endTime);
        bookingData.put("purpose", purpose);

        apiService.createBooking(bookingData).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Snackbar.make(binding.getRoot(), "Booking created successfully!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getColor(R.color.success_green))
                            .show();
                    binding.getRoot().postDelayed(() -> finish(), 1500);
                } else {
                    showError("Failed to create booking. Room may not be available.");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.bookButton.setEnabled(!show);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.md_theme_light_error))
                .show();
    }
}
