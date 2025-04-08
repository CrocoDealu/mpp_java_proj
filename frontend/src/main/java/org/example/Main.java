package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import javafx.application.Application;
import org.example.network.JSONDispatcher;
import org.example.network.ResponseParser;

import java.io.IOException;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/login.fxml"));
        try {

            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}