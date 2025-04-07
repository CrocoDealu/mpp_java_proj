package org.example.network;

import ch.qos.logback.core.joran.sanity.Pair;
import ch.qos.logback.core.net.server.Client;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.service.SportsTicketManagementService;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class RequestHandler {
    private SportsTicketManagementService service;

    public RequestHandler(SportsTicketManagementService service) {
        this.service = service;
    }

    public void handleRequest(String request, BackendClient client) throws IOException {
        JSONObject jsonObject = new JSONObject(request);
        String type = jsonObject.getString("type");
        switch (type) {
            case "LOGIN":
                handleLogin(jsonObject.getJSONObject("payload"), client);
                break;
            case "SAVE_TICKET":
                handleSaveTicket(jsonObject.getJSONObject("payload"), client);
                break;
            case "UPDATE_GAME":
                handleUpdateGame(jsonObject.getJSONObject("payload"), client);
                break;
            case "GET_GAMES":
                handleGetGames(client);
                break;
            case "GET_TICKETS":
                handleGetTickets(client);
                break;
            case "ERROR":
                handleError();
                break;
            case "LOGOUT":
                handleLogout(client);
                break;
            default:
                System.out.println("Unknown response type: " + type);
                break;
        }
    }

    private void handleLogin(JSONObject jsonPayload, BackendClient client) throws IOException {
        String username = jsonPayload.getString("username");
        String password = jsonPayload.getString("password");
        Optional<Cashier> optionalCashier = service.getCashierByUsername(username);
        
        if (optionalCashier.isEmpty()) {
            // User not found case
            sendFailedLoginResponse(client, "USER_NOT_FOUND");
        } else if (!optionalCashier.get().getPassword().equals(password)) {
            // Incorrect password case
            sendFailedLoginResponse(client, "INCORRECT_PASSWORD", optionalCashier.get().getId());
        } else {
            // Successful login
            Cashier cashier = optionalCashier.get();
            sendSuccessfulLoginResponse(cashier, client);
        }
    }
    
    private void sendSuccessfulLoginResponse(Cashier cashier, BackendClient client) throws IOException {
        JSONObject response = new JSONObject();
        response.put("type", "LOGIN_RESPONSE");
        
        JSONObject payload = new JSONObject();
        payload.put("token", cashier.getId().toString());
        payload.put("id", cashier.getId());
        payload.put("username", cashier.getUsername());
        payload.put("name", cashier.getName());
        
        response.put("payload", payload);
        client.send(response.toString());
    }
    
    private void sendFailedLoginResponse(BackendClient client, String reason) throws IOException {
        JSONObject response = new JSONObject();
        response.put("type", "LOGIN_RESPONSE");
        
        JSONObject payload = new JSONObject();
        payload.put("token", "");
        payload.put("id", -1);
        payload.put("username", "");
        payload.put("name", "");
        payload.put("reason", reason);
        
        response.put("payload", payload);
        client.send(response.toString());
    }
    
    private void sendFailedLoginResponse(BackendClient client, String reason, Integer userId) throws IOException {
        JSONObject response = new JSONObject();
        response.put("type", "LOGIN_RESPONSE");
        
        JSONObject payload = new JSONObject();
        payload.put("token", "");
        payload.put("id", userId);
        payload.put("username", "");
        payload.put("name", "");
        payload.put("reason", reason);
        
        response.put("payload", payload);
        client.send(response.toString());
    }

    private void handleSaveTicket(JSONObject jsonPayload, BackendClient client) {
        System.out.println("Saving ticket");
        // Implement the logic for saving a ticket
    }

    private void handleUpdateGame(JSONObject jsonPayload, BackendClient client) {
        System.out.println("Updating game");
        // Implement the logic for updating a game
    }

    private void handleGetGames(BackendClient client) {
        System.out.println("Getting games");
        // Implement the logic for getting games
    }

    private void handleGetTickets(BackendClient client) {
        System.out.println("Getting tickets");
        // Implement the logic for getting tickets
    }

    private void handleError() {
        System.out.println("Oops got an error");
    }

    private void handleLogout(BackendClient client) throws IOException {
        System.out.println("Logging out");
        client.close();
    }
}
