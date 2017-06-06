package com.lleps.mfm.model;

import java.io.Serializable;
import java.time.LocalDate;

public class ExercisePlan implements Serializable {
    private final String name;
    private final LocalDate date;
    private String[][] exercises;

    public ExercisePlan(String name, LocalDate date, String[][] exercises) {
        this.name = name;
        this.date = date;
        this.exercises = exercises;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public String[][] getExercises() {
        return exercises;
    }

    public void setExercises(String[][] exercises) {
        this.exercises = exercises;
    }
}