package com.controllers;

import java.io.IOException;

import com.DBConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainPageController {

    private String master;
    private String mPassword;

    @FXML
    private Button logOutBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button generatorBtn;
    @FXML
    private Button vaultBtn;
    @FXML
    private Label titleLabel;

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logOutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public void setMasterPassword(String password) {
        this.mPassword = password;
    }

    @FXML
    private void openVault(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vault.fxml"));
            Parent root = loader.load();

            VaultController vaultController = loader.getController();
            vaultController.setMasterUsername(master);
            vaultController.setMasterPassword(mPassword);

            Stage vaultStage = new Stage();
            vaultStage.setTitle("Vault");
            vaultStage.setScene(new Scene(root));
            vaultStage.initOwner((Stage) vaultBtn.getScene().getWindow());
            vaultStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            vaultStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void addNew(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewLogIn.fxml"));
            Parent root = loader.load();
            NewLogInController controller = loader.getController();
            controller.setMaster(master);
            controller.setMasterPassword(mPassword);
            Stage stage = (Stage) addBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openGenerator(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Generator.fxml"));
            Parent root = loader.load();
            GeneratorController generatorController = loader.getController();

            Stage currentStage = (Stage) generatorBtn.getScene().getWindow();
            generatorController.setMainPageStage(currentStage);
            generatorController.setMaster(master);
            generatorController.setMasterPassword(mPassword);

            Stage generatorStage = new Stage();
            generatorStage.setTitle("Password Generator");
            generatorStage.setScene(new Scene(root));
            generatorStage.setResizable(false);
            generatorStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void closeAccount(ActionEvent e) {
        try {
            DBConnector conn = new DBConnector();
            conn.deleteMaster(master, mPassword);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logOutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
