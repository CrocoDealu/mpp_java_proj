package org.example.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.example.dto.Cashier;
import org.example.dto.Game;
import org.example.dto.Ticket;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ResponseParser {
    public Object handleResponse(String response) {
        JSONObject jsonObject = new JSONObject(response);
        String type = jsonObject.getString("type");
        switch (type) {
            case "LOGIN_RESPONSE":
                return handleLogin(jsonObject);
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

    private Iterable<Ticket> handleGetTickets(JSONObject response) {
        JSONObject jsonObject = response.getJSONObject("payload");
        String result = jsonObject.getString("result");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(result, new TypeReference<List<Ticket>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Iterable<Game> handleGetGames(JSONObject response) {
        JSONObject jsonObject = response.getJSONObject("payload");
        String result = jsonObject.getString("result");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(result, new TypeReference<List<Game>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Pair<Cashier, String> handleLogin(JSONObject jsonObject) {
        JSONObject jsonPayload = jsonObject.getJSONObject("payload");
        if (jsonPayload.has("reason")) {
            if (jsonPayload.getString("reason").equals("USER_NOT_FOUND")) {
                return new Pair<>(new Cashier(), "USER_NOT_FOUND");
            } else if (jsonPayload.getString("reason").equals("INCORRECT_PASSWORD")) {
                return new Pair<>(new Cashier(), "INCORRECT_PASSWORD");
            }
        }
        int id = jsonPayload.getInt("id");
        String username = jsonPayload.getString("username");
        String name = jsonPayload.getString("name");
        Cashier cashier = new Cashier(id, username, name);
        return new Pair<>(cashier, "");
    }
}
