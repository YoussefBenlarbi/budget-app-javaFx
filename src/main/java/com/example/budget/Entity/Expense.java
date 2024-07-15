package com.example.budget.Entity;

public class Expense {
    private int id;
    private int userId;  // Changed from String name to int userId
    private double amount;
    private String date;
    private int categoryId;

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }

    // Constructors
    public Expense() {}

    public Expense(int id, int userId, double amount, String date, int categoryId) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}