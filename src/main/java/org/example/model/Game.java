package org.example.model;

import java.util.Objects;

public class Game extends Entity<Integer> {

    private String team1;
    private String team2;
    private int team1Score;
    private int team2Score;
    private String competition;
    private int capacity;
    private String stage;
    private float ticketPrice;

    public Game(Integer integer, String team1, String team2, int team1Score, int team2Score, String competition, int capacity, String stage, float ticketPrice) {
        super(integer);
        this.team1 = team1;
        this.team2 = team2;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.competition = competition;
        this.capacity = capacity;
        this.stage = stage;
        this.ticketPrice = ticketPrice;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id + ", " +
                "team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", team1Score=" + team1Score +
                ", team2Score=" + team2Score +
                ", competition='" + competition + '\'' +
                ", capacity=" + capacity +
                ", stage='" + stage + '\'' +
                ", ticketPrice=" + ticketPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return team1Score == game.team1Score && team2Score == game.team2Score && capacity == game.capacity && Objects.equals(team1, game.team1) && Objects.equals(team2, game.team2) && Objects.equals(competition, game.competition) && Objects.equals(stage, game.stage) && Float.compare(ticketPrice, game.getTicketPrice()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team1, team2, team1Score, team2Score, competition, capacity, stage, ticketPrice);
    }
}
