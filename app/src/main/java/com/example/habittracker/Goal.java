package com.example.habittracker;

import android.os.Parcel;
import android.os.Parcelable;

public class Goal implements Parcelable {
    private String id; // Unique ID for the goal
    private String title; // Goal name/title
    private String description; // Goal description
    private String frequency; // Frequency of the goal (e.g., Daily, Weekly)
    private boolean isCompleted; // Whether the goal is completed or not

    // Default constructor (required for Firebase)
    public Goal() {
    }

    public Goal(String id, String title) {
        this.id = id;
        this.title = title;
    }

    // Constructor with parameters
    public Goal(String id, String title, String description, String frequency, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.isCompleted = isCompleted;
    }

    // Parcelable implementation
    protected Goal(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        frequency = in.readString();
        isCompleted = in.readByte() != 0; // readByte returns 0 if false, 1 if true
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(frequency);
        dest.writeByte((byte) (isCompleted ? 1 : 0)); // writeByte writes 0 for false, 1 for true
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return title; // Goal'ın Spinner'da sadece adını gösterecek
    }

}
