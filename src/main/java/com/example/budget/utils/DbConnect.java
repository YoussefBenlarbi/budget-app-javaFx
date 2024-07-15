package com.example.budget.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnect {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/budget-app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static Connection conn;

    public DbConnect() {
        // Private constructor to prevent instantiation
    }
    public static Connection getConnect() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, e);
        }
        return conn;
    }
}
