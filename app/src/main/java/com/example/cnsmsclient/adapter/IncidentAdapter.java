package com.example.cnsmsclient.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cnsmsclient.databinding.ItemIncidentBinding;
import com.example.cnsmsclient.model.Incident;

public class IncidentAdapter extends ListAdapter<Incident, IncidentAdapter.IncidentViewHolder> {

    public IncidentAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Incident> DIFF_CALLBACK = new DiffUtil.ItemCallback<Incident>() {
        @Override
        public boolean areItemsTheSame(@NonNull Incident oldItem, @NonNull Incident newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Incident oldItem, @NonNull Incident newItem) {
            return oldItem.serverId.equals(newItem.serverId) &&
                    oldItem.status.equals(newItem.status);
        }
    };

    @NonNull
    @Override
    public IncidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIncidentBinding binding = ItemIncidentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IncidentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentViewHolder holder, int position) {
        Incident currentIncident = getItem(position);
        holder.binding.categoryText.setText(currentIncident.category);
        holder.binding.descriptionText.setText(currentIncident.description);
        holder.binding.dateText.setText(currentIncident.createdAt);
        holder.binding.statusChip.setText(currentIncident.status);
    }

    static class IncidentViewHolder extends RecyclerView.ViewHolder {
        private final ItemIncidentBinding binding;

        public IncidentViewHolder(ItemIncidentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
