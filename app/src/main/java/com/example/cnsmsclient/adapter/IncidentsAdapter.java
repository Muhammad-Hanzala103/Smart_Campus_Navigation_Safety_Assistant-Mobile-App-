package com.example.cnsmsclient.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cnsmsclient.databinding.ItemIncidentBinding;
import com.example.cnsmsclient.model.Incident;
import java.util.List;

public class IncidentsAdapter extends RecyclerView.Adapter<IncidentsAdapter.IncidentViewHolder> {

    private final List<Incident> incidents;

    public IncidentsAdapter(List<Incident> incidents) {
        this.incidents = incidents;
    }

    @NonNull
    @Override
    public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIncidentBinding binding = ItemIncidentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IncidentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentViewHolder holder, int position) {
        Incident incident = incidents.get(position);
        holder.binding.categoryText.setText(incident.category);
        holder.binding.descriptionText.setText(incident.description);
        holder.binding.statusChip.setText(incident.status);
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    static class IncidentViewHolder extends RecyclerView.ViewHolder {
        private final ItemIncidentBinding binding;

        public IncidentViewHolder(ItemIncidentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
