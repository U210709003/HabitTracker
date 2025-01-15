
package com.example.habittracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadgesFragment extends Fragment {

    private RecyclerView badgesRecyclerView;
    private BadgesAdapter badgesAdapter;
    private DatabaseReference goalsReference;
    private DatabaseReference progressReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_badges, container, false);

        badgesRecyclerView = view.findViewById(R.id.badges_recycler_view);
        badgesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        goalsReference = FirebaseDatabase.getInstance().getReference("Goals");
        progressReference = FirebaseDatabase.getInstance().getReference("Progress");

        fetchBadgesData();

        return view;
    }

    private void fetchBadgesData() {
        goalsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> goalNames = new HashMap<>();
                for (DataSnapshot goalSnapshot : snapshot.getChildren()) {
                    String goalId = goalSnapshot.getKey();
                    String goalTitle = goalSnapshot.child("title").getValue(String.class);
                    if (goalId != null && goalTitle != null) {
                        goalNames.put(goalId, goalTitle);
                    }
                }
                calculateBadges(goalNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch goals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateBadges(Map<String, String> goalNames) {
        progressReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> goalProgress = new HashMap<>();

                for (DataSnapshot goalSnapshot : snapshot.getChildren()) {
                    String goalId = goalSnapshot.getKey();
                    int completedDays = 0;

                    for (DataSnapshot daySnapshot : goalSnapshot.getChildren()) {
                        Boolean isChecked = daySnapshot.getValue(Boolean.class);
                        if (isChecked != null && isChecked) {
                            completedDays++;
                        }
                    }
                    goalProgress.put(goalId, completedDays);
                }

                List<Badge> badges = assignBadges(goalProgress, goalNames);
                badgesAdapter = new BadgesAdapter(requireContext(), badges);
                badgesRecyclerView.setAdapter(badgesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Badge> assignBadges(Map<String, Integer> goalProgress, Map<String, String> goalNames) {
        List<Badge> badges = new ArrayList<>();

        List<String> achievementGoals = new ArrayList<>();
        List<String> progressGoals = new ArrayList<>();
        List<String> explorerGoals = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : goalProgress.entrySet()) {
            String goalId = entry.getKey();
            int completedDays = entry.getValue();

            String goalName = goalNames.getOrDefault(goalId, "Unknown Goal");

            if (completedDays >= 7) {
                achievementGoals.add(goalName);
            } else if (completedDays >= 5) {
                progressGoals.add(goalName);
            } else if (completedDays >= 2) {
                explorerGoals.add(goalName);
            }
        }

        if (!achievementGoals.isEmpty()) {
            badges.add(new Badge("Achievement Hunter", "Completed 7 days!", achievementGoals));
        }
        if (!progressGoals.isEmpty()) {
            badges.add(new Badge("Progress Master", "Completed 5 days!", progressGoals));
        }
        if (!explorerGoals.isEmpty()) {
            badges.add(new Badge("Explorer", "Completed 2 days!", explorerGoals));
        }

        return badges;
    }
}

