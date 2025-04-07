package org.example.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.dto.ClientFilterDTO;
import org.example.dto.TicketDTO;
import org.example.network.BackendClient;
import org.example.network.ResponseHandler;
import org.json.JSONObject;


public class TicketsController {
    public TextField nameField;
    public TextField addressField;
    public TableView<TicketDTO> ticketsTable;
    public Button searchButton;
    public TableColumn<TicketDTO, String> clientNameColumn;
    public TableColumn<TicketDTO, String> clientAddressColumn;
    public TableColumn<TicketDTO, String> matchColumn;
    public TableColumn<TicketDTO, Integer> seatsColumn;
    private ObservableList<TicketDTO> ticketList = FXCollections.observableArrayList();

    private ResponseHandler responseHandler;
    private BackendClient backendClient;

    public TicketsController() {
    }

    public TicketsController(ResponseHandler responseHandler, BackendClient backendClient) {
        this.responseHandler = responseHandler;
        this.backendClient = backendClient;
    }

    public void initialize() {
        clientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        clientAddressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerAddress()));
        matchColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGame().getTeam1() + " vs " + cellData.getValue().getGame().getTeam2()));
        seatsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNoOfSeats()).asObject());
        ticketsTable.setItems(ticketList);
    }

    public void onSearchPressed(ActionEvent actionEvent) {
        String clientName = nameField.getText();
        String clientAddress = addressField.getText();

        loadTickets(new ClientFilterDTO(clientName, clientAddress));
    }

    public void loadTickets(ClientFilterDTO clientFilterDTO) {
        JSONObject request = new JSONObject();
        request.append("type", "GET_TICKETS");
        backendClient.send(request.toString());
        try {
            String jsonResponse = backendClient.receive();
            if (jsonResponse == null) {
                throw new RuntimeException("No response");
            }
            Object response = responseHandler.handleResponse(jsonResponse);
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
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }
    public void setBackendClient(BackendClient backendClient) {
        this.backendClient = backendClient;
    }
}
