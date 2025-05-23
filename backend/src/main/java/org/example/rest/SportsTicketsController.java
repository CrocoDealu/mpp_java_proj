package org.example.rest;

import org.example.model.Cashier;
import org.example.model.Game;
import org.example.service.SportsTicketManagementService;
import org.example.utils.GameEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/games")
public class SportsTicketsController {
    private final SportsTicketManagementService sportsTicketManagementService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SportsTicketsController(SportsTicketManagementService sportsTicketManagementService, SimpMessagingTemplate messagingTemplate) {
        this.sportsTicketManagementService = sportsTicketManagementService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = StreamSupport.stream(sportsTicketManagementService.getAllGames().spliterator(), false).toList();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Integer id) {
        Optional<Game> game = sportsTicketManagementService.getGameById(id);
        return game.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game savedGame = sportsTicketManagementService.saveGame(game);
        messagingTemplate.convertAndSend("/topic/games", new GameEvent("CREATED", savedGame));
        return ResponseEntity.ok(savedGame);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Integer id, @RequestBody Game game) {
        if (Objects.equals(id, game.getId())) {
            game.setId(id);
            Game updatedGame = sportsTicketManagementService.updateGame(game);
            messagingTemplate.convertAndSend("/topic/games", new GameEvent("UPDATED", updatedGame));
            return ResponseEntity.ok(updatedGame);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Game> deleteGame(@PathVariable Integer id) {
        Optional<Game> deletedGame = sportsTicketManagementService.deleteGame(id);
        deletedGame.ifPresent(game -> messagingTemplate.convertAndSend("/topic/games", new GameEvent("DELETED", game)));
        return deletedGame.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}