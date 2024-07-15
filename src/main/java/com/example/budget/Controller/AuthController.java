package com.example.budget.Controller;


import com.example.budget.Entity.User;
import com.example.budget.Repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {
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
//        if (userRepository.login(username.getText(), password.getText()) != null) {
//            welcomeText.setText("Login successful!");
//        } else {
//            welcomeText.setText("Login failed!");
//        }
        User user = userRepository.login(username.getText(), password.getText());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (user != null) {
            System.out.println("Login successful!");
            alert.setTitle("Login successful!!");
            alert.setHeaderText("Welcome " + user.getUsername());
            alert.setContentText("You are logged in as " + user.getUsername());
            alert.showAndWait();
            openNewView();
        } else {
            System.out.println("Login Failed!");
            alert.setTitle("Login failed!");
            alert.setHeaderText("Invalid username or password");
            alert.setContentText("Please try again");
            alert.showAndWait();
        }

    }

    private void openNewView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/budget/layout.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) auth_form.getScene().getWindow(); // Get the current stage
            stage.setScene(scene); // Set the new scene
            stage.show(); // Show the new stage
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}