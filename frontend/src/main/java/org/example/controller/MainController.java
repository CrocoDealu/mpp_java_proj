package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dto.CashierDTO;
import org.example.dto.GameDTO;
import org.example.dto.TicketDTO;
import org.example.network.FrontendClient;
import org.example.network.ConnectionManager;
import org.example.network.ResponseParser;
import org.example.util.Listener;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController implements Listener {
    public ListView<GameDTO> matchList;
    public TextField clientName;
    public TextField clientAddress;
    public Button sellButton;
    public Label forGameLabel;
    public Spinner<Integer> noOfSeats;
    public Button viewTickets;
    public Button logOut;

    private final List<Stage> openedStages = new ArrayList<>();
    private final List<TicketsController> listeners = new ArrayList<>();
    private CashierDTO loggedCashier;
    private GameDTO selectedGame;

    private ResponseParser responseParser;

    public MainController(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    public MainController() {
    }

    public void initialize() {
        matchList.setCellFactory(param -> new ListCell<GameDTO>() {
            @Override
            protected void updateItem(GameDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vBox = new VBox();
                    vBox.setSpacing(10);
                    vBox.setAlignment(Pos.CENTER);
                    String match = item.getTeam1() + " vs " + item.getTeam2();
                    Label gameNameLabel = new Label(match);
                    String price = "Price: " + item.getTicketPrice() + "$";
                    Label priceLabel = new Label(price);
                    HBox.setHgrow(gameNameLabel, Priority.ALWAYS);
                    String noOfSeats = "";
                    Label noOfSeatsLabel = new Label();
                    if (item.getCapacity() != 0) {
                        noOfSeats = "Available Seats: " + item.getCapacity();
                    } else {
                        noOfSeats = "Sold out";
                        noOfSeatsLabel.setStyle("-fx-text-fill: red;");
                    }
                    noOfSeatsLabel.setText(noOfSeats);
                    vBox.getChildren().addAll(gameNameLabel, priceLabel, noOfSeatsLabel);
                    setGraphic(vBox);
                }
            }
        });
        matchList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected: " + newValue.getId());
                selectedGame = newValue;
                forGameLabel.setText(newValue.getTeam1() + " vs " + newValue.getTeam2());
                updateSpinnerMaxValue(selectedGame.getCapacity());
                sellButton.setDisable(selectedGame.getCapacity() == 0);
            }
        });
        noOfSeats.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        sellButton.setDisable(true);
        ConnectionManager.getClient().addListener(this);
    }

    public void loadMatches() {
        matchList.getItems().clear();
        JSONObject request = new JSONObject();
        request.put("type", "GET_GAMES");
        FrontendClient frontendClient = ConnectionManager.getClient();

        try {
            String jsonResponse = frontendClient.sendAndWaitResponse(request);
            if (jsonResponse == null) {
                throw new RuntimeException("No response");
            }
            Object response = responseParser.handleResponse(jsonResponse);
            if (response instanceof Iterable<?>) {
                Iterable<GameDTO> games = (Iterable<GameDTO>) response;
                ObservableList<GameDTO> gameObservableList = FXCollections.observableArrayList();
                games.forEach(gameObservableList::add);

                matchList.setItems(gameObservableList);
            } else {
                throw new RuntimeException("Unexpected response: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sellTicket(ActionEvent actionEvent) {
        boolean canSell = canSellTicket(selectedGame);
        if (canSell) {
            JSONObject request = new JSONObject();
            request.put("type", "SAVE_TICKET");
            JSONObject payload = new JSONObject();
            payload.put("gameId", selectedGame.getId());
            payload.put("clientName", clientName.getText());
            payload.put("clientAddress", clientAddress.getText());
            payload.put("cashierId", loggedCashier.getId());
            payload.put("noOfSeats", noOfSeats.getValue());
            request.put("payload", payload);
            FrontendClient frontendClient = ConnectionManager.getClient();

            try {
                System.out.println("Sending sell ticket request");
                String jsonString = frontendClient.sendAndWaitResponse(request);
                System.out.println("Got sell ticket response" + jsonString);
                JSONObject response = new JSONObject(jsonString);
                if (response.has("payload")) {
                    clearClientInfo();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCashier(CashierDTO cashier) {
        loggedCashier = cashier;
    }

    private void clearClientInfo() {
        clientName.clear();
        clientAddress.clear();
        noOfSeats.getValueFactory().setValue(0);
    }

    private boolean canSellTicket(GameDTO selectedGame) {
        if (selectedGame != null ) {
            boolean clientNameEmpty = clientName.getText().isEmpty();
            boolean clientAddressEmpty = clientAddress.getText().isEmpty();
            boolean noOfSeatsEmpty = noOfSeats.getValue() == 0;
            if (!clientNameEmpty && !clientAddressEmpty && !noOfSeatsEmpty) {
                clientName.setStyle("");
                clientAddress.setStyle("");
                noOfSeats.setStyle("");
                return selectedGame.getCapacity() != 0;
            } else {
                if (clientNameEmpty) {
                    clientName.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                } else {
                    clientName.setStyle("");
                }
                if (clientAddressEmpty) {
                    clientAddress.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                } else {
                    clientAddress.setStyle("");
                }
                if (noOfSeatsEmpty) {
                    noOfSeats.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                } else {
                    noOfSeats.setStyle("");
                }
            }
        }
        return false;
    }

    private void updateSpinnerMaxValue(int newMax) {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) noOfSeats.getValueFactory();
        valueFactory.setMax(newMax);
    }

    public void onViewTicketsPressed(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/tickets_view.fxml"));
            Parent root = loader.load();
            Scene mainScene = new Scene(root);
            TicketsController controller = loader.getController();
            ConnectionManager.getClient().addListener(controller);
            listeners.add(controller);
            controller.loadTickets(null);
            Stage mainStage = new Stage();
            mainStage.setTitle("Tickets");
            mainStage.setScene(mainScene);

            mainStage.setOnCloseRequest(event -> {
                ConnectionManager.getClient().removeListener(controller);
                listeners.remove(controller);
            });
            mainStage.show();
            openedStages.add(mainStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLogOutPressed(ActionEvent actionEvent) {
        try {
            endSession();
            closeAllOpenedStages();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/xmlFiles/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);

            primaryStage.setResizable(false);

            primaryStage.show();


            Stage currentStage = (Stage) clientName.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        endSession();
        closeAllOpenedStages();
        Stage currentStage = (Stage) clientName.getScene().getWindow();
        currentStage.close();
    }

    private static void endSession() {
        FrontendClient frontendClient = ConnectionManager.getClient();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "LOGOUT");
        frontendClient.send(jsonObject.toString());
        try {
            frontendClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeAllOpenedStages() {
        for (Stage stage : openedStages) {
            stage.close();
        }
        listeners.clear();
    }

    public void setResponseHandler(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    @Override
    public void onUpdate(String updateType) {
        if (updateType.equals("GAMES")) {
            loadMatches();
        }
    }
}
