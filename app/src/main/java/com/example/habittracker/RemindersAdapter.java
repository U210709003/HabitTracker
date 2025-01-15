// RemindersAdapter
package com.example.habittracker;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.List;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    private final List<Reminder> reminderList;
    private final DatabaseReference remindersReference;

    public RemindersAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
        this.remindersReference = FirebaseDatabase.getInstance().getReference("Reminders");
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminders, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);

        // Fetch Goal name from Firebase
        DatabaseReference goalRef = FirebaseDatabase.getInstance().getReference("Goals").child(reminder.getGoalId());
        goalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Goal goal = snapshot.getValue(Goal.class);
                if (goal != null) {
                    holder.textViewGoalName.setText(goal.getTitle());
                } else {
                    holder.textViewGoalName.setText("Unknown Goal");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.textViewGoalName.setText("Error Loading Goal");
            }
        });

        holder.textViewDay.setText(reminder.getDayOfWeek());
        holder.textViewTime.setText(reminder.getTime());

        // Edit button listener
        holder.buttonEdit.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_edit_reminder, null);
            Spinner daySpinner = dialogView.findViewById(R.id.spinner_edit_day);
            Button pickTimeButton = dialogView.findViewById(R.id.button_edit_pick_time);
            Button saveButton = dialogView.findViewById(R.id.button_save_edit_reminder);

            // Populate day spinner
            ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(v.getContext(), R.array.days_of_week, android.R.layout.simple_spinner_item);
            dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            daySpinner.setAdapter(dayAdapter);

            // Pre-select current day
            int dayPosition = dayAdapter.getPosition(reminder.getDayOfWeek());
            daySpinner.setSelection(dayPosition);

            // Pre-select current time
            final String[] selectedTime = {reminder.getTime()};
            pickTimeButton.setText(selectedTime[0]);

            pickTimeButton.setOnClickListener(view -> {
                Calendar calendar = Calendar.getInstance();
                String[] timeParts = selectedTime[0].split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), (timeView, hourOfDay, minuteOfHour) -> {
                    selectedTime[0] = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    pickTimeButton.setText(selectedTime[0]);
                }, hour, minute, true);
                timePickerDialog.show();
            });

            // Save edited reminder
            saveButton.setOnClickListener(view -> {
                String newDay = daySpinner.getSelectedItem().toString();
                reminder.setDayOfWeek(newDay);
                reminder.setTime(selectedTime[0]);

                // Update reminder in Firebase
                remindersReference.child(reminder.getId()).setValue(reminder)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(view.getContext(), "Reminder updated successfully", Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position); // Update the UI
                            } else {
                                Toast.makeText(view.getContext(), "Failed to update reminder", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            // Show edit dialog
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
            builder.setView(dialogView);
            builder.setCancelable(true);
            builder.show();
        });

        // Delete button listener
        holder.buttonDelete.setOnClickListener(v -> {
            remindersReference.child(reminder.getId()).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int currentPosition = holder.getAdapterPosition();
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                reminderList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                Toast.makeText(holder.itemView.getContext(), "Reminder deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Failed to delete reminder", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGoalName, textViewDay, textViewTime;
        Button buttonEdit, buttonDelete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGoalName = itemView.findViewById(R.id.text_view_goal_name);
            textViewDay = itemView.findViewById(R.id.text_view_day);
            textViewTime = itemView.findViewById(R.id.text_view_time);
            buttonEdit = itemView.findViewById(R.id.button_edit_reminder);
            buttonDelete = itemView.findViewById(R.id.button_delete_reminder);
        }
    }
}
