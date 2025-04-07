package org.example.network;

import javafx.util.Pair;
import org.example.dto.CashierDTO;
import org.example.dto.ClientFilterDTO;
import org.example.dto.GameDTO;
import org.example.dto.TicketDTO;
import org.json.JSONObject;

import java.util.Optional;

public class ResponseParser {
    public Object handleResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String type = jsonObject.getString("type");
        switch (type) {
            case "LOGIN_RESPONSE":
                System.out.println("Login");
                return handleLogin(jsonObject);
            case "SAVE_TICKET":
                System.out.println("Saving ticket");
                return handleSaved(jsonObject);
            case "UPDATE_GAME":
                System.out.println("Updating game");
                return handleUpdated(jsonObject);
            case "GET_GAMES":
                System.out.println("Getting games");
                return handleGetGames();
            case "GET_TICKETS":
                System.out.println("Getting tickets");
                return handleGetTickets();
            case "ERROR":
                System.out.println("Oops got an error");
                return "Error: " + jsonObject.getString("message");
            default:
                System.out.println("Unknown response type: " + type);
                return null;
        }
    }

    private Iterable<TicketDTO> handleGetTickets() {
        return null;
    }

    private Iterable<GameDTO> handleGetGames() {
        return null;
    }

    private String handleUpdated(JSONObject jsonObject) {
        return null;
    }

    private String handleSaved(JSONObject jsonObject) {
        return null;
    }

    private Pair<CashierDTO, String> handleLogin(JSONObject jsonObject) {
        JSONObject jsonPayload = jsonObject.getJSONObject("payload");
        if (jsonPayload.has("reason")) {
            if (jsonPayload.getString("reason").equals("USER_NOT_FOUND")) {
                return new Pair<>(new CashierDTO(), "USER_NOT_FOUND");
            } else if (jsonPayload.getString("reason").equals("INCORRECT_PASSWORD")) {
                return new Pair<>(new CashierDTO(), "INCORRECT_PASSWORD");
            }
        }
        int id = jsonPayload.getInt("id");
        String username = jsonPayload.getString("username");
        String name = jsonPayload.getString("name");
        CashierDTO cashierDTO = new CashierDTO(id, username, name);
        return new Pair<>(cashierDTO, "");
    }
}
