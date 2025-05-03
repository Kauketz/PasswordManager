package com.controllers;

import com.DBConnector;
import com.PasswordHandling;
import com.VaultEntry;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class VaultController {

    @FXML
    private TableView<VaultEntry> vaultTable;
    @FXML
    private TableColumn<VaultEntry, String> domainColumn;
    @FXML
    private TableColumn<VaultEntry, String> usernameColumn;
    @FXML
    private TableColumn<VaultEntry, String> passwordColumn;

    private String masterUsername;
    private String masterPassword;

    @FXML
    public void initialize() {
        passwordColumn.setResizable(true);
        domainColumn.setCellValueFactory(cellData -> cellData.getValue().domainProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        passwordColumn.setCellValueFactory(cellData -> {
            try {
                String encryptedPassword = cellData.getValue().passwordProperty().get();
                String decryptedPassword = PasswordHandling.decryptServicePassword(encryptedPassword, masterPassword);
                return new SimpleStringProperty(decryptedPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return new SimpleStringProperty("Error decrypting");
            }
        });

        loadVaultData();
    }

    public void setWindowSize(double width, double height) {
        Stage stage = (Stage) vaultTable.getScene().getWindow();
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void setMasterUsername(String masterUsername) {
        if (masterUsername == null) {
            System.out.println("Error: Master username is null.");
            return;
        }

        this.masterUsername = masterUsername;
        loadVaultData();
    }

    private void loadVaultData() {
        if (masterUsername == null) {
            System.out.println("Error: Master username is null.");
            return;
        }
        try {
            DBConnector dbConnector = new DBConnector();
            int masterId = dbConnector.getMasterIdByUsername(masterUsername);
            List<VaultEntry> vaultEntries = dbConnector.getVaultEntriesByMasterId(masterId);

            ObservableList<VaultEntry> data = FXCollections.observableArrayList(vaultEntries);
            vaultTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void copyUsername() {
        VaultEntry selectedEntry = vaultTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            String username = selectedEntry.getUsername();
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(username);
            clipboard.setContent(content);

        }
    }

    @FXML
    private void copyPassword() {
        VaultEntry selectedEntry = vaultTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            try {
                String encryptedPassword = selectedEntry.passwordProperty().get();
                String decryptedPassword = PasswordHandling.decryptServicePassword(encryptedPassword, masterPassword);

                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(decryptedPassword);
                clipboard.setContent(content);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void delete() {
        VaultEntry selectedEntry = vaultTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this entry?");
            alert.setContentText("This action cannot be undone.");

            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

            if (result == ButtonType.OK) {
                try {
                    DBConnector dbConnector = new DBConnector();
                    dbConnector.deleteVaultEntry(selectedEntry.getDomain(), selectedEntry.getUsername(),
                            masterUsername);

                    vaultTable.getItems().remove(selectedEntry);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        }
    }
}
