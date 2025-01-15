// RemindersFragment
package com.example.habittracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemindersFragment extends Fragment {

    private RecyclerView recyclerView;
    private RemindersAdapter remindersAdapter;
    private List<Reminder> reminderList;
    private DatabaseReference remindersReference;
    private DatabaseReference goalsReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderList = new ArrayList<>();
        remindersAdapter = new RemindersAdapter(reminderList);
        recyclerView.setAdapter(remindersAdapter);

        // Initialize Firebase references
        remindersReference = FirebaseDatabase.getInstance().getReference("Reminders");
        goalsReference = FirebaseDatabase.getInstance().getReference("Goals");

        // Fetch reminders from Firebase
        fetchReminders();

        // Add Reminder Button
        Button addReminderButton = view.findViewById(R.id.button_add_reminder);
        addReminderButton.setOnClickListener(v -> showGoalSelectionDialog());

        return view;
    }

    private void fetchReminders() {
        remindersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Reminder reminder = dataSnapshot.getValue(Reminder.class);
                    if (reminder != null) {
                        reminderList.add(reminder);
                    }
                }
                remindersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGoalSelectionDialog() {
        // Use the correct layout for goal selection dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_reminder, null);
        Spinner goalSpinner = dialogView.findViewById(R.id.spinner_goals);
        Spinner daySpinner = dialogView.findViewById(R.id.spinner_days);
        Button pickTimeButton = dialogView.findViewById(R.id.button_pick_time);
        Button saveButton = dialogView.findViewById(R.id.button_save_reminder);

        // Populate day spinner
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.days_of_week, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        // Fetch Goals
        List<Goal> goalList = new ArrayList<>();
        goalsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Goal goal = dataSnapshot.getValue(Goal.class);
                    if (goal != null) {
                        goalList.add(goal);
                    }
                }
                ArrayAdapter<Goal> goalAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, goalList);
                goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                goalSpinner.setAdapter(goalAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load goals", Toast.LENGTH_SHORT).show();
            }
        });

        // Time Picker Dialog
        final String[] selectedTime = new String[1];
        pickTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minuteOfHour) -> {
                selectedTime[0] = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                pickTimeButton.setText(selectedTime[0]);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        // Save Reminder
        saveButton.setOnClickListener(v -> {
            Goal selectedGoal = (Goal) goalSpinner.getSelectedItem();
            String selectedDay = daySpinner.getSelectedItem().toString();
            if (selectedGoal != null && selectedTime[0] != null) {
                String reminderId = remindersReference.push().getKey();
                if (reminderId != null) {
                    Reminder reminder = new Reminder(reminderId, selectedGoal.getId(), selectedDay, selectedTime[0]);
                    remindersReference.child(reminderId).setValue(reminder).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Reminder added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add reminder", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Please select a goal, day, and time", Toast.LENGTH_SHORT).show();
            }
        });

        // Show dialog for goal selection
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.show();
    }


    private void setReminderAlarm(Context context, Reminder reminder, Goal goal) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("goal_title", goal.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Gün ve saati ayarlayın
        String[] timeParts = reminder.getTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        String dayOfWeek = reminder.getDayOfWeek();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Haftanın doğru günü için ayarlama
        int dayOfWeekValue = getDayOfWeekValue(dayOfWeek);
        while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeekValue) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Geçmiş bir zaman seçildiyse bir hafta ekleyin
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private int getDayOfWeekValue(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            case "Sunday": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }


    private void saveReminder(Goal selectedGoal, String time, String day) {
        String reminderId = remindersReference.push().getKey();
        if (reminderId != null) {
            Reminder reminder = new Reminder(reminderId, selectedGoal.getId(), day, time);
            remindersReference.child(reminderId).setValue(reminder).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    setReminderAlarm(getContext(), reminder, selectedGoal);
                    Toast.makeText(getContext(), "Reminder added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add reminder", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
