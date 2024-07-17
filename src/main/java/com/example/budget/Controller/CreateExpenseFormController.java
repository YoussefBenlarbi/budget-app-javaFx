package com.example.budget.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.example.budget.Entity.User;
import com.example.budget.utils.DbConnect;
import com.example.budget.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.scene.control.Alert;

public class CreateExpenseFormController {

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<Pair<Integer, String>> categoryComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private void initialize() {
        populateCategoryComboBox();
    }

    private void populateCategoryComboBox() {
        String query = "SELECT id, name FROM categories";
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categoryComboBox.getItems().add(new Pair<>(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load categories.");
        }

        categoryComboBox.setConverter(new StringConverter<Pair<Integer, String>>() {
            @Override
            public String toString(Pair<Integer, String> pair) {
                return pair.getValue();
            }

            @Override
            public Pair<Integer, String> fromString(String string) {
                return null; // Not needed for combo box selection
            }
        });
    }

    @FXML
    private void handleAddExpense() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            showErrorAlert("Authentication Error", "No user logged in. Please log in first.");
            return;
        }

        if (!validateInput()) {
            return;
        }

        double amount = Double.parseDouble(amountField.getText());
        Pair<Integer, String> selectedCategory = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();

        String query = "INSERT INTO expenses (user_id, amount, date, category_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnect.getConnect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentUser.getId());
            pstmt.setDouble(2, amount);
            pstmt.setDate(3, java.sql.Date.valueOf(date));
            pstmt.setInt(4, selectedCategory.getKey());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
//                showInfoAlert("Success", "Expense added successfully.");
                clearForm();

                // Get the instance of TableViewController and refresh the table
                TableViewController tableViewController = TableViewController.getInstance();
                if (tableViewController != null) {
                    tableViewController.refreshTable();
                }
                StatsController statsController = StatsController.getInstance();
                if (statsController != null) {
                    statsController.refreshStats();
                }
            } else {
                showErrorAlert("Error", "Failed to add expense.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to add expense to database.");
        }
    }

    private boolean validateInput() {
        if (amountField.getText().isEmpty()) {
            showErrorAlert("Input Error", "Please enter an amount.");
            return false;
        }

        try {
            Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Please enter a valid number for amount.");
            return false;
        }

        if (categoryComboBox.getValue() == null) {
            showErrorAlert("Input Error", "Please select a category.");
            return false;
        }

        if (datePicker.getValue() == null) {
            showErrorAlert("Input Error", "Please select a date.");
            return false;
        }

        return true;
    }

    private void clearForm() {
        amountField.clear();
        categoryComboBox.setValue(null);
        datePicker.setValue(null);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
