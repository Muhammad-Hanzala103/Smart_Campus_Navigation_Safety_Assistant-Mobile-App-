package com.example.cnsmsclient.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.network.NetworkModule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentRepository {

    private final IncidentDao incidentDao;
    private final ApiService apiService;
    private final LiveData<List<Incident>> allIncidents;

    public IncidentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.incidentDao = db.incidentDao();
        this.apiService = NetworkModule.getApiService(application);
        this.allIncidents = incidentDao.getAllIncidents();
    }

    public LiveData<List<Incident>> getAllIncidents() {
        return allIncidents;
    }

    public void refreshIncidents() {
        apiService.getIncidents().enqueue(new Callback<List<Incident>>() {
            @Override
            public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        incidentDao.deleteAll();
                        incidentDao.insertAll(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Incident>> call, Throwable t) {
                // Handle network error, maybe log it or show a non-intrusive error message
            }
        });
    }
}
