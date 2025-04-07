package org.example.service;

import org.example.repository.CashierRepository;
import org.example.repository.GameRepository;
import org.example.repository.TicketRepository;
import org.example.dto.ClientFilterDTO;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;

import java.util.List;
import java.util.Optional;

public class SportsTicketManagementService {
    private CashierRepository cashierRepository;
    private GameRepository gameRepository;
    private TicketRepository ticketRepository;

    public SportsTicketManagementService(CashierRepository cashierRepository, GameRepository gameRepository, TicketRepository ticketRepository) {
        this.cashierRepository = cashierRepository;
        this.gameRepository = gameRepository;
        this.ticketRepository = ticketRepository;
    }

    public Iterable<Ticket> getTicketsForClient(ClientFilterDTO filter) {
        return ticketRepository.getTicketsForClient(filter);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
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
}
