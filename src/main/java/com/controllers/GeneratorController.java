package com.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.security.SecureRandom;

public class GeneratorController {

    @FXML
    private TextField suggestedPassword;
    @FXML
    private TextField passwordLength;

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
    private Button regenerateBtn;

    @FXML
    public void initialize() {
        AZCheckBox.setSelected(true);
        azCheckBox.setSelected(true);
        NumCheckBox.setSelected(true);
        specialCheckBox.setSelected(true);
    }

    @FXML
    private void regeneratePassword() {
        // TODO
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

    private String generatePassword(int length) {
        // StringBuilder characterPool = new StringBuilder();
        // if (AZCheckBox.isSelected())
        // characterPool.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // if (azCheckBox.isSelected())
        // characterPool.append("abcdefghijklmnopqrstuvwxyz");
        // if (NumCheckBox.isSelected())
        // characterPool.append("0123456789");
        // if (specialCheckBox.isSelected())
        // characterPool.append("!@#?%$^*");

        // if (characterPool.length() == 0) {
        // return "Select at least one option";
        // }

        // SecureRandom random = new SecureRandom();
        // StringBuilder password = new StringBuilder();

        // for (int i = 0; i < length; i++) {
        // int randIndex = random.nextInt(characterPool.length());
        // password.append(characterPool.charAt(randIndex));
        // }

        // return password.toString();
        return "";
    }
}
