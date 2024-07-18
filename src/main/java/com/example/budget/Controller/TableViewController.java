package com.example.budget.Controller;

import com.example.budget.Entity.Expense;
import com.example.budget.Entity.User;
import com.example.budget.Repository.ExpenseRepository;
import com.example.budget.utils.DbConnect;
import com.example.budget.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TableViewController implements Initializable {
    public PieChart pieChart;
    public BarChart barChart;
    public Button open_balance_form;
    @FXML
    private TableView<Expense> tableV;
    @FXML
    private TableColumn<Expense, Integer> expense_id;
    @FXML
    private TableColumn<Expense, Integer> user_id;
    @FXML
    private TableColumn<Expense, Double> amount;
    @FXML
    private TableColumn<Expense, Integer> category_id;
    @FXML
    private TableColumn<Expense, String> date;
    @FXML
    private TableColumn<Expense, Void> actions;
    private static TableViewController instance;
    private ExpenseRepository expenseRepository = new ExpenseRepository();
    private ObservableList<Expense> listExpense = FXCollections.observableArrayList();

    public TableViewController() {
        instance = this; // Store the instance when created
    }
    public static TableViewController getInstance() {
        return instance;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeColumns();
        setupActionsColumn();
        loadDataForCurrentUser();
        pieChart.setPrefSize(780, 400);
        barChart.setPrefSize(380, 400);
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            showErrorAlert("Authentication Error", "No user logged in. Please log in first.");
            return;
        }

        populatePieChart(currentUser);
        populateBarChart(currentUser);

    }

    private void initializeColumns() {
        expense_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        user_id.setCellValueFactory(new PropertyValueFactory<>("userId"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        category_id.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    @FXML
    private void close(MouseEvent event) {
        // Implement close functionality here
    }

    @FXML
    void refreshTable() {
        loadDataForCurrentUser();
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            showErrorAlert("Authentication Error", "No user logged in. Please log in first.");
            return;
        }

        populatePieChart(currentUser);
        populateBarChart(currentUser);
    }

    private void loadDataForCurrentUser() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser == null) {
            showErrorAlert("Authentication Error", "No user logged in. Please log in first.");
            return;
        }

        listExpense.clear();
        String query = "SELECT * FROM expenses WHERE user_id = ?";

        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    listExpense.add(new Expense(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getDouble("amount"),
                            resultSet.getString("date"),
                            resultSet.getInt("category_id")
                    ));
                }
            }
            tableV.setItems(listExpense);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load data from the database.");
        }
    }

    private void setupActionsColumn() {
        actions.setCellFactory(param -> new TableCell<>() {
            private final FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE_ALT);
            private final FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);

            {
                editIcon.getStyleClass().add("edit-icon");
                editIcon.setOnMouseClicked(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    editExpense(expense);
                });

                deleteIcon.getStyleClass().add("delete-icon");
                deleteIcon.setOnMouseClicked(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    deleteExpense(expense);

                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editIcon, deleteIcon);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void editExpense(Expense expense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/budget/EditForm.fxml"));
            Parent root = loader.load();

            EditFormController controller = loader.getController();
            controller.setExpense(expense);

            Stage stage = new Stage();
            stage.setTitle("Edit Expense");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            StatsController statsController = StatsController.getInstance();
            if (statsController != null) {
                statsController.refreshStats();
            }
            // Set up a listener for when the stage is closed
            stage.setOnHidden(event -> {
                refreshTable();
                updateCharts();
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to open edit form.");
        }
    }

    private void updateCharts() {
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            populatePieChart(currentUser);
            populateBarChart(currentUser);
        }
        StatsController statsController = StatsController.getInstance();
        if (statsController != null) {
            statsController.refreshStats();
        }
    }

    private void deleteExpense(Expense expense) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Expense");
        confirmAlert.setContentText("Are you sure you want to delete this expense?");

        confirmAlert.showAndWait().ifPresent(response -> {
            StatsController statsController = StatsController.getInstance();

            if (response == ButtonType.OK) {
                expenseRepository.delete(expense);
                listExpense.remove(expense);
                tableV.refresh();
                System.out.println("Expense with id = " + expense.getId() + " deleted successfully.");
                updateCharts();
                if (statsController != null) {
                    statsController.refreshStats();
                }
            }
        });
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void populatePieChart(User currentUser) {
        String query = "SELECT c.name, SUM(e.amount) as total FROM expenses e " +
                "JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ? GROUP BY c.id, c.name";

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString("name"), rs.getDouble("total")));
            }

            pieChart.setData(pieChartData);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to fetch pie chart data from the database.");
        }
    }

    private void populateBarChart(User currentUser) {
        String query = "SELECT DATE_FORMAT(date, '%Y-%m') as month, SUM(amount) as total " +
                "FROM expenses WHERE user_id = ? " +
                "GROUP BY DATE_FORMAT(date, '%Y-%m') " +
                "ORDER BY month DESC LIMIT 6";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Expenses");

        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, currentUser.getId());
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("month"), rs.getDouble("total")));
            }

            ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList(series);
            barChart.setData(barChartData);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to fetch bar chart data from the database.");
        }
    }


    public void openBalance(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/budget/add-balance.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Balance Form");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to open balance form.");
        }
    }
}