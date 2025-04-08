package org.example.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.dto.ClientFilterDTO;
import org.example.dto.TicketDTO;
import org.example.network.ConnectionManager;
import org.example.network.FrontendClient;
import org.example.network.JSONDispatcher;
import org.example.network.ResponseParser;
import org.example.util.Listener;
import org.json.JSONObject;


public class TicketsController implements Listener {
    public TextField nameField;
    public TextField addressField;
    public TableView<TicketDTO> ticketsTable;
    public Button searchButton;
    public TableColumn<TicketDTO, String> clientNameColumn;
    public TableColumn<TicketDTO, String> clientAddressColumn;
    public TableColumn<TicketDTO, String> matchColumn;
    public TableColumn<TicketDTO, Integer> seatsColumn;
    private ObservableList<TicketDTO> ticketList = FXCollections.observableArrayList();

    public TicketsController() {
    }

    public void initialize() {
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        clientAddressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerAddress()));
        matchColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTeam1() + " vs " + cellData.getValue().getGame().getTeam2()));
        seatsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNoOfSeats()).asObject());
        ticketsTable.setItems(ticketList);
    }

    public void initializeResources() {
        ConnectionManager.getDispatcher().onEvent("TICKETS", message-> {
            loadTickets(null);
        });
    }

    public void onSearchPressed(ActionEvent actionEvent) {
        String clientName = nameField.getText();
        String clientAddress = addressField.getText();

        loadTickets(new ClientFilterDTO(clientName, clientAddress));
    }

    public void loadTickets(ClientFilterDTO clientFilterDTO) {
        JSONObject request = new JSONObject();
        request.put("type", "GET_TICKETS");
        request.put("messageId", ConnectionManager.getNextMessageId());
        JSONObject params = new JSONObject();
        if (clientFilterDTO != null) {
            params.put("username", clientFilterDTO.getName());
            params.put("address", clientFilterDTO.getAddress());
        }
        request.put("payload", params);
        FrontendClient frontendClient = ConnectionManager.getClient();

        try {
            frontendClient.send(request.toString());
            ConnectionManager.getDispatcher().addPendingRequest(request).thenAccept(message -> {;
                try {
                    Object response = ConnectionManager.getResponseParser().handleResponse(message.toString());
                    if (response instanceof Iterable<?>) {
                        Iterable<TicketDTO> itTickets = (Iterable<TicketDTO>) response;
                        ticketList.clear();
                        for (TicketDTO ticket : itTickets) {
                            ticketList.add(ticket);
                        }
                    } else {
                        throw new RuntimeException("Unexpected response: " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate(String updateType) {
        if (updateType.equals("TICKETS")) {
            loadTickets(null);
        }
    }
}
