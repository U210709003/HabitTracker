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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_goal, container, false);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Goals");

        // Initialize UI components
        editTextGoalTitle = view.findViewById(R.id.edit_text_goal_title);
        editTextGoalDescription = view.findViewById(R.id.edit_text_goal_description);
        buttonSaveGoal = view.findViewById(R.id.button_save_goal);

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

        // Generate unique ID for the goal
        String id = databaseReference.push().getKey();

        // Create Goal object
        String frequency = "Daily"; // veya kullanıcıdan alınan bir değer
        Goal goal = new Goal(id, title, description, frequency, false);


        // Save to Firebase
        if (id != null) {
            databaseReference.child(id).setValue(goal).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Goal saved successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    editTextGoalTitle.setText("");
                    editTextGoalDescription.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to save goal", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
