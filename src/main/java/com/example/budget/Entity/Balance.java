package com.example.budget.Entity;

public class Balance {
    private int id;
    private int userId;  // Changed from String name to int userId
    private double amount;

    public Balance(int id, int userId, double amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Balance {" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                "}\n";
    }

    public Balance() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }
}
