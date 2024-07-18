package com.example.budget.Controller;

import com.example.budget.Entity.User;
import com.example.budget.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private BorderPane register_form;

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Button submit_button;

    private UserRepository userRepository;

    public RegisterController() {
        this.userRepository = new UserRepository();
    }

    @FXML
    public void onSubmitButtonClick(ActionEvent event) {
        String usernameText = username.getText().trim();
        String emailText = email.getText().trim();
        String passwordText = password.getText();

        // Basic input validation
        if (usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "All fields are required.");
            return;
        }

        // Create a new User object
        User newUser = new User();
        newUser.setUsername(usernameText);
        newUser.setEmail(emailText);
        newUser.setPassword(passwordText);

        // Attempt to register the user
        boolean registrationSuccessful = userRepository.register(newUser);

        if (registrationSuccessful) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "You have been successfully registered!");
            openAuthView();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username or email may already be in use.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) register_form.getScene().getWindow();
        stage.close();
    }

    private void openAuthView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/budget/auth-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) register_form.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open auth view: " + e.getMessage());
        }
    }
}
