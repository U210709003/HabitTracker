package com.example.habittracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.BadgeViewHolder> {

    private final List<Badge> badges;
    private final Context context;

    public BadgesAdapter(Context context, List<Badge> badges) {
        this.context = context;
        this.badges = badges;
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Badge badge = badges.get(position);
        holder.badgeName.setText(badge.getName());
        holder.badgeDescription.setText(badge.getDescription());

        // Show related goals
        if (badge.getRelatedGoals() != null && !badge.getRelatedGoals().isEmpty()) {
            holder.badgeGoals.setText(String.join(", ", badge.getRelatedGoals()));
        } else {
            holder.badgeGoals.setText("No related goals");
        }

        // Assign an icon based on badge name
        switch (badge.getName()) {
            case "Achievement Hunter":
                holder.badgeIcon.setImageResource(R.drawable.baseline_shield_24);
                break;
            case "Progress Master":
                holder.badgeIcon.setImageResource(R.drawable.baseline_shield_moon_24);
                break;
            case "Explorer":
                holder.badgeIcon.setImageResource(R.drawable.baseline_diamond_24);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        TextView badgeName;
        TextView badgeDescription;
        TextView badgeGoals;
        ImageView badgeIcon;

        public BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeName = itemView.findViewById(R.id.badge_name);
            badgeDescription = itemView.findViewById(R.id.badge_description);
            badgeGoals = itemView.findViewById(R.id.badge_goals);
            badgeIcon = itemView.findViewById(R.id.badge_icon);
        }
    }
}
