export interface Game {
  id?: number;
  team1: string;
  team2: string;
  team1Score: number;
  team2Score: number;
  competition: string;
  capacity: number;
  stage: string;
  ticketPrice: number;
}
