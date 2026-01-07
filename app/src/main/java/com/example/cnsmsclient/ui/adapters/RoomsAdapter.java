package com.example.cnsmsclient.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cnsmsclient.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying rooms in a RecyclerView.
 */
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    private final List<Map<String, Object>> rooms;
    private List<Integer> availableRoomIds = new ArrayList<>();
    private final RoomClickListener listener;

    public interface RoomClickListener {
        void onRoomClick(Map<String, Object> room);
    }

    public RoomsAdapter(List<Map<String, Object>> rooms, RoomClickListener listener) {
        this.rooms = rooms;
        this.listener = listener;
    }

    public void setAvailableRooms(List<Map<String, Object>> availableRooms) {
        availableRoomIds.clear();
        for (Map<String, Object> room : availableRooms) {
            Object id = room.get("id");
            if (id instanceof Double) {
                availableRoomIds.add(((Double) id).intValue());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> room = rooms.get(position);

        holder.roomName.setText((String) room.get("name"));

        int capacity = room.get("capacity") != null ? ((Double) room.get("capacity")).intValue() : 0;
        holder.capacity.setText("Capacity: " + capacity);

        String building = room.get("building") != null ? (String) room.get("building") : "";
        String floor = room.get("floor") != null ? (String) room.get("floor") : "";
        holder.location.setText(building + (floor.isEmpty() ? "" : ", " + floor));

        // Check availability status
        int roomId = room.get("id") != null ? ((Double) room.get("id")).intValue() : -1;
        if (!availableRoomIds.isEmpty()) {
            if (availableRoomIds.contains(roomId)) {
                holder.availabilityChip.setText("Available");
                holder.availabilityChip.setChipBackgroundColorResource(R.color.success_green);
                holder.card.setEnabled(true);
                holder.card.setAlpha(1.0f);
            } else {
                holder.availabilityChip.setText("Unavailable");
                holder.availabilityChip.setChipBackgroundColorResource(R.color.md_theme_light_error);
                holder.card.setEnabled(false);
                holder.card.setAlpha(0.5f);
            }
            holder.availabilityChip.setVisibility(View.VISIBLE);
        } else {
            holder.availabilityChip.setVisibility(View.GONE);
            holder.card.setEnabled(true);
            holder.card.setAlpha(1.0f);
        }

        // Equipment icons
        Boolean hasProjector = (Boolean) room.get("has_projector");
        Boolean hasWhiteboard = (Boolean) room.get("has_whiteboard");
        Boolean hasAC = (Boolean) room.get("has_ac");

        holder.projectorIcon.setVisibility(hasProjector != null && hasProjector ? View.VISIBLE : View.GONE);
        holder.whiteboardIcon.setVisibility(hasWhiteboard != null && hasWhiteboard ? View.VISIBLE : View.GONE);
        holder.acIcon.setVisibility(hasAC != null && hasAC ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.card.isEnabled()) {
                listener.onRoomClick(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView roomName;
        TextView capacity;
        TextView location;
        Chip availabilityChip;
        ImageView projectorIcon;
        ImageView whiteboardIcon;
        ImageView acIcon;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.roomCard);
            roomName = itemView.findViewById(R.id.roomName);
            capacity = itemView.findViewById(R.id.roomCapacity);
            location = itemView.findViewById(R.id.roomLocation);
            availabilityChip = itemView.findViewById(R.id.availabilityChip);
            projectorIcon = itemView.findViewById(R.id.projectorIcon);
            whiteboardIcon = itemView.findViewById(R.id.whiteboardIcon);
            acIcon = itemView.findViewById(R.id.acIcon);
        }
    }
}
