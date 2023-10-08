package com.saidev.MediVeda.modelClass;

public class Hospital {
    private String name;
    private double rating;

    public Hospital(String name, double rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }
}
