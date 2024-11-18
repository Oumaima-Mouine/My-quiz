package com.example.myinteljquiz.model;


public class QuizCard {
    private String title;
    private String description;
    private String duration;

    public QuizCard(String title, String description, String duration) {
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }
}
