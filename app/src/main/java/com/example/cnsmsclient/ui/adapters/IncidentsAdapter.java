package com.example.cnsmsclient.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.util.DateTimeUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import java.util.List;

/**
 * Enhanced RecyclerView adapter for displaying incidents with status colors and
 * improved UI.
 */
public class IncidentsAdapter extends RecyclerView.Adapter<IncidentsAdapter.ViewHolder> {

    private final List<Incident> incidents;
    private final IncidentClickListener listener;
    private String baseUrl;

    public interface IncidentClickListener {
        void onIncidentClick(Incident incident, int position);
    }

    public IncidentsAdapter(List<Incident> incidents, IncidentClickListener listener) {
        this.incidents = incidents;
        this.listener = listener;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incident_enhanced, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incident incident = incidents.get(position);

        // Set description
        holder.description.setText(incident.description != null ? incident.description : "No description");

        // Set category with icon
        holder.category.setText(getCategoryText(incident.category));
        holder.categoryIcon.setImageResource(getCategoryIcon(incident.category));

        // Set status chip with color
        holder.statusChip.setText(incident.status != null ? incident.status.toUpperCase() : "OPEN");
        holder.statusChip.setChipBackgroundColorResource(getStatusColor(incident.status));

        // Set severity indicator
        if (incident.severity != null) {
            holder.severityIndicator.setVisibility(View.VISIBLE);
            holder.severityIndicator.setBackgroundResource(getSeverityColor(incident.severity));
        } else {
            holder.severityIndicator.setVisibility(View.GONE);
        }

        // Set time
        holder.timeText.setText(DateTimeUtils.getRelativeTime(null)); // Will show "Just now" if null

        // Load incident image
        if (incident.imageUrl != null && !incident.imageUrl.isEmpty() && baseUrl != null) {
            String fullUrl = baseUrl + incident.imageUrl;
            holder.incidentImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(fullUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_incident)
                    .into(holder.incidentImage);
        } else {
            holder.incidentImage.setVisibility(View.GONE);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncidentClick(incident, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    private String getCategoryText(String category) {
        if (category == null)
            return "Other";
        switch (category.toLowerCase()) {
            case "security":
                return "🛡️ Security";
            case "fire":
                return "🔥 Fire";
            case "medical":
                return "🏥 Medical";
            case "accident":
                return "⚠️ Accident";
            case "infrastructure":
                return "🏗️ Infrastructure";
            default:
                return "📋 " + category;
        }
    }

    private int getCategoryIcon(String category) {
        if (category == null)
            return R.drawable.ic_info;
        switch (category.toLowerCase()) {
            case "security":
                return R.drawable.ic_security;
            case "fire":
                return R.drawable.ic_fire;
            case "medical":
                return R.drawable.ic_medical;
            case "accident":
                return R.drawable.ic_warning;
            case "infrastructure":
                return R.drawable.ic_infrastructure;
            default:
                return R.drawable.ic_info;
        }
    }

    private int getStatusColor(String status) {
        if (status == null)
            return R.color.status_open;
        switch (status.toLowerCase()) {
            case "open":
                return R.color.status_open;
            case "in_progress":
            case "in progress":
            case "investigating":
                return R.color.status_in_progress;
            case "resolved":
            case "closed":
                return R.color.status_resolved;
            default:
                return R.color.status_pending;
        }
    }

    private int getSeverityColor(String severity) {
        if (severity == null)
            return R.color.severity_low;
        switch (severity.toLowerCase()) {
            case "high":
            case "critical":
                return R.color.severity_high;
            case "medium":
                return R.color.severity_medium;
            case "low":
            default:
                return R.color.severity_low;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        View severityIndicator;
        ImageView categoryIcon;
        TextView category;
        Chip statusChip;
        TextView description;
        TextView timeText;
        ImageView incidentImage;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.incidentCard);
            severityIndicator = itemView.findViewById(R.id.severityIndicator);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            category = itemView.findViewById(R.id.categoryText);
            statusChip = itemView.findViewById(R.id.statusChip);
            description = itemView.findViewById(R.id.descriptionText);
            timeText = itemView.findViewById(R.id.timeText);
            incidentImage = itemView.findViewById(R.id.incidentImage);
        }
    }
}
