package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.service.CashierService;
import org.example.service.GameService;
import org.example.service.TicketService;
import org.example.utils.SpringFXMLLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainController {
    public ListView<Game> matchList;
    public TextField clientName;
    public TextField clientAddress;
    public Button sellButton;
    public Label forGameLabel;
    public Spinner<Integer> noOfSeats;
    public Button viewTickets;
    public Button logOut;

    private GameService gameService;
    private TicketService ticketService;
    private CashierService cashierService;
    private Cashier loggedCashier;
    private SpringFXMLLoader loader;
    private Game selectedGame;

    private final List<Stage> openedStages = new ArrayList<>();

    public MainController(GameService gameService, CashierService cashierService, TicketService ticketService, SpringFXMLLoader loader) {
        this.gameService = gameService;
        this.cashierService = cashierService;
        this.ticketService = ticketService;
        this.loader = loader;
    }

    public void initialize() {
        matchList.setCellFactory(param -> new ListCell<Game>() {
            @Override
            protected void updateItem(Game item, boolean empty) {
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
    }

    public void loadMatches() {
        matchList.getItems().clear();
        Iterable<Game> games = gameService.getAllGames();

        ObservableList<Game> gameObservableList = FXCollections.observableArrayList();
        games.forEach(gameObservableList::add);

        matchList.setItems(gameObservableList);
    }

    public void sellTicket(ActionEvent actionEvent) {
        boolean canSell = canSellTicket(selectedGame);
        if (canSell) {
            System.out.println("Selling ticket for game: " + selectedGame.getTeam1() + " vs " + selectedGame.getTeam2() + " for client: " + clientName.getText());
            Ticket ticket = new Ticket(0, selectedGame, clientName.getText(), clientAddress.getText(), loggedCashier, noOfSeats.getValue());
            ticketService.saveTicket(ticket);
            selectedGame.setCapacity(selectedGame.getCapacity() - noOfSeats.getValue());
            gameService.updateGame(selectedGame);
            loadMatches();
            clearClientInfo();
        }
    }

    public void setCashier(Cashier cashier) {
        loggedCashier = cashier;
    }

    private void clearClientInfo() {
        clientName.clear();
        clientAddress.clear();
        noOfSeats.getValueFactory().setValue(0);
    }

    private boolean canSellTicket(Game selectedGame) {
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
            SpringFXMLLoader.ViewControllerPair<TicketsController> controllerViewPair =  loader.loadWithController("/xmlFiles/tickets_view.fxml");
            Parent root = controllerViewPair.getView();
            Scene mainScene = new Scene(root);
            TicketsController controller = controllerViewPair.getController();
            controller.loadTickets(null);
            Stage mainStage = new Stage();
            mainStage.setTitle("Tickets");
            mainStage.setScene(mainScene);

            mainStage.show();
            openedStages.add(mainStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLogOutPressed(ActionEvent actionEvent) {
        try {
            closeAllOpenedStages();
            Parent root = loader.load("/xmlFiles/login.fxml");

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

    private void closeAllOpenedStages() {
        for (Stage stage : openedStages) {
            stage.close();
        }
    }
}
