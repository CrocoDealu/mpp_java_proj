package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.repository.CashierDBRepository;
import org.example.repository.GameDBRepository;
import org.example.repository.TicketDBRepository;
import org.example.service.CashierService;
import org.example.service.GameService;
import org.example.service.TicketService;
import org.example.utils.AppConfig;
import org.example.utils.JdbcUtils;

import javafx.application.Application;
import org.example.utils.SpringFXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application{

    private static ApplicationContext springContext;

    @Override
    public void init() {
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Override
    public void start(Stage primaryStage) {
        SpringFXMLLoader loader = springContext.getBean(SpringFXMLLoader.class);
        loader.setContext(springContext);
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Parent root = loader.load("/xmlFiles/login.fxml");

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