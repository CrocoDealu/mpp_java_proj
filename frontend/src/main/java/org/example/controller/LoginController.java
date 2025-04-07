package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.dto.CashierDTO;
import org.example.network.FrontendClient;
import org.example.network.ConnectionManager;
import org.example.network.ResponseParser;
import org.json.JSONObject;

import java.util.Optional;

public class LoginController {

    public LoginController() {
    }

    public TextField username;
    public TextField password;
    public Label usernameErrorLabel;
    public Label passwordErrorLabel;

    public Button login;
    private ResponseParser responseParser;

    public LoginController(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    public void onLogin(ActionEvent actionEvent) {
        // Clear error messages before making a new login attempt
        clearFields();
        
        String username = this.username.getText();
        String password = this.password.getText();

        JSONObject request = new JSONObject();
        JSONObject requestPayload = new JSONObject();
        requestPayload.put("username", username);
        requestPayload.put("password", password);
        request.put("type", "LOGIN");
        request.put("payload", requestPayload);
        FrontendClient frontendClient = ConnectionManager.getClient();
        System.out.println("Sending request: ");
        System.out.println("Is client null " + frontendClient.isClosed());
        frontendClient.send(request.toString());
        try {
            String response = frontendClient.receive();
            System.out.println("Received response: " + response);
            Object responseHandled = responseParser.handleResponse(response);
            if (responseHandled instanceof Pair<?,?> responsePair) {
                String reason = (String) responsePair.getValue();
                CashierDTO cashier = (CashierDTO) responsePair.getKey();
                if (reason.equals("USER_NOT_FOUND")) {
                    usernameErrorLabel.setText("Username not found");
                } else if (reason.equals("INCORRECT_PASSWORD")) {
                    passwordErrorLabel.setText("Wrong password!");
                } else {
                    openMainPannel(cashier);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openMainPannel(CashierDTO cashier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/main_view.fxml"));
            Parent root = loader.load();
            Scene mainScene = new Scene(root);

            Stage mainStage = new Stage();
            mainStage.setTitle("Basketball match handler");
            mainStage.setScene(mainScene);

            mainStage.show();

            MainController mainController = loader.getController();
//            mainController.loadMatches();
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

    public void setResponseHandler(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }
}
