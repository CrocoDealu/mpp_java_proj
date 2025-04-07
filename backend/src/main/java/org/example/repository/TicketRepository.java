package org.example.repository;

import org.example.dto.ClientFilterDTO;
import org.example.model.Game;
import org.example.model.Ticket;

public interface TicketRepository extends IRepository<Integer, Ticket>{

    public Iterable<Ticket> getTicketsSoldForGame(Game game);
    public Iterable<Ticket> getTicketsForClient(ClientFilterDTO filter);
}
