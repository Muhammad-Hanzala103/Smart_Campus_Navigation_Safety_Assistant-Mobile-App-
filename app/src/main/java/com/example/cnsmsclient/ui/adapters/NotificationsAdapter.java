package com.example.cnsmsclient.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.model.NotificationItem;
import com.example.cnsmsclient.util.DateTimeUtils;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

/**
 * Adapter for displaying notifications in a RecyclerView.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final List<NotificationItem> notifications;
    private final NotificationClickListener listener;

    public interface NotificationClickListener {
        void onNotificationClick(NotificationItem notification, int position);

        void onDeleteClick(NotificationItem notification, int position);
    }

    public NotificationsAdapter(List<NotificationItem> notifications, NotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);

        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.time.setText(DateTimeUtils.getRelativeTime(notification.getCreatedAt()));

        // Set icon based on type
        holder.icon.setImageResource(notification.getIconResource());

        // Style based on read status
        if (notification.isRead()) {
            holder.card.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.card_background));
            holder.unreadIndicator.setVisibility(View.GONE);
        } else {
            holder.card.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.md_theme_light_primaryContainer));
            holder.unreadIndicator.setVisibility(View.VISIBLE);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification, position);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(notification, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView icon;
        TextView title;
        TextView message;
        TextView time;
        View unreadIndicator;
        ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.notificationCard);
            icon = itemView.findViewById(R.id.notificationIcon);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            time = itemView.findViewById(R.id.notificationTime);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
