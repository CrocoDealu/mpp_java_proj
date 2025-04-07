package org.example.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.example.dto.CashierDTO;
import org.example.dto.ClientFilterDTO;
import org.example.dto.GameDTO;
import org.example.dto.TicketDTO;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ResponseParser {
    public Object handleResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String type = jsonObject.getString("type");
        switch (type) {
            case "LOGIN_RESPONSE":
                return handleLogin(jsonObject);
            case "SAVE_TICKET":
                return handleSaved(jsonObject);
            case "UPDATE_GAME":
                return handleUpdated(jsonObject);
            case "GET_GAMES_RESPONSE":
                return handleGetGames(jsonObject);
            case "GET_TICKETS_RESPONSE":
                return handleGetTickets(jsonObject);
            case "ERROR":
                return "Error: " + jsonObject.getString("message");
            default:
                System.out.println("Unknown response type: " + type);
                return null;
        }
    }

    private Iterable<TicketDTO> handleGetTickets(JSONObject response) {
        JSONObject jsonObject = response.getJSONObject("payload");
        String result = jsonObject.getString("result");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(result, new TypeReference<List<TicketDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Iterable<GameDTO> handleGetGames(JSONObject response) {
        JSONObject jsonObject = response.getJSONObject("payload");
        String result = jsonObject.getString("result");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(result, new TypeReference<List<GameDTO>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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
