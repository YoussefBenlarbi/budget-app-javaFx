package com.example.budget.Repository;

import com.example.budget.Entity.Expense;
import com.example.budget.utils.DbConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ExpenseRepository {
    public void save(Expense expense) {
        // Save the expense to the database
    }

    public void update(Expense expense) {
        String updateUserSQL = "UPDATE expenses SET amount = ?, category_id = ?, date = ? WHERE id = ?";
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(updateUserSQL)) {
            preparedStatement.setDouble(1, expense.getAmount());
            preparedStatement.setInt(2, expense.getCategoryId());
            preparedStatement.setString(3, expense.getDate());
            preparedStatement.setInt(4, expense.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Expense expense) {

        String deleteUserSQL = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(deleteUserSQL)) {
            preparedStatement.setInt(1, expense.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Expense select(int id) {
        // Select the expense from the database
        return null;
    }

    public List<Expense> selectAll() {
        // Select all expenses from the database
        return null;
    }

    public List<Expense> selectByUserId(int userId) {
        // Select all expenses for a given user from the database
        return null;
    }

    public List<Expense> selectByCategoryId(int categoryId) {
        // Select all expenses for a given category from the database
        return null;
    }

    public List<Expense> selectByDate(String date) {
        // Select all expenses for a given date from the database
        return null;
    }


}
