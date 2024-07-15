package com.example.budget.Controller;

import com.example.budget.Entity.Expense;
import com.example.budget.Repository.ExpenseRepository;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditFormController {
    @FXML private TextField expenseIdField;
    @FXML private TextField userIdField;
    @FXML private TextField amountField;
    @FXML private TextField categoryIdField;
    @FXML private TextField dateField;
    private ExpenseRepository expenseRepository = new ExpenseRepository();
    private Expense expense;
    @FXML
    public void initialize() {
        System.out.println("EditFormController initialized");
    }
    public void setExpense(Expense expense) {
        System.out.println("setExpense called with expense: " + expense);
        this.expense = expense;
        populateForm();
    }

    private void populateForm() {
        expenseIdField.setText(String.valueOf(expense.getId()));
        userIdField.setText(String.valueOf(expense.getUserId()));
        amountField.setText(String.valueOf(expense.getAmount()));
        categoryIdField.setText(String.valueOf(expense.getCategoryId()));
        dateField.setText(expense.getDate());
    }

    @FXML
    private void handleSave() {
        // Update the expense object with the new values
        expense.setUserId(Integer.parseInt(userIdField.getText()));
        expense.setAmount(Double.parseDouble(amountField.getText()));
        expense.setCategoryId(Integer.parseInt(categoryIdField.getText()));
        expense.setDate(dateField.getText());

        // Save the updated expense to the database
        saveExpenseToDatabase(expense);

        // Close the edit window
        ((Stage) expenseIdField.getScene().getWindow()).close();
    }

    private void saveExpenseToDatabase(Expense expense) {
        expenseRepository.update(expense);
        System.out.println("Saving updated expense to the database: " + expense);
    }
}