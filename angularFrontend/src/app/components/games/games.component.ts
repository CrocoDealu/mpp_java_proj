import { Component } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Game } from '../../models/game.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-games',
  standalone: true, // Standalone component
  imports: [CommonModule, FormsModule], // Import required modules
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.css']
})
export class GamesComponent {
  games: Game[] = [];
  selectedGame: Game | null = null;
  newGame: Game = { team1: '', team2: '', team1Score: 0, team2Score: 0, competition: '', capacity: 0, stage: '', ticketPrice: 0 };

  constructor(private gameService: GameService) {
    this.ngOnInit()
  }

  ngOnInit(): void {
    this.getGames();
  }

  getGames(): void {
    this.gameService.getAllGames().subscribe((data) => {
      this.games = data;
    });
  }

  addGame(): void {
    this.gameService.addGame(this.newGame).subscribe((game) => {
      this.games.push(game);
      this.newGame = { team1: '', team2: '', team1Score: 0, team2Score: 0, competition: '', capacity: 0, stage: '', ticketPrice: 0 };
    });
  }

  selectGame(game: Game): void {
    this.selectedGame = game;
  }

  updateGame(): void {
    if (this.selectedGame) {
      this.gameService.updateGame(this.selectedGame.id!, this.selectedGame).subscribe((updatedGame) => {
        const index = this.games.findIndex((g) => g.id === updatedGame.id);
        if (index !== -1) this.games[index] = updatedGame;
        this.selectedGame = null;
      });
    }
  }

  deleteGame(id: number): void {
    this.gameService.deleteGame(id).subscribe(() => {
      this.games = this.games.filter((game) => game.id !== id);
    });
  }
}
