package com.example.habittracker;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends Fragment {

    private RecyclerView recyclerView;
    private GoalsAdapter goalsAdapter;
    private List<Goal> goalList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_goals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        goalList = new ArrayList<>();
        goalsAdapter = new GoalsAdapter(goalList);
        recyclerView.setAdapter(goalsAdapter);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Goals");

        // Fetch goals from Firebase
        fetchGoals();

        // Floating action button to navigate to AddGoalFragment
        FloatingActionButton fabAddGoal = view.findViewById(R.id.fab_add_goal);
        fabAddGoal.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new AddGoalFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void fetchGoals() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Goal> newGoalList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Goal goal = dataSnapshot.getValue(Goal.class);
                    if (goal != null) {
                        newGoalList.add(goal);
                    }
                }
                goalList.clear();
                goalList.addAll(newGoalList);
                goalsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }
}
