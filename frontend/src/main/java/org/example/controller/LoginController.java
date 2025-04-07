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
import org.example.network.BackendClient;
import org.example.network.ResponseHandler;
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
    private ResponseHandler responseHandler;
    private BackendClient backendClient;

    public LoginController(ResponseHandler responseHandler, BackendClient backendClient) {
        this.responseHandler = responseHandler;
        this.backendClient = backendClient;
    }

    public void onLogin(ActionEvent actionEvent) {
        String username = this.username.getText();
        String password = this.password.getText();

        JSONObject request = new JSONObject();
        JSONObject requestPayload = new JSONObject();
        requestPayload.append("username", username);
        requestPayload.append("password", password);
        request.append("type", "LOGIN");
        request.append("payload", requestPayload);
        backendClient.send(request.toString());
        try {
            String response = backendClient.receive();
            Object responseHandled = responseHandler.handleResponse(response);
            if (responseHandled instanceof Pair<?,?> responsePair) {
                JSONObject responseJson = (JSONObject) responsePair.getValue();
                JSONObject responsePayload = (JSONObject) responseJson.get("payload");
                Optional<CashierDTO> optionalCashier = (Optional<CashierDTO>) responsePair.getKey();
                if (optionalCashier.isPresent()) {
                    CashierDTO cashier = optionalCashier.get();
                    if (responsePayload.has("token")) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openMainPannel(CashierDTO cashier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
            Parent root = loader.load();
            Scene mainScene = new Scene(root);

            Stage mainStage = new Stage();
            mainStage.setTitle("Basketball match handler");
            mainStage.setScene(mainScene);

            mainStage.show();

            MainController mainController = loader.getController();
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

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }
    public void setBackendClient(BackendClient backendClient) {
        this.backendClient = backendClient;
    }
}
