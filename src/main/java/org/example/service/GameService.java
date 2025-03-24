package org.example.service;

import org.example.model.Game;
import org.example.model.Ticket;
import org.example.repository.GameRepository;

import java.util.Optional;

public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(Integer id) {
        return gameRepository.findById(id);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public Optional<Game> deleteGameById(Integer id) {
        return gameRepository.deleteById(id);
    }

    public Game updateGame(Game game) {
        return gameRepository.update(game);
    }

    public Iterable<Game> getGamesForTickets(Iterable<Ticket> tickets) {
        return gameRepository.findGamesForTickets(tickets);
    }
}
