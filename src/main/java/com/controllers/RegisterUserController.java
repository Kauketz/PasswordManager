package com.controllers;

import java.io.IOException;
import java.sql.SQLException;

import com.DBConnector;
import com.PasswordHandling;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterUserController {

    @FXML
    private Button cancelBtn;

    @FXML
    private Button registerBtn;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String user = username.getText();
        String pass = password.getText();

        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            DBConnector conn = new DBConnector();
            if (conn.doesUserExist(user)) {
                errorLabel.setText("Username/E-mail already in use.");
                return;
            }

            String hashed = PasswordHandling.hashPassword(pass);
            conn.addMaster(user, hashed);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load the application.");
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to register user: " + e.getMessage());
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
