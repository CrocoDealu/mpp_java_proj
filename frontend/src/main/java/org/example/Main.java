package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import javafx.application.Application;
import org.example.network.BackendClient;
import org.example.network.BackendConnectionManager;
import org.example.network.ResponseHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/login.fxml"));

        try {
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setBackendClient(BackendConnectionManager.getClient());
            loginController.setResponseHandler(new ResponseHandler());
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