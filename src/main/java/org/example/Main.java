package org.example;

import javafx.fxml.FXMLLoader;
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
import org.example.utils.JdbcUtils;

import javafx.application.Application;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/login.fxml"));

        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JdbcUtils jdbcUtils = new JdbcUtils(properties);
        GameDBRepository gameDBRepository = new GameDBRepository(jdbcUtils);
        GameService gameService = new GameService(gameDBRepository);
        CashierDBRepository cashierDBRepository = new CashierDBRepository(jdbcUtils);
        CashierService cashierService = new CashierService(cashierDBRepository);
        TicketDBRepository ticketDBRepository = new TicketDBRepository(jdbcUtils, gameDBRepository, cashierDBRepository);
        TicketService ticketService = new TicketService(ticketDBRepository);
        try {
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            LoginController loginController = loader.getController();
            loginController.setServices(gameService, cashierService, ticketService);
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}