package org.example.service;

import org.example.network.BackendClient;
import org.example.network.ClientManager;
import org.example.repository.CashierRepository;
import org.example.repository.GameRepository;
import org.example.repository.TicketRepository;
import org.example.dto.ClientFilterDTO;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

public class SportsTicketManagementService implements IService<BackendClient> {
    private final CashierRepository cashierRepository;
    private final GameRepository gameRepository;
    private final TicketRepository ticketRepository;
    private final ClientManager clientManager;

    public SportsTicketManagementService(CashierRepository cashierRepository, GameRepository gameRepository, TicketRepository ticketRepository, ClientManager clientManager) {
        this.cashierRepository = cashierRepository;
        this.gameRepository = gameRepository;
        this.ticketRepository = ticketRepository;
        this.clientManager = clientManager;
    }

    public Iterable<Ticket> getTicketsForClient(ClientFilterDTO filter) {
        return ticketRepository.getTicketsForClient(filter);
    }

    public Ticket saveTicket(Ticket ticket) {
        Optional<Game> g = getGameById(ticket.getGame().getId());
        if (g.isPresent()) {
            Game game = g.get();
            game.setCapacity(game.getCapacity() - ticket.getNoOfSeats());
            updateGame(game);
        }
        Ticket savedTicket = ticketRepository.save(ticket);
        JSONObject updateTicketListMessage = new JSONObject();
        updateTicketListMessage.put("type", "TICKETS");
        notifyClients(updateTicketListMessage.toString());
        JSONObject updateGameListMessage = new JSONObject();
        updateGameListMessage.put("type", "GAMES");
        notifyClients(updateGameListMessage.toString());
        return savedTicket;
    }

    public Optional<Cashier> getCashierByUsername(String username) {
        return cashierRepository.findByUsername(username);
    }

    public Game updateGame(Game game) {
        return gameRepository.update(game);
    }

    public Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(int gameId) {
        return gameRepository.findById(gameId);
    }

    public void loginClient(BackendClient client) {
        clientManager.subscribe(client);
    }

    public void logoutClient(BackendClient client) {
        try {
            clientManager.unsubscribe(client);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyClients(String notification) {
        clientManager.notifySubscribers(notification);
    }
}
