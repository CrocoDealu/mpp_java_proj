package org.example.service;

import org.example.repository.CashierRepository;
import org.example.repository.GameRepository;
import org.example.repository.TicketRepository;
import org.example.dto.ClientFilterDTO;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SportsTicketManagementService {
    private final CashierRepository cashierRepository;
    private final GameRepository gameRepository;
    private final TicketRepository ticketRepository;

    public SportsTicketManagementService(CashierRepository cashierRepository, GameRepository gameRepository, TicketRepository ticketRepository) {
        this.cashierRepository = cashierRepository;
        this.gameRepository = gameRepository;
        this.ticketRepository = ticketRepository;
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
        JSONObject updateGameListMessage = new JSONObject();
        updateGameListMessage.put("type", "GAMES");
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

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public Optional<Game> deleteGame(Integer id) {
        return gameRepository.deleteById(id);
    }
}
