package com.example.habittracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GoalSpinnerAdapter extends RecyclerView.Adapter<GoalSpinnerAdapter.GoalViewHolder> {

    private final List<Goal> goals;
    private final OnGoalClickListener listener;

    public GoalSpinnerAdapter(List<Goal> goals, OnGoalClickListener listener) {
        this.goals = goals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.textView.setText(goal.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onGoalClick(goal));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
