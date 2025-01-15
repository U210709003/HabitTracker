package com.example.habittracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {

    private final List<Goal> goalList;

    public GoalsAdapter(List<Goal> goalList) {
        this.goalList = goalList;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal_simple, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goalList.get(position);
        holder.textViewTitle.setText(goal.getTitle());
        holder.textViewFrequency.setText(goal.getFrequency());

        // Delete button listener
        holder.buttonDelete.setOnClickListener(v -> {
            DatabaseReference goalRef = FirebaseDatabase.getInstance().getReference("Goals").child(goal.getId());

            // Remove value in Firebase
            goalRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (position >= 0 && position < goalList.size()) { // Ensure position is valid
                        goalList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, goalList.size()); // Optional for smooth animation
                    }
                    Toast.makeText(holder.itemView.getContext(), "Goal deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to delete goal", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        });

        // Edit button listener
        holder.buttonEdit.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();
            if (activity != null) {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                AddGoalFragment editFragment = AddGoalFragment.newInstance(goal);
                transaction.replace(R.id.fragment_container, editFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(holder.itemView.getContext(), "Unable to edit goal. Activity not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewFrequency;
        Button buttonDelete;
        Button buttonEdit;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_goal_title);
            textViewFrequency = itemView.findViewById(R.id.text_view_goal_frequency);
            buttonDelete = itemView.findViewById(R.id.button_delete_goal);
            buttonEdit = itemView.findViewById(R.id.button_edit_goal);
        }
    }
}
