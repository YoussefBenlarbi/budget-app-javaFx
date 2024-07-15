package com.example.budget.Controller;

import com.example.budget.Entity.Expense;
import com.example.budget.Repository.ExpenseRepository;
import com.example.budget.utils.DbConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private ExpenseRepository expenseRepository = new ExpenseRepository();

    private ObservableList<Expense> listExpense = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeColumns();
        setupActionsColumn();
        loadData();
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
    private void refreshTable() {
        loadData();
    }

    @FXML
    private void editExpense() {
        Expense selectedExpense = tableV.getSelectionModel().getSelectedItem();
        if (selectedExpense != null) {
            // Implement edit functionality using selectedExpense.getId()
            System.out.println("Editing expense with ID: " + selectedExpense.getId());
        } else {
            showErrorAlert("Selection Error", "Please select an expense to edit.");
        }
    }

    @FXML
    private void deleteExpense() {
        Expense selectedExpense = tableV.getSelectionModel().getSelectedItem();
        if (selectedExpense != null) {
            int expenseId = selectedExpense.getId();
            // Implement delete functionality using expenseId
            System.out.println("Deleting expense with ID: " + expenseId);
            // Remove from list and update table
            listExpense.remove(selectedExpense);
        } else {
            showErrorAlert("Selection Error", "Please select an expense to delete.");
        }
    }

    private void loadData() {
        listExpense.clear();
        String query = "SELECT * FROM expenses";

        try (Connection connection = DbConnect.getConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                listExpense.add(new Expense(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("date"),
                        resultSet.getInt("category_id")
                ));
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
//                deleteIcon.setStyle("-fx-fill: red; -fx-font-size: 20px;");
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
            stage.showAndWait();

            // Refresh the table after editing
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteExpense(Expense expense) {
        // Implement delete functionality
        System.out.println("Deleting expense with ID: " + expense.getId());
        expenseRepository.delete(expense);
        System.out.println("Expense having  id = "+expense.getId()+ " is deleted successfully.");
        listExpense.remove(expense);
        tableV.refresh();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
