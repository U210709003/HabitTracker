package com.example.habittracker;


import java.io.Serializable;

public class Reminder implements Serializable {
    private String id;
    private String goalId;
    private String dayOfWeek;
    private String time;

    public Reminder() {
    }

    public Reminder(String id, String goalId, String dayOfWeek, String time) {
        this.id = id;
        this.goalId = goalId;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
