package org.example.network;

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

    public void handleRequest(String requestString, BackendClient client) throws IOException {
        try {
            JSONObject request = new JSONObject(requestString);
            JSONObject response = new JSONObject();
            if (request.has("messageId")) {
                response.put("messageId", request.getInt("messageId"));
            }
            String type = request.getString("type");
            switch (type) {
                case "LOGIN":
                    handleLogin(request, client, response);
                    break;
                case "SAVE_TICKET":
                    handleSaveTicket(request, client, response);
                    break;
                case "GET_GAMES":
                    handleGetGames(client, response);
                    break;
                case "GET_TICKETS":
                    ClientFilterDTO filter = getClientFilterDTO(request);
                    handleGetTickets(client, filter, response);
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

    private ClientFilterDTO getClientFilterDTO(JSONObject request) {
        ClientFilterDTO filter = null;
        if (request.has("payload")) {
            filter = parseFilter(request.getJSONObject("payload"));
        }
        return filter;
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

    private void handleLogin(JSONObject request, BackendClient client, JSONObject response) throws IOException {
        JSONObject jsonPayload = request.getJSONObject("payload");
        String username = jsonPayload.getString("username");
        String password = jsonPayload.getString("password");
        Optional<Cashier> optionalCashier = service.getCashierByUsername(username);
        System.out.println("Attempting login");
        if (optionalCashier.isEmpty()) {
            // User not found case
            sendFailedLoginResponse(client, "USER_NOT_FOUND", response);
        } else if (!optionalCashier.get().getPassword().equals(password)) {
            // Incorrect password case
            sendFailedLoginResponse(client, "INCORRECT_PASSWORD", optionalCashier.get().getId(), response);
        } else {
            // Successful login
            Cashier cashier = optionalCashier.get();
            sendSuccessfulLoginResponse(cashier, client, response);
        }
    }
    
    private void sendSuccessfulLoginResponse(Cashier cashier, BackendClient client, JSONObject response) throws IOException {
        response.put("type", "LOGIN_RESPONSE");
        
        JSONObject payload = new JSONObject();
        payload.put("token", cashier.getId().toString());
        payload.put("id", cashier.getId());
        payload.put("username", cashier.getUsername());
        payload.put("name", cashier.getName());
        
        response.put("payload", payload);
        client.send(response.toString());
    }
    
    private void sendFailedLoginResponse(BackendClient client, String reason, JSONObject response) throws IOException {
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
    
    private void sendFailedLoginResponse(BackendClient client, String reason, Integer userId, JSONObject response) throws IOException {
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

    private void handleSaveTicket(JSONObject request, BackendClient client, JSONObject response) throws IOException {
        JSONObject jsonPayload = request.getJSONObject("payload");
        int gameId = jsonPayload.getInt("gameId");
        String clientName = jsonPayload.getString("clientName");
        String clientAddress = jsonPayload.getString("clientAddress");
        int cashierId = jsonPayload.getInt("cashierId");
        int noOfSeats = jsonPayload.getInt("noOfSeats");
        Optional<Game> g = service.getGameById(gameId);
        if (g.isPresent()) {
            Ticket ticket = new Ticket(0, new Game(gameId), clientName, clientAddress, new Cashier(cashierId), noOfSeats);
            service.saveTicket(ticket);
            response.put("type", "SAVE_TICKET_RESPONSE");
            JSONObject payload = new JSONObject();
            payload.put("message", "Ticket saved successfully");
            response.put("payload", payload);
            client.send(response.toString());
        }
    }

    private void handleGetGames(BackendClient client, JSONObject response) throws IOException {
        Iterable<Game> games = service.getAllGames();
        List<Game> gameList = new ArrayList<>();
        games.forEach(gameList::add);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(gameList);
        response.put("type", "GET_GAMES_RESPONSE");
        JSONObject payload = new JSONObject();
        payload.put("result", jsonString);
        response.put("payload", payload);
        client.send(response.toString());
    }

    private void handleGetTickets(BackendClient client, ClientFilterDTO filter, JSONObject response) throws IOException {
        Iterable<Ticket> tickets = service.getTicketsForClient(filter);
        List<Ticket> ticketList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketList.add(ticket);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(ticketList);
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
        service.logoutClient(client);
    }
}
