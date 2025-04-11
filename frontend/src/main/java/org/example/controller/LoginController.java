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
import org.example.dto.Cashier;
import org.example.network.FrontendClient;
import org.example.network.ConnectionManager;
import org.json.JSONObject;

public class LoginController {


    public TextField username;
    public TextField password;
    public Label usernameErrorLabel;
    public Label passwordErrorLabel;

    public Button login;

    public LoginController() {
    }

    public void onLogin(ActionEvent actionEvent) {
        clearFields();

        String username = this.username.getText();
        String password = this.password.getText();

        JSONObject request = new JSONObject();
        JSONObject requestPayload = new JSONObject();
        requestPayload.put("username", username);
        requestPayload.put("password", password);
        request.put("type", "LOGIN");
        request.put("payload", requestPayload);
        request.put("messageId", ConnectionManager.getNextMessageId());
        FrontendClient frontendClient = ConnectionManager.getClient();
        try {
            frontendClient.send(request.toString());
            ConnectionManager.getDispatcher()
                    .addPendingRequest(request)
                    .thenAccept(response -> {
                        Object responseHandled = ConnectionManager.getResponseParser().handleResponse(response.toString());
                        if (responseHandled instanceof Pair<?,?> responsePair) {
                            String reason = (String) responsePair.getValue();
                            Cashier cashier = (Cashier) responsePair.getKey();
                            if (reason.equals("USER_NOT_FOUND")) {
                                usernameErrorLabel.setText("Username not found");
                            } else if (reason.equals("INCORRECT_PASSWORD")) {
                                passwordErrorLabel.setText("Wrong password!");
                            } else {
                                openMainPannel(cashier);
                            }
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openMainPannel(Cashier cashier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/main_view.fxml"));
            Parent root = loader.load();
            Scene mainScene = new Scene(root);
            Stage mainStage = new Stage();
            mainStage.setTitle("Basketball match handler");
            mainStage.setScene(mainScene);
            mainStage.show();
            MainController mainController = loader.getController();
            mainController.initializeResources();
            mainController.loadMatches();
            mainController.setCashier(cashier);
            mainStage.setOnCloseRequest(event -> {
                event.consume();
                mainController.shutdown();
            });
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
}
