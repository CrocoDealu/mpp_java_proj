package org.example.rest;

import org.example.model.Cashier;
import org.example.model.Game;
import org.example.service.SportsTicketManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/tickets")
public class SportsTicketsController {
    private final SportsTicketManagementService sportsTicketManagementService;

    @Autowired
    public SportsTicketsController(SportsTicketManagementService sportsTicketManagementService) {
        this.sportsTicketManagementService = sportsTicketManagementService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> cashiers = StreamSupport.stream(sportsTicketManagementService.getAllGames().spliterator(), false).toList();
        return ResponseEntity.ok(cashiers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Integer id) {
        Optional<Game> game = sportsTicketManagementService.getGameById(id);
        return game.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game savedGame = sportsTicketManagementService.saveGame(game);
        return ResponseEntity.ok(savedGame);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Integer id, @RequestBody Game game) {
        System.out.println("Update game: " + game);
        game.setId(id);
        Game updatedGame = sportsTicketManagementService.updateGame(game);
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Game> deleteGame(@PathVariable Integer id) {
        if (sportsTicketManagementService.deleteGame(id).isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
