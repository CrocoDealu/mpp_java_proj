package org.example.network;

import ch.qos.logback.core.joran.sanity.Pair;
import ch.qos.logback.core.net.server.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ClientFilterDTO;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.service.SportsTicketManagementService;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestHandler {
    private SportsTicketManagementService service;

    public RequestHandler(SportsTicketManagementService service) {
        this.service = service;
    }

    public void handleRequest(String request, BackendClient client) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(request);
            String type = jsonObject.getString("type");
            switch (type) {
                case "LOGIN":
                    handleLogin(jsonObject.getJSONObject("payload"), client);
                    break;
                case "SAVE_TICKET":
                    handleSaveTicket(jsonObject.getJSONObject("payload"), client);
                    break;
                case "GET_GAMES":
                    handleGetGames(client);
                    break;
                case "GET_TICKETS":
                    ClientFilterDTO filter = null;
                    if (jsonObject.has("payload")) {
                        filter = parseFilter(jsonObject.getJSONObject("payload"));
                    }
                    handleGetTickets(client, filter);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClientFilterDTO parseFilter(JSONObject jsonObject) {
        String username = "", address = "";
        if (jsonObject.has("username")) {
            username = jsonObject.getString("username");
        }
        if (jsonObject.has("address")) {
            address = jsonObject.getString("address");
        }

        return new ClientFilterDTO(username, address);
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
        int gameId = jsonPayload.getInt("gameId");
        String clientName = jsonPayload.getString("clientName");
        String clientAddress = jsonPayload.getString("clientAddress");
        int cashierId = jsonPayload.getInt("cashierId");
        int noOfSeats = jsonPayload.getInt("noOfSeats");
        Optional<Game> g = service.getGameById(gameId);
        if (g.isPresent()) {
            Game game = g.get();
            game.setCapacity(game.getCapacity() - noOfSeats);
            service.updateGame(game);
            Ticket ticket = new Ticket(0, new Game(gameId), clientName, clientAddress, new Cashier(cashierId), noOfSeats);
            Ticket t = service.saveTicket(ticket);
            JSONObject response = new JSONObject();
            response.put("type", "SAVE_TICKET_RESPONSE");
            JSONObject payload = new JSONObject();
            payload.put("message", "Ticket saved successfully");
            response.put("payload", payload);
            client.send(response.toString());
        }
    }

    private void handleGetGames(BackendClient client) throws IOException {
        Iterable<Game> games = service.getAllGames();
        List<Game> gameList = new ArrayList<>();
        games.forEach(gameList::add);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(gameList);
        JSONObject response = new JSONObject();
        response.put("type", "GET_GAMES_RESPONSE");
        JSONObject payload = new JSONObject();
        payload.put("result", jsonString);
        response.put("payload", payload);
        client.send(response.toString());
    }

    private void handleGetTickets(BackendClient client, ClientFilterDTO filter) throws IOException {
        Iterable<Ticket> tickets = service.getTicketsForClient(filter);
        List<Ticket> ticketList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketList.add(ticket);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(ticketList);
        JSONObject response = new JSONObject();
        response.put("type", "GET_TICKETS_RESPONSE");
        JSONObject payload = new JSONObject();
        payload.put("result", jsonString);
        response.put("payload", payload);
        client.send(response.toString());
    }

    private void handleError() {
        System.out.println("Oops got an error");
    }

    private void handleLogout(BackendClient client) throws IOException {
        System.out.println("Logging out");
        client.close();
    }
}
