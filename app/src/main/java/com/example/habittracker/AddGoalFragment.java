package com.example.habittracker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGoalFragment extends Fragment {

    private EditText editTextGoalTitle, editTextGoalDescription;
    private Button buttonSaveGoal;
    private DatabaseReference databaseReference;
    private Goal goal;

    public static AddGoalFragment newInstance(Goal goal) {
        AddGoalFragment fragment = new AddGoalFragment();
        Bundle args = new Bundle();
        args.putParcelable("goal", goal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_goal, container, false);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Goals");

        // Initialize UI components
        editTextGoalTitle = view.findViewById(R.id.edit_text_goal_title);
        editTextGoalDescription = view.findViewById(R.id.edit_text_goal_description);
        buttonSaveGoal = view.findViewById(R.id.button_save_goal);

        // Check for arguments and populate fields if editing a goal
        if (getArguments() != null) {
            goal = getArguments().getParcelable("goal");
            if (goal != null) {
                editTextGoalTitle.setText(goal.getTitle());
                editTextGoalDescription.setText(goal.getDescription());
            }
        }

        // Set click listener for save button
        buttonSaveGoal.setOnClickListener(v -> saveGoalToFirebase());

        return view;
    }

    private void saveGoalToFirebase() {
        String title = editTextGoalTitle.getText().toString().trim();
        String description = editTextGoalDescription.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (goal == null) {
            // Add new goal
            String id = databaseReference.push().getKey();
            if (id != null) {
                goal = new Goal(id, title, description, "Daily", false);
            } else {
                Toast.makeText(getContext(), "Failed to generate goal ID", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Update existing goal
            goal.setTitle(title);
            goal.setDescription(description);
        }

        databaseReference.child(goal.getId()).setValue(goal).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Goal saved successfully", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> getActivity().onBackPressed());
                }
            } else {
                Toast.makeText(getContext(), "Failed to save goal", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}