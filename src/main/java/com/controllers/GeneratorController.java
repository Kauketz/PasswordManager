package com.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.SecureRandom;

public class GeneratorController {
    @FXML
    private TextField suggestedPassword;
    @FXML
    private TextField passwordLength;
    @FXML
    private Label errorLabel;
    @FXML
    private CheckBox AZCheckBox;
    @FXML
    private CheckBox azCheckBox;
    @FXML
    private CheckBox NumCheckBox;
    @FXML
    private CheckBox specialCheckBox;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button copyBtn;
    @FXML
    private Button acceptBtn;
    @FXML
    private Button generateBtn;

    private Stage mainPageStage;
    private String master;
    private String mPassword;

    public void setMainPageStage(Stage stage) {
        this.mainPageStage = stage;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public void setMasterPassword(String password) {
        this.mPassword = password;
    }

    @FXML
    public void initialize() {
        AZCheckBox.setSelected(true);
        azCheckBox.setSelected(true);
        NumCheckBox.setSelected(true);
        specialCheckBox.setSelected(true);
        passwordLength.setText("10");
    }

    @FXML
    private void copyPassword() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(suggestedPassword.getText());
        clipboard.setContent(content);
    }

    @FXML
    private void exitGenerator() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void acceptPassword() throws IOException {
        // First create the loader
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewLogIn.fxml"));
        Parent root = loader.load();

        // Get controller and configure it
        NewLogInController controller = loader.getController();
        controller.setMaster(master);
        controller.setMasterPassword(mPassword);

        // Set the generated password - this will work because the controller is fully
        // initialized
        String generated = suggestedPassword.getText();
        controller.setGeneratedPassword(generated);

        // Update the scene
        mainPageStage.getScene().setRoot(root);

        // Close the generator window
        Stage stage = (Stage) acceptBtn.getScene().getWindow();
        stage.close();
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void generatePassword() {
        if (!isNumeric(passwordLength.getText())) {
            errorLabel.setText("Password length input must be numeric");
            return;
        }

        int length = Integer.parseInt(passwordLength.getText());
        StringBuilder characterPool = new StringBuilder();

        if (AZCheckBox.isSelected())
            characterPool.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        if (azCheckBox.isSelected())
            characterPool.append("abcdefghijklmnopqrstuvwxyz");
        if (NumCheckBox.isSelected())
            characterPool.append("0123456789");
        if (specialCheckBox.isSelected())
            characterPool.append("!@#?%$^*");

        if (characterPool.length() == 0) {
            errorLabel.setText("Select at least one option");
            return;
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randIndex = random.nextInt(characterPool.length());
            password.append(characterPool.charAt(randIndex));
        }

        suggestedPassword.setText(password.toString());
    }
}