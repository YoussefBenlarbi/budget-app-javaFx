package com.example.budget.Controller;

import com.example.budget.Entity.Balance;
import com.example.budget.Entity.Expense;
import com.example.budget.Entity.User;
import com.example.budget.utils.DbConnect;
import com.example.budget.utils.UserSession;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StatsController implements Initializable {
    public Text totalBalance;
    public Text Expenses;
    public Text numberTransactions;
    public Text housingEtFood;
    public Text transportationEtUtilities;
    private static StatsController instance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setInstance(this);
        refreshStats();
    }

    public static void setInstance(StatsController instance) {
        StatsController.instance = instance;
    }

    public static StatsController getInstance() {
        return instance;
    }

    public void refreshStats() {
        Double currentBalance = getCurrentBalance();
        Double totalExpenses = getTotalExpenses();
        int countTransactions = getCountTransactions();
        int housingAndFood = getHousingAndFood();
        int transportationAndUtilities = getTransportationAndUtilities();

        if (currentBalance != null) {
            // Format the balance to two decimal places
            String formattedBalance = String.format("%.2f", currentBalance);
            totalBalance.setText(formattedBalance);
        } else {
            totalBalance.setText("N/A");
        }

        if (totalExpenses != null) {
            // Format the expenses to two decimal places
            String formattedExpenses = String.format("%.2f", totalExpenses);
            Expenses.setText(formattedExpenses);
        } else {
            Expenses.setText("N/A");
        }

        numberTransactions.setText(String.valueOf(countTransactions));
        housingEtFood.setText(String.valueOf(housingAndFood));
        transportationEtUtilities.setText(String.valueOf(transportationAndUtilities));
    }

    public Double getCurrentBalance() {
        List<Balance> balances = new ArrayList<>();
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            return null;
        }
        String query = "SELECT * FROM balances WHERE user_id = ?";

        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    balances.add(new Balance(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getDouble("amount")
                    ));
                }
            }
            double totalBalance = 0.0;
            for (Balance balance : balances) {
                totalBalance += balance.getAmount();
            }
            return totalBalance;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getTotalExpenses() {
        List<Expense> expenses = new ArrayList<>();
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            return null;
        }
        String query = "SELECT * FROM expenses WHERE user_id = ?";

        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    expenses.add(new Expense(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getDouble("amount"),
                            resultSet.getString("date"),
                            resultSet.getInt("category_id")
                    ));
                }
            }
            double totalExpenses = 0.0;
            for (Expense expense : expenses) {
                totalExpenses += expense.getAmount();
            }
            return totalExpenses;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCountTransactions() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            return 0;
        }

        String query = "SELECT COUNT(*) FROM expenses WHERE user_id = ?";

        return mappingData(currentUser, query);
    }

    public int getHousingAndFood() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            return 0;
        }

        String query = "SELECT SUM(amount) FROM expenses WHERE user_id = ? AND (category_id = 1 OR category_id = 3)";

        return mappingData(currentUser, query);
    }

    public int getTransportationAndUtilities() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            return 0;
        }

        String query = "SELECT SUM(amount) FROM expenses WHERE user_id = ? AND (category_id = 2 OR category_id = 5)";

        return mappingData(currentUser, query);
    }

    private int mappingData(User currentUser, String query) {
        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
