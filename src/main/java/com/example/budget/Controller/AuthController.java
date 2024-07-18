package com.example.budget.Controller;


import com.example.budget.Entity.User;
import com.example.budget.Repository.UserRepository;
import com.example.budget.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AuthController {
    public Hyperlink registerLink;
    private UserRepository userRepository = new UserRepository();
    @FXML
    private Label welcomeText;
    @FXML
    private BorderPane auth_form;

    @FXML
    private TextField password;

    @FXML
    private Button submit_button;
    @FXML
    private TextField username;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onSubmitButtonClick() {
        try {
            User user = userRepository.login(username.getText(), password.getText());
            if (user != null) {
                UserSession.getInstance().setUser(user);
                System.out.println("Login successful!");
                openNewView();
            } else {
                // ... (login failure code)
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void openNewView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/budget/new-layout.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) auth_form.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to open new view: " + e.getMessage());
        }
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openRegisterForm() {
        try {
            // Load the register.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/budget/register.fxml"));
            Parent root = loader.load();

            // Create a new stage for the registration form
            Stage registerStage = new Stage();
            registerStage.setTitle("Register");
            registerStage.setScene(new Scene(root));

            // Get the current stage (login window)
            Stage currentStage = (Stage) registerLink.getScene().getWindow();

            // Close the current stage
            currentStage.close();

            // Show the new stage
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}