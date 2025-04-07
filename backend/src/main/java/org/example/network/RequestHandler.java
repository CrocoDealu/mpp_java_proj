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
                System.out.println("Login");
                JSONObject jsonPayload = jsonObject.getJSONObject("payload");
                String username = jsonPayload.getString("username");
                String password = jsonPayload.getString("password");
                Optional<Cashier> optionalClient = service.getCashierByUsername(username);
                System.out.println(username);
                if (optionalClient.isPresent()) {
                    String token;
                    if (optionalClient.get().getPassword().equals(password)) {
                        token = String.valueOf(optionalClient.get().getId());
                    } else {
                        token = "";
                    }
                    Cashier cashier = optionalClient.get();
                    JSONObject response = new JSONObject();
                    response.put("type", "LOGIN_RESPONSE");
                    JSONObject payload = new JSONObject();
                    payload.put("token", cashier.getId().toString());
                    payload.put("id", cashier.getId());
                    payload.put("username", cashier.getUsername());
                    payload.put("name", cashier.getName());
                    response.put("payload", payload);
                    client.send(response.toString());
                } else {
                    JSONObject response = new JSONObject();
                    response.put("type", "LOGIN_RESPONSE");
                    JSONObject payload = new JSONObject();
                    payload.put("token", "");
                    payload.put("id", -1);
                    payload.put("username", Optional.empty());
                    client.send(response.toString());
                }
                break;
            case "SAVE_TICKET":
                System.out.println("Saving ticket");
                break;
            case "UPDATE_GAME":
                System.out.println("Updating game");
//                return handleUpdated(jsonObject);
                break;
            case "GET_GAMES":
                System.out.println("Getting games");
                break;
//                return handleGetGames();
            case "GET_TICKETS":
                System.out.println("Getting tickets");
                break;
//                return handleGetTickets();
            case "ERROR":
                System.out.println("Oops got an error");
                break;
//                return "Error: " + jsonObject.getString("message");
            case "LOGOUT":
                System.out.println("Logging out");
                client.close();
                break;
            default:
                System.out.println("Unknown response type: " + type);
                break;
//                return null;
        }
    }

    private Iterable<Ticket> handleGetTickets() {
        return null;
    }

    private Iterable<Game> handleGetGames() {
        return null;
    }

    private String handleUpdated(JSONObject jsonObject) {
        return null;
    }

    private String handleSaved(JSONObject jsonObject) {
        return null;
    }

    private Pair<Optional<Cashier>, JSONObject> handleLogin(JSONObject jsonObject) {
        return null;
    }
}
