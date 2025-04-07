package org.example.repository;

import org.example.model.Game;
import org.example.model.Ticket;

public interface GameRepository extends IRepository<Integer, Game> {
    public Iterable<Game> findGamesForTickets(Iterable<Ticket> tickets);
}
