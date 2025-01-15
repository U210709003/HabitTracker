package com.example.habittracker;

import java.util.List;

public class Badge {
    private String name;
    private String description;
    private List<String> relatedGoals;

    // Varsayılan constructor (Firebase için gerekli)
    public Badge() {
    }

    // Parametreli constructor
    public Badge(String name, String description, List<String> relatedGoals) {
        this.name = name;
        this.description = description;
        this.relatedGoals = relatedGoals;
    }

    // Getter ve Setter metotları
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRelatedGoals() {
        return relatedGoals;
    }

    public void setRelatedGoals(List<String> relatedGoals) {
        this.relatedGoals = relatedGoals;
    }
}
