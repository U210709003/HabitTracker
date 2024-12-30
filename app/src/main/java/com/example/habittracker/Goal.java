package com.example.habittracker;

public class Goal {
    private String id; // Unique ID for the goal
    private String title; // Goal name/title
    private String description; // Goal description
    private String frequency; // Frequency of the goal (e.g., Daily, Weekly)
    private boolean isCompleted; // Whether the goal is completed or not

    // Default constructor (required for Firebase)
    public Goal() {
    }

    // Constructor with parameters
    public Goal(String id, String title, String description, String frequency, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.isCompleted = isCompleted;
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
}
