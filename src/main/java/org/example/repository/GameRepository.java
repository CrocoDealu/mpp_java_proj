package org.example.repository;

import org.example.dto.ClientFilterDTO;
import org.example.model.Game;
import org.example.model.Ticket;

import java.util.List;

public interface GameRepository extends IRepository<Integer, Game> {
    public Iterable<Game> findGamesForTickets(Iterable<Ticket> tickets);
}
