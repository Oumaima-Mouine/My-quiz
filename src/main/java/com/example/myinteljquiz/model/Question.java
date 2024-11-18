package com.example.myinteljquiz.model;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private String text;
    private int points;
    private List<Option> options;
    public Question(int id, String text, int points) {
        this.id = id;
        this.text = text;
        this.points = points;
        this.options = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
    public void addOption(Option option) {
        this.options.add(option);
    }
}
