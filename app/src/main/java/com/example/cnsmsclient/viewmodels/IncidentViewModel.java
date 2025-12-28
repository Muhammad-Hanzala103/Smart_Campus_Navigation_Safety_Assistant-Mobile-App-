package com.example.cnsmsclient.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cnsmsclient.data.IncidentRepository;
import com.example.cnsmsclient.model.Incident;

import java.util.List;

public class IncidentViewModel extends AndroidViewModel {

    private final IncidentRepository incidentRepository;
    private final LiveData<List<Incident>> allIncidents;

    public IncidentViewModel(@NonNull Application application) {
        super(application);
        incidentRepository = new IncidentRepository(application);
        allIncidents = incidentRepository.getAllIncidents();
    }

    public LiveData<List<Incident>> getAllIncidents() {
        return allIncidents;
    }

    public void refreshIncidents() {
        incidentRepository.refreshIncidents();
    }
}
