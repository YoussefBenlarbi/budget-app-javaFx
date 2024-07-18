package com.example.budget.Controller;

import com.example.budget.Entity.User;
import com.example.budget.utils.DbConnect;
import com.example.budget.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddBalanceController {
    public TextField amountField;

    public void handleAddBalance(ActionEvent actionEvent) {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            showErrorAlert("Authentication Error", "No user logged in. Please log in first.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showErrorAlert("Invalid Input", "Please enter a positive amount.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Input", "Please enter a valid number.");
            return;
        }
        try (Connection conn = DbConnect.getConnect()) {
            String insertBalanceSQL = "INSERT INTO balances (user_id, amount) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertBalanceSQL)) {
                insertStmt.setInt(1, currentUser.getId());
                insertStmt.setDouble(2, amount);
                insertStmt.executeUpdate();

            }
            showSuccessAlert("Success", "Balance added successfully.");
            StatsController statsController = StatsController.getInstance();
            if (statsController != null) {
                statsController.refreshStats();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to update balance: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
