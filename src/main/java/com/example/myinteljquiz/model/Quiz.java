package com.example.myinteljquiz.model;

public class Quiz {
    private int id;
    private String name;
    private String description;
    private String time;

    // Constructor
    public Quiz(int id, String name, String description, String time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
    }

    public Quiz() {

    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
