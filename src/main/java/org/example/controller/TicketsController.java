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
import org.example.model.Ticket;
import org.example.service.CashierService;
import org.example.service.GameService;
import org.example.service.TicketService;

import java.util.List;

public class TicketsController {
    public TextField nameField;
    public TextField addressField;
    public TableView<Ticket> ticketsTable;
    public Button searchButton;
    public TableColumn<Ticket, String> clientNameColumn;
    public TableColumn<Ticket, String> clientAddressColumn;
    public TableColumn<Ticket, String> matchColumn;
    public TableColumn<Ticket, Integer> seatsColumn;
    private GameService gameService;
    private TicketService ticketService;
    private CashierService cashierService;

    private ObservableList<Ticket> ticketList = FXCollections.observableArrayList();

    public TicketsController(TicketService ticketService) {
        this.ticketService = ticketService;
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
        Iterable<Ticket> itTickets = ticketService.getTicketsForClient(clientFilterDTO);

        ticketList.clear();
        for (Ticket ticket : itTickets) {
            ticketList.add(ticket);
        }
    }
}
