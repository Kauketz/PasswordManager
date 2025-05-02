package com;

import com.controllers.AppController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image icon = new Image("/images/key.png");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App.fxml"));
        Parent root = loader.load();
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Password Manager");
        AppController controller = loader.getController();
        controller.setHostServices(getHostServices());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
