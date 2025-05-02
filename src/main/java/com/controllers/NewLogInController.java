package com.controllers;

import java.io.IOException;

import com.DBConnector;
import com.PasswordHandling;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class NewLogInController {

    private String master;
    private String mPassword;

    @FXML
    private ImageView lockImageView;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button addNewBtn;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label titleLabel;

    @FXML
    private Label errorLabel;

    public void setMaster(String master) {
        this.master = master;
    }

    public void setMasterPassword(String password) {
        this.mPassword = password;
    }

    @FXML
    private void cancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void addLogIn() {
        String serviceUsername = username.getText().trim();
        String servicePassword = password.getText().trim();

        if (serviceUsername.isEmpty() || servicePassword.isEmpty()) {
            errorLabel.setText("Username and Password are required.");
            return;
        }

        try {
            DBConnector db = new DBConnector();
            String encryptedPassword = PasswordHandling.encryptServicePassword(servicePassword, mPassword);

            db.addLogIn(master, serviceUsername, encryptedPassword);

            errorLabel.setText("Login saved successfully.");
            username.clear();
            password.clear();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error saving login: " + e.getMessage());
        }
    }

}
