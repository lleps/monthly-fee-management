package com.lleps.mfm.model;

import com.lleps.mfm.view.ExercisePlanView;

import java.io.Serializable;
import java.time.LocalDate;

public class ExercisePlan implements Serializable {
    public static String[] defaultColumns = { "Ejercicio", "Series", "Repeticiones", "Pausa" };

    public static String[][] getEmptyExercises() {
        String[][] emptyExercises = new String[66][4];
        for (int i = 0; i < emptyExercises.length; i++) {
            for (int j = 0; j < emptyExercises[i].length; j++) {
                if (i == 0) {
                    emptyExercises[i][j] = defaultColumns[j];
                } else {
                    emptyExercises[i][j] = "";
                }
            }
        }
        return emptyExercises;
    }

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