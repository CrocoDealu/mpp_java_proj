package org.example;

import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;

public class GameRestClient {

    private final RestClient restClient;
    private final String baseUrl;

    public GameRestClient(String baseUrl) {
        this.restClient = RestClient.create();
        this.baseUrl = baseUrl;
    }

    // Get all games
    public void getGames() {
        String url = baseUrl + "/tickets";
        try {
            String response = restClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            System.out.println("GET all games | Response Body: " + response);
        } catch (Exception e) {
            System.err.println("GET all games | Error: " + e.getMessage());
        }
    }

    // Get one game by ID (ID = 5)
    public void getOneGame() {
        String url = baseUrl + "/tickets/5";
        try {
            String response = restClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            System.out.println("GET game with ID 5 | Response Body: " + response);
        } catch (Exception e) {
            System.err.println("GET game with ID 5 | Error: " + e.getMessage());
        }
    }

    // Add a new game
    public String addGame(String newGameJson) {
        String url = baseUrl + "/tickets";
        try {
            String response = restClient
                    .post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .body(newGameJson)
                    .retrieve()
                    .body(String.class);

            System.out.println("POST add new game | Response Body: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("POST add new game | Error: " + e.getMessage());
        }
        return null;
    }

    // Update an existing game
    public void updateGame(int id, String updatedGameJson) {
        String url = baseUrl + "/tickets/" + id;
        try {
            restClient
                    .put()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .body(updatedGameJson)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("PUT update game with ID " + id + " | Status: Success");
        } catch (Exception e) {
            System.err.println("PUT update game with ID " + id + " | Error: " + e.getMessage());
        }
    }

    // Delete a game by ID
    public void deleteGame(int id) {
        String url = baseUrl + "/tickets/" + id;
        try {
            restClient
                    .delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("DELETE game with ID " + id + " | Status: Success");
        } catch (Exception e) {
            System.err.println("DELETE game with ID " + id + " | Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Base URL for the API
        String baseUrl = "http://localhost:8080/api";

        // Initialize the GameRestClient
        GameRestClient client = new GameRestClient(baseUrl);

        // Test the methods
        client.getGames(); // Get all games
        client.getOneGame(); // Get game with ID 5

        // Add a new game
        String newGameJson = "{ \"team1\": \"Rapid\", \"team2\": \"U Cluj\", \"team1Score\": 3, \"team2Score\": 0, \"competition\": \"Supercupa\", \"capacity\": 100, \"stage\": \"Finals\", \"ticketPrice\": 25.99 }";
        client.addGame(newGameJson);

        // Update a game by ID
        String updatedGameJson = "{ \"id\": 5, \"team1\": \"Rapid\", \"team2\": \"U Cluj\", \"team1Score\": 3, \"team2Score\": 1, \"competition\": \"Supercupa\", \"capacity\": 120, \"stage\": \"Finals\", \"ticketPrice\": 30.99 }";
        client.updateGame(5, updatedGameJson);

        // Delete a game by ID
        client.deleteGame(5);
    }
}