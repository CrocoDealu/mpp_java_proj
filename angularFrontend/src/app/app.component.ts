import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {GamesComponent} from './components/games/games.component';

@Component({
  selector: 'app-root',
  imports: [GamesComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'angularFrontend';
}
