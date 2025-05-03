package com.controllers;

import java.io.IOException;
import java.sql.SQLException;

import com.DBConnector;
import com.PasswordHandling;

import java.net.URI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.HostServices;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AppController {

    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private Button registerBtn;

    @FXML
    private Button logInBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Button aboutBtn;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleRegister(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegisterUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void exitApp(ActionEvent e) {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void aboutApp(ActionEvent e) {
        try {
            URI uri = new URI("https://github.com/Kauketz/pw_v3");
            hostServices.showDocument(uri.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void login(ActionEvent e) throws SQLException {
        String master = username.getText();
        String plainPassword = password.getText();

        DBConnector conn = new DBConnector();

        if (conn.doesUserExist(master)) {
            String hashedPassword = conn.retrieveHashedPassword(master);

            if (PasswordHandling.verifyPassword(plainPassword, hashedPassword)) {

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainPage.fxml"));
                    Parent root = loader.load();
                    MainPageController controller = loader.getController();
                    controller.setMaster(master);
                    controller.setMasterPassword(plainPassword);
                    Stage stage = (Stage) logInBtn.getScene().getWindow();
                    stage.getScene().setRoot(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                errorLabel.setText("Invalid password");
            }
        } else {
            errorLabel.setText("User does not exist");
            ;
        }
    }

}