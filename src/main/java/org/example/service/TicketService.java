package org.example.service;

import org.example.dto.ClientFilterDTO;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.repository.TicketRepository;

import java.util.Optional;

public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Iterable<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> deleteTicketById(Integer id) {
        return ticketRepository.deleteById(id);
    }

    public Ticket updateTicket(Ticket ticket) {
        return ticketRepository.update(ticket);
    }

    public Iterable<Ticket> getTicketsSoldForGame(Game game) {
        return ticketRepository.getTicketsSoldForGame(game);
    }

    public Iterable<Ticket> getTicketsForClient(ClientFilterDTO filter) {
        return ticketRepository.getTicketsForClient(filter);
    }
}
