package com.example.cnsmsclient.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cnsmsclient.model.Incident;

import java.util.List;

@Dao
public interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Incident> incidents);

    // Ordering by the auto-generated primary key in descending order to get the newest first.
    // This is more robust than relying on a `createdAt` column.
    @Query("SELECT * FROM incidents ORDER BY id DESC")
    LiveData<List<Incident>> getAllIncidents();

    @Query("DELETE FROM incidents")
    void deleteAll();
}
