package com.example.budget.Entity;

public class Category {
    private int id;
    private String name;

    // Constructors, getters, and setters
    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
