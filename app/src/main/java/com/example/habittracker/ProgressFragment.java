package com.example.habittracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    TableLayout progressTable;
    protected DatabaseReference goalsReference;
    protected DatabaseReference progressReference;
    private String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        HorizontalScrollView scrollView = view.findViewById(R.id.scroll_view);
        progressTable = view.findViewById(R.id.progress_table);

        goalsReference = FirebaseDatabase.getInstance().getReference("Goals");
        progressReference = FirebaseDatabase.getInstance().getReference("Progress");

        fetchGoals();

        return view;
    }

    protected void fetchGoals() {
        goalsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Goal> goals = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Goal goal = dataSnapshot.getValue(Goal.class);
                    if (goal != null) {
                        goals.add(goal);
                    }
                }
                populateTable(goals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch goals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateTable(List<Goal> goals) {
        for (Goal goal : goals) {
            TableRow row = new TableRow(getContext());

            // Goal adı
            TextView goalName = new TextView(getContext());
            goalName.setText(goal.getTitle());
            goalName.setPadding(8, 8, 8, 8);
            row.addView(goalName);

            // Haftanın günleri için hücreler (CheckBox)
            for (int i = 0; i < 7; i++) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setPadding(8, 8, 8, 8);
                String day = days[i];

                // Firebase'den mevcut progress verisini getir ve CheckBox'ı güncelle
                fetchProgressForGoalAndDay(goal.getId(), day, checkBox);

                // CheckBox tıklama dinleyicisi
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    saveProgressToFirebase(goal.getId(), day, isChecked);
                });

                row.addView(checkBox);
            }

            progressTable.addView(row);
        }
    }

    private void fetchProgressForGoalAndDay(String goalId, String day, CheckBox checkBox) {
        progressReference.child(goalId).child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChecked = snapshot.getValue(Boolean.class);
                if (isChecked != null) {
                    checkBox.setChecked(isChecked);
                } else {
                    checkBox.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProgressToFirebase(String goalId, String day, boolean isChecked) {
        progressReference.child(goalId).child(day).setValue(isChecked).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Progress updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update progress", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
