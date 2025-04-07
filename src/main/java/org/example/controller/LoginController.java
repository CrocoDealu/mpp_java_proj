package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Cashier;
import org.example.service.CashierService;
import org.example.service.GameService;
import org.example.service.TicketService;
import javafx.scene.Parent;
import org.example.utils.SpringFXMLLoader;

import java.io.File;
import java.util.Optional;

public class LoginController {


    public TextField username;
    public TextField password;
    public Label usernameErrorLabel;
    public Label passwordErrorLabel;

    private CashierService cashierService;
    private GameService gameService;
    private TicketService ticketService;
    private final SpringFXMLLoader fxmlLoader;

    public Button login;

    public LoginController(GameService gameService, CashierService cashierService, TicketService ticketService, SpringFXMLLoader fxmlLoader) {
        this.cashierService = cashierService;
        this.gameService = gameService;
        this.ticketService = ticketService;
        this.fxmlLoader = fxmlLoader;
    }

    public void onLogin(ActionEvent actionEvent) {
        String username = this.username.getText();
        String password = this.password.getText();
        Optional<Cashier> optionalCashier = cashierService.getCashierByUsername(username);
        if (optionalCashier.isPresent()) {
            Cashier cashier = optionalCashier.get();
            if (password.equals(cashier.getPassword())) {
                openMainPannel(cashier);
            } else {
                clearFields();
                passwordErrorLabel.setText("Wrong password!");
            }
        } else {
            clearFields();
            usernameErrorLabel.setText("Username not found");
        }
    }

    private void openMainPannel(Cashier cashier) {
        try {
            SpringFXMLLoader.ViewControllerPair<MainController> controllerParentPair = this.fxmlLoader.loadWithController("/xmlFiles/main_view.fxml");
            Parent root = controllerParentPair.getView();
            Scene mainScene = new Scene(root);

            Stage mainStage = new Stage();
            mainStage.setTitle("Basketball match handler");
            mainStage.setScene(mainScene);

            mainStage.show();

            MainController mainController = controllerParentPair.getController();
            mainController.loadMatches();
            mainController.setCashier(cashier);
            Stage currentStage = (Stage) username.getScene().getWindow();

            currentStage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
    }

    public void setServices(GameService gameService, CashierService cashierService, TicketService ticketService) {
        this.cashierService = cashierService;
        this.gameService = gameService;
        this.ticketService = ticketService;
    }
}
